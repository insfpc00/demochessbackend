package es.fporto.demo.minichess.player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.fporto.demo.minichess.challenge.service.ChallengeService;
import es.fporto.demo.minichess.common.Utils;
import es.fporto.demo.minichess.match.model.MatchMessage.MessageType;
import es.fporto.demo.minichess.match.model.MatchMessage.NewEloRating;
import es.fporto.demo.minichess.match.service.MatchService;
import es.fporto.demo.minichess.model.Challenge;
import es.fporto.demo.minichess.model.Challenge.ChallengeColor;
import es.fporto.demo.minichess.model.Match;
import es.fporto.demo.minichess.model.Move;
import es.fporto.demo.minichess.model.PieceId.PieceType;
import es.fporto.demo.minichess.model.Position;
import es.fporto.demo.minichess.repository.UserRepository;
import es.fporto.demo.minichess.uciclient.UCIClient;
import es.fporto.demo.minichess.uciclient.exception.UCIClientException;
import es.fporto.demo.minichess.user.model.User;

@Entity
@Configurable(autowire = Autowire.BY_TYPE, preConstruction = false)

public class UCIEngine extends User {

	public enum State {
		IDLE, PLAYING
	};

	public enum Difficulty {
		EASY, MEDIUM, HARD;
	}

	@Transient
	@Autowired
	private ChallengeService challengeService;
	@Transient
	@Autowired
	private MatchService matchService;
	@Autowired
	@Transient
	private Map<String, UCIClient> uciClients;
	@Autowired
	@Transient
	private UserRepository userRepository;
	@Autowired
	@Transient
	private BeanFactory beanFactory;

	private Difficulty difficulty;

	@OneToOne
	@JsonIgnore
	private Match currentMatch;

	public UCIEngine() {
		super();
	}

	public UCIEngine(Difficulty difficulty) {
		super();
		this.difficulty = difficulty;
	}

	private static final List<String> positions = Arrays
			.asList(new String[] { "a", "b", "c", "d", "e", "f", "g", "h" });

	public Match getCurrentMatch() {
		return currentMatch;
	}

	public void setCurrentMatch(Match currentMatch) {
		this.currentMatch = currentMatch;
	}

	private State botState;

	public State getState() {
		return botState;
	}

	public void setState(State state) {
		this.botState = state;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	private UCIClient getClient() {

		UCIClient uciClient = null;
		if (uciClients.containsKey(this.getUsername())) {
			uciClient = uciClients.get(this.getUsername());
		} else {
			try {
				uciClient = (UCIClient) beanFactory.getBean("uciClient", this.difficulty);
				uciClients.put(this.getUsername(), uciClient);
				uciClient.connect();
			} catch (UCIClientException e) {
				e.printStackTrace();
			}
		}
		return uciClient;

	}

	public void activate() {
		this.botState = State.IDLE;
		this.createChallenges();
	}

	private void createChallenges() {

		for (int time : Arrays.asList(60, 300, 600)) {
			int increment = (int) Math.round(Math.random() * 10);
			challengeService
					.createChallenge(new Challenge(this.getUsername(), ChallengeColor.RANDOM, time, increment, false));
		}

	}

	@Override
	public void notifyMatchMessage(Long matchId, MessageType message, Optional<NewEloRating> newElo) {
		switch (message) {
		case WIN_BY_DISCONNECTION:
		case LOSS_BY_DISCONNECTION:
		case WIN_BY_RESIGNATION:
		case CHECKMATE:
		case CHECKMATED:
		case STALEMATE:
		case THREEFOLD_REPETITION_DRAW:
		case LOSS_ON_TIME:
		case WIN_BY_TIME:
			this.botState = State.IDLE;
			this.createChallenges();
			this.currentMatch = null;
			userRepository.save(this);
			break;
		case MATCH_STARTED:
			matchService.chatDisabled(this.getUsername(), matchId);
			if (currentMatch != null && currentMatch.getWhitePiecesUser().equals(this)) {
				this.sendMove(Utils.STARTING_FEN, currentMatch.getMatchId());
			}
			break;
		default:
		}

	}

	@Override
	public void notifyAcceptedChallenge(Challenge acceptedChallenge) {
		if (this.botState != State.PLAYING) {
			Match match = matchService.findById(acceptedChallenge.getMatchId());
			this.botState = State.PLAYING;
			matchService.userReady(this.getUsername(), acceptedChallenge.getMatchId());
			currentMatch = match;
			userRepository.save(this);
			try {
				getClient().newGame();
			} catch (UCIClientException e) {
				e.printStackTrace();
			}

		}
	}

	private Move convertACNToMove(String acnMove) {

		Position from = convertACNToPosition(acnMove.substring(0, 2));
		Position to = convertACNToPosition(acnMove.substring(2, 4));

		PieceType promotedTo = null;

		if (acnMove.length() > 4) {

			String promotion = acnMove.substring(4, 5);

			if ("q".equals(promotion)) {
				promotedTo = PieceType.QUEEN;
			} else if ("b".equals(promotion)) {
				promotedTo = PieceType.BISHOP;
			} else if ("n".equals(promotion)) {
				promotedTo = PieceType.HORSE;
			} else if ("r".equals(promotion)) {
				promotedTo = PieceType.ROOK;
			}

		}
		return new Move(0, this.currentMatch.getWhitePiecesUser().equals(this), from, to, promotedTo, acnMove, 0l);
	}

	private Position convertACNToPosition(String acn) {
		int x = positions.indexOf(acn.substring(0, 1));
		int y = Integer.valueOf(acn.substring(1, 2));
		return new Position(x, 7 - (y - 1));
	}

	@Override
	public void notifyMove(Move move) {
		if (botState == State.PLAYING) {
			this.sendMove(move.getFenPosition(), move.getMatch().getMatchId());
		}
	}

	private void sendMove(String fen, long matchId) {
		try {
			UCIClient.Move uciBestMove = getClient().makeAMove(fen);
			if (uciBestMove != null) {
				Move bestMove = this.convertACNToMove(uciBestMove.getMove());
				matchService.move(matchId, bestMove);
			}
		} catch (UCIClientException e) {
			e.printStackTrace();
		}
	}

}
