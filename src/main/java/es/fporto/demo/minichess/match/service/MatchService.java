package es.fporto.demo.minichess.match.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.MoveList;

import es.fporto.demo.minichess.match.model.MatchMessage;
import es.fporto.demo.minichess.match.model.MatchMessage.MessageType;
import es.fporto.demo.minichess.match.service.MoveValidator.MoveValidationResult;
import es.fporto.demo.minichess.model.Challenge;
import es.fporto.demo.minichess.model.Match;
import es.fporto.demo.minichess.model.MatchState;
import es.fporto.demo.minichess.model.Move;
import es.fporto.demo.minichess.model.PieceId.Color;
import es.fporto.demo.minichess.model.TimeControlType;
import es.fporto.demo.minichess.player.Player;
import es.fporto.demo.minichess.repository.MatchRepository;
import es.fporto.demo.minichess.repository.UserRepository;
import es.fporto.demo.minichess.user.model.EloScore;
import es.fporto.demo.minichess.user.model.User;
import es.fporto.demo.minichess.user.service.UserService;

@Service
public class MatchService {

	private static class GameStatus {
		private Board board;
		private MoveList moveList;

		public GameStatus() {
			board = new Board();
			moveList = new MoveList();
		}

		public Board getBoard() {
			return board;
		}

		public MoveList getMoveList() {
			return moveList;
		}

	}
	@Value ("${es.fporto.demo.minichess.match.kFactor}")
	private long K = 32;
	@Value ("${es.fporto.demo.minichess.match.timeToReconBeforeLoss}")
	private int TIME_TO_RECONNECT_BEFORE_LOSS;
	
	public final static String DESTINATION_FORMAT = "/user/%s/exchange/amq.direct/match.%d";
	public final static String MOVE_DESTINATION_FORMAT = "/user/%s/exchange/amq.direct/move.%d";
	public final static String DESTINATION = "/user/exchange/amq.direct/match.";

	private final static Map<Long, GameStatus> gamesStatus = new ConcurrentHashMap<>();
	private final static Map<Long, String> userConnected = new ConcurrentHashMap<>();
	private final static Map<Long, Timer> userTimeOuts = new ConcurrentHashMap<>();
	private final static Map<Long, String> offeredDraws = new ConcurrentHashMap<>();
	private final static Map<Long, MatchClock> blackPiecesClocks = new ConcurrentHashMap<Long, MatchClock>();
	private final static Map<Long, MatchClock> whitePiecesClocks = new ConcurrentHashMap<Long, MatchClock>();

	@Autowired
	private MatchRepository matchRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private MoveValidator moveValidator;

	public Match findById(Long matchId) {
		return matchRepository.findById(matchId).get();
	}

	public List<Match> findByUser(String username) {
		return matchRepository.findByUser(username);
	}

	public Page<Match> findByUser(final Optional<List<Color>> color,
			final Optional<List<TimeControlType>> timeControlTypes, final Optional<Calendar> since,
			final Optional<Calendar> until, final Optional<List<String>> result, final Optional<String> opponent,
			int page, int count, String username) {
		Pageable pageable = PageRequest.of(page, count, Sort.by("creationDate").descending());

		@SuppressWarnings("serial")
		Specification<Match> specification = new Specification<Match>() {

			@Override
			public Predicate toPredicate(Root<Match> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = new ArrayList<Predicate>();

				if (!opponent.isEmpty()) {
					predicates.add(cb.or(cb.equal(root.get("whitePiecesUser").get("username"), opponent.get()),
							cb.equal(root.get("blackPiecesUser").get("username"), opponent.get())));
				}

				if (!color.isEmpty() && !color.get().containsAll(Arrays.asList(Color.WHITE, Color.BLACK))) {
					String user = color.get().get(0) == Color.WHITE ? "whitePiecesUser" : "blackPiecesUser";
					predicates.add(cb.like(root.get(user).get("username"), username));
				} else {

					predicates.add(cb.or(cb.like(root.get("whitePiecesUser").get("username"), username),
							cb.like(root.get("blackPiecesUser").get("username"), username)));
				}

				if (!until.isEmpty()) {
					predicates.add(cb.lessThanOrEqualTo(root.<Calendar>get("creationDate"), until.get()));
				}

				if (!since.isEmpty()) {
					predicates.add(cb.greaterThanOrEqualTo(root.<Calendar>get("creationDate"), since.get()));
				}

				if (!timeControlTypes.isEmpty()) {
					List<Predicate> timeControlPredicates = new ArrayList<Predicate>();

					for (TimeControlType timeControl : timeControlTypes.get()) {
						switch (timeControl) {
						case RAPID:
							timeControlPredicates.add(cb.greaterThanOrEqualTo(root.get("timeInSeconds"), 600));
							break;
						case BLITZ:
							timeControlPredicates.add(cb.and(cb.greaterThanOrEqualTo(root.get("timeInSeconds"), 180),
									cb.lessThan(root.get("timeInSeconds"), 600)));
							break;
						case BULLET:
							timeControlPredicates.add(cb.lessThan(root.get("timeInSeconds"), 180));
							break;
						}
					}
					if (timeControlPredicates.size() > 1) {
						predicates
								.add(cb.or(timeControlPredicates.toArray(new Predicate[timeControlPredicates.size()])));
					} else {
						predicates.addAll(timeControlPredicates);
					}
				}

				if (!result.isEmpty() && !result.get().containsAll(Arrays.asList("DRAW", "WIN", "LOSS"))) {
					List<Predicate> resultPredicates = new ArrayList<Predicate>();
					for (String matchResult : result.get()) {

						if ("DRAW".equals(matchResult)) {
							resultPredicates.add(cb.equal(root.get("state"), MatchState.DRAW));
						} else {

							if ("WIN".equals(matchResult)) {
								Predicate winpred = cb.or(
										cb.and(cb.equal(root.get("whitePiecesUser").get("username"), username),
												cb.equal(root.get("state"), MatchState.WHITE_WIN)),
										cb.and(cb.equal(root.get("blackPiecesUser").get("username"), username),
												cb.equal(root.get("state"), MatchState.BLACK_WIN)));
								resultPredicates.add(winpred);
							} else {
								Predicate losspred = cb.or(
										cb.and(cb.equal(root.get("whitePiecesUser").get("username"), username),
												cb.equal(root.get("state"), MatchState.BLACK_WIN)),
										cb.and(cb.equal(root.get("blackPiecesUser").get("username"), username),
												cb.equal(root.get("state"), MatchState.WHITE_WIN)));
								resultPredicates.add(losspred);
							}
						}
					}

					predicates.add(cb.or(resultPredicates.toArray(new Predicate[resultPredicates.size()])));
				}

				return cb.and(predicates.toArray(new Predicate[predicates.size()]));
			}

		};

		Page<Match> searchResult = matchRepository.findAll(specification, pageable);

		return searchResult;
	}

	private void sendMessage(Long matchId, String username, MessageType message,
			Optional<MatchMessage.NewEloRating> newElo) {

		Match match = findById(matchId);
		Player player = match.getWhitePiecesUser().getUsername().equals(username) ? match.getWhitePiecesUser()
				: match.getBlackPiecesUser();
		player.notifyMatchMessage(matchId, message, newElo);
	}

	private MatchState computeMatchState(Match match, String winner) {
		return match.getWhitePiecesUser().getUsername().equals(winner) ? MatchState.WHITE_WIN : MatchState.BLACK_WIN;
	}

	public void userDisconnects(final String username, final long matchId) {
		Match match = matchRepository.findById(matchId).get();
		if (match.getState().equals(MatchState.STARTED)) {

			String remainingUsername = getSecondUser(match, username);
			sendMessage(matchId, remainingUsername, MessageType.USER_DISCONNECTED, Optional.empty());
			Timer timer = new Timer();
			timer.schedule(new ServiceTask(this, m -> m.notifyDisconnect(matchId, username)),
					TIME_TO_RECONNECT_BEFORE_LOSS);
		}
	}

	protected void notifyDisconnect(long matchId, String username) {
		notifyLossAndWin(matchId, username,
				new MessageType[] { MessageType.LOSS_BY_DISCONNECTION, MessageType.WIN_BY_DISCONNECTION });

	}

	private String getSecondUser(Match match, String firstusername) {
		return match.getWhitePiecesUser().getUsername().equals(firstusername) ? match.getBlackPiecesUser().getUsername()
				: match.getWhitePiecesUser().getUsername();
	}

	private Match save(Match match) {
		Match savedMatch = matchRepository.save(match);
		if (!match.getState().equals(MatchState.STARTED)) {
			gamesStatus.remove(match.getMatchId());
			whitePiecesClocks.get(match.getMatchId()).stop();
			blackPiecesClocks.get(match.getMatchId()).stop();
			whitePiecesClocks.remove(match.getMatchId());
			blackPiecesClocks.remove(match.getMatchId());
			offeredDraws.remove(match.getMatchId());

		}

		return savedMatch;

	}

	public void userReady(String username, long matchId) {
		Match match = findById(matchId);
		if (userConnected.containsKey(matchId) && !username.equals(userConnected.get(matchId))) {
			whitePiecesClocks.get(matchId).start();
			sendMessage(matchId, match.getWhitePiecesUser().getUsername(), MessageType.MATCH_STARTED, Optional.empty());
			sendMessage(matchId, match.getBlackPiecesUser().getUsername(), MessageType.MATCH_STARTED, Optional.empty());
			userConnected.remove(matchId);
			if (userTimeOuts.containsKey(matchId)) {
				userTimeOuts.get(matchId).cancel();
				userTimeOuts.remove(matchId);
			}
		} else {
			userConnected.put(matchId, username);
			Timer timer = new Timer();
			timer.schedule(new ServiceTask(this, m -> m.notifyDisconnect(matchId, getSecondUser(match, username))),
					TIME_TO_RECONNECT_BEFORE_LOSS);
			userTimeOuts.put(matchId, timer);
		}
	}

	public void chatDisabled(String username, long matchId) {
		Match match = findById(matchId);
		User user = username.equals(match.getWhitePiecesUser().getUsername()) ? match.getBlackPiecesUser()
				: match.getWhitePiecesUser();
		user.notifyMatchMessage(matchId, MessageType.CHAT_DISABLED, Optional.empty());
	}

	public void userResigns(String username, long matchId) {
		notifyLossAndWin(matchId, username,
				new MessageType[] { MessageType.LOSS_BY_RESIGNATION, MessageType.WIN_BY_RESIGNATION });
	}

	public void userCheckmated(String username, long matchId) {
		notifyLossAndWin(matchId, username, new MessageType[] { MessageType.CHECKMATED, MessageType.CHECKMATE });
	}

	public void userWrongMove(String username, long matchId) {
		notifyLossAndWin(matchId, username,
				new MessageType[] { MessageType.WRONG_MOVE, MessageType.WIN_BY_RESIGNATION });
	}

	public void userOffersDraw(String username, long matchId) {

		Match match = findById(matchId);
		String secondUser = getSecondUser(match, username);
		Board matchBoard = gamesStatus.get(matchId).getBoard();
		if (matchBoard.isDraw() && !matchBoard.isStaleMate() && !matchBoard.isInsufficientMaterial()) {
			if (matchBoard.getHalfMoveCounter() > 100) {
				this.notifyDraw(username, matchId, MessageType.FIFTY_MOVE_RULE_REPETITION_DRAW);
			} else {
				this.notifyDraw(username, matchId, MessageType.THREEFOLD_REPETITION_DRAW);
			}
		} else {
			if (offeredDraws.containsKey(matchId) && offeredDraws.get(matchId).equals(secondUser)) {
				userAcceptsDraw(username, matchId);
			} else {
				offeredDraws.put(matchId, username);
				sendMessage(matchId, secondUser, MessageType.USER_OFFERS_DRAW, Optional.empty());
			}
		}
	}

	public void userAcceptsDraw(String username, long matchId) {
		Match match = findById(matchId);
		String secondUser = getSecondUser(match, username);
		if (offeredDraws.containsKey(matchId) && offeredDraws.get(matchId).equals(secondUser)) {
			this.notifyDraw(username, matchId, MessageType.DRAW_BY_AGREEMENT);
		}
	}

	private void userStalemates(String username, long matchId) {
		this.notifyDraw(username, matchId, MessageType.STALEMATE);
	}

	private void insufficientMaterial(String username, long matchId) {
		this.notifyDraw(username, matchId, MessageType.INSUFFICIENT_MATERIAL);
	}

	private void notifyDraw(String username, long matchId, MessageType drawMessage) {
		Match match = findById(matchId);
		String secondUser = getSecondUser(match, username);
		match.setState(MatchState.DRAW);
		save(match);
		Map<String, MatchMessage.NewEloRating> newRatings = updateEloRatings(match);
		sendMessage(matchId, username, drawMessage, Optional.of(newRatings.get(username)));
		sendMessage(matchId, secondUser, drawMessage, Optional.of(newRatings.get(secondUser)));
	}

	private Map<String, MatchMessage.NewEloRating> updateEloRatings(Match match) {

		double[] eloRatings = new double[] { match.getWhitePiecesUser().getCurrentRating(match.getType()),
				match.getBlackPiecesUser().getCurrentRating(match.getType()) };

		double[] newEloRatings;
		User[] users;
		switch (match.getState()) {
		case WHITE_WIN:
			newEloRatings = calculateNewEloRatings(eloRatings[0], eloRatings[1], false);
			users = new User[] { match.getWhitePiecesUser(), match.getBlackPiecesUser() };
			break;
		case BLACK_WIN:
			newEloRatings = calculateNewEloRatings(eloRatings[1], eloRatings[0], false);
			users = new User[] { match.getBlackPiecesUser(), match.getWhitePiecesUser() };
			break;
		case DRAW:
			newEloRatings = calculateNewEloRatings(eloRatings[0], eloRatings[1], true);
			users = new User[] { match.getWhitePiecesUser(), match.getBlackPiecesUser() };
			break;
		default:
			return null;
		}

		Map<String, MatchMessage.NewEloRating> result = new HashMap<>();
		double[] currentEloRatings = new double[] { users[0].getCurrentRating(match.getType()),
				users[1].getCurrentRating(match.getType()) };
		users[0].getRatings(match.getType()).add(0, new EloScore(users[0], newEloRatings[0], Calendar.getInstance()));
		users[1].getRatings(match.getType()).add(0, new EloScore(users[1], newEloRatings[1], Calendar.getInstance()));

		userRepository.saveAll(Arrays.asList(users));

		result.put(users[0].getUsername(),
				new MatchMessage.NewEloRating(newEloRatings[0], newEloRatings[0] - currentEloRatings[0]));
		result.put(users[1].getUsername(),
				new MatchMessage.NewEloRating(newEloRatings[1], newEloRatings[1] - currentEloRatings[1]));

		return result;

	}

	private double[] calculateNewEloRatings(double firstEloRating, double secondEloRating, boolean isADraw) {

		double r1 = Math.pow(10d, firstEloRating / 400d);
		double r2 = Math.pow(10d, secondEloRating / 400d);

		double e1 = r1 / (r1 + r2);
		double e2 = r2 / (r1 + r2);

		double s1 = isADraw ? 0.5d : 1d;
		double s2 = isADraw ? 0.5d : 0d;

		double newFirstEloRating = firstEloRating + K * (s1 - e1);
		double newSecondEloRating = secondEloRating + K * (s2 - e2);

		return new double[] { newFirstEloRating, newSecondEloRating };
	}

	public void move(long matchId, Move move) {

		Match match = findById(matchId);
		String username = move.getColor() ? match.getWhitePiecesUser().getUsername()
				: match.getBlackPiecesUser().getUsername();

		User opUser = move.getColor() ? match.getBlackPiecesUser() : match.getWhitePiecesUser();

		move.setMatch(match);
		move.setNumber(match.getMoves() != null ? match.getMoves().size() : 0);

		if (match.getMoves() == null) {
			match.setMoves(new ArrayList<Move>(Arrays.asList(new Move[] { move })));
		} else {
			match.getMoves().add(move);
		}

		MatchClock moveClock = (move.getColor() ? whitePiecesClocks : blackPiecesClocks).get(match.getMatchId());
		MatchClock opponentClock = (move.getColor() ? blackPiecesClocks : whitePiecesClocks).get(match.getMatchId());

		moveClock.stop();
		opponentClock.start();

		move.setTimeInMillis(moveClock.getTimeInMillis());

		GameStatus status = gamesStatus.get(match.getMatchId());

		MoveValidationResult validationResult = moveValidator.validateMove(status.getBoard(), status.getMoveList(),
				move);

		if (validationResult.isValid()) {

			move.setAcn(validationResult.getSan());
			move.setFenPosition(validationResult.getNewFenPosition());
			Runnable task = () -> {
				opUser.notifyMove(move);
			};
			Thread thread = new Thread(task);
			thread.start();
			match = save(match);
			
			if (validationResult.isCheckMate()) {
				userCheckmated(opUser.getUsername(), matchId);
			} else if (validationResult.isStaleMate()) {
				userStalemates(username, matchId);
			} else if (validationResult.isInsuficientMaterial()) {
				insufficientMaterial(username, matchId);
			}

		} else {
			this.userWrongMove(opUser.getUsername(), matchId);
		}

		

	}

	protected void notifyTimeOut(long matchId, String username) {
		notifyLossAndWin(matchId, username, new MessageType[] { MessageType.LOSS_ON_TIME, MessageType.WIN_BY_TIME });
	}

	private void notifyLossAndWin(long matchId, String username, MessageType[] messages) {
		Match match = findById(matchId);
		if (match.getState().equals(MatchState.STARTED)) {
			String winner = getSecondUser(match, username);
			MatchState newState = computeMatchState(match, winner);
			match.setState(newState);
			match = save(match);
			Map<String, MatchMessage.NewEloRating> newRatings = updateEloRatings(match);
			sendMessage(matchId, username, messages[0], Optional.of(newRatings.get(username)));
			sendMessage(matchId, winner, messages[1], Optional.of(newRatings.get(winner)));
		}
	}

	public Match createMatchFromChallenge(Challenge challenge, String challengeAcepterUsername) {

		Match newMatch = new Match();
		newMatch.setTimeInSeconds(challenge.getTimeInSeconds());
		newMatch.setTimeIncrementInSeconds(challenge.getTimeIncrementInSeconds());
		newMatch.setCreationDate(Calendar.getInstance());
		User creator = userService.findOne(challenge.getUserName());
		User challengeAcepter = userService.findOne(challengeAcepterUsername);
		newMatch.setState(MatchState.STARTED);

		switch (challenge.getColor()) {
		case WHITE:
			newMatch.setWhitePiecesUser(creator);
			newMatch.setBlackPiecesUser(challengeAcepter);
			break;
		case BLACK:
			newMatch.setWhitePiecesUser(challengeAcepter);
			newMatch.setBlackPiecesUser(creator);
			break;
		case RANDOM:
			if (Math.random() < 0.5) {
				newMatch.setWhitePiecesUser(creator);
				newMatch.setBlackPiecesUser(challengeAcepter);
			} else {
				newMatch.setWhitePiecesUser(challengeAcepter);
				newMatch.setBlackPiecesUser(creator);
			}
			break;
		}

		newMatch = matchRepository.save(newMatch);
		GameStatus matchGameStatus = new GameStatus();
		gamesStatus.put(newMatch.getMatchId(), matchGameStatus);
		newMatch = save(newMatch);
		MatchClock blackClock = createMatchClock(newMatch.getBlackPiecesUser().getUsername(), newMatch.getMatchId(),
				newMatch.getTimeInSeconds(), newMatch.getTimeIncrementInSeconds());
		MatchClock whiteClock = createMatchClock(newMatch.getWhitePiecesUser().getUsername(), newMatch.getMatchId(),
				newMatch.getTimeInSeconds(), newMatch.getTimeIncrementInSeconds());

		blackPiecesClocks.put(newMatch.getMatchId(), blackClock);
		whitePiecesClocks.put(newMatch.getMatchId(), whiteClock);

		return newMatch;
	}

	private MatchClock createMatchClock(final String username, final long matchId, final int seconds,
			final int increment) {
		MatchClock clock = new MatchClock(new ServiceTask(this, m -> m.notifyTimeOut(matchId, username)), seconds,
				increment);

		return clock;
	}
}
