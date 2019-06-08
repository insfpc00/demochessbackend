package es.fporto.demo.minichess.loader;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import es.fporto.demo.minichess.controller.model.UserStats;
import es.fporto.demo.minichess.model.Match;
import es.fporto.demo.minichess.model.MatchState;
import es.fporto.demo.minichess.model.Move;
import es.fporto.demo.minichess.model.Piece;
import es.fporto.demo.minichess.model.PieceId.Color;
import es.fporto.demo.minichess.model.PieceId.PieceType;
import es.fporto.demo.minichess.model.Position;
import es.fporto.demo.minichess.player.UCIEngine;
import es.fporto.demo.minichess.player.UCIEngine.Difficulty;
import es.fporto.demo.minichess.repository.AvatarRepository;
import es.fporto.demo.minichess.repository.EloScoreRepository;
import es.fporto.demo.minichess.repository.MatchRepository;
import es.fporto.demo.minichess.repository.PieceRepository;
import es.fporto.demo.minichess.repository.RoleRepository;
import es.fporto.demo.minichess.repository.UserRepository;
import es.fporto.demo.minichess.user.model.Avatar;
import es.fporto.demo.minichess.user.model.EloScore;
import es.fporto.demo.minichess.user.model.Role;
import es.fporto.demo.minichess.user.model.User;
import es.fporto.demo.minichess.user.model.User.FideTitles;
import es.fporto.demo.minichess.user.model.User.Themes;
import es.fporto.demo.minichess.user.model.UserDto;
import es.fporto.demo.minichess.user.service.UserService;

@Component
public class DataLoader implements CommandLineRunner {

	@Autowired
	private PieceRepository pieceRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private AvatarRepository avatarRepository;
	@Autowired
	private MatchRepository matchRepository;
	@Autowired
	private EloScoreRepository eloScoreRepository;
	@Autowired 
	private PuzzlesLoader puzzlesLoader;
	@Autowired 
	private OpeningsLoader openingsLoader;
	
	private final String DEMO_USER_ROLE = "USER";
	private final int MATCH_HISTORY_DAYS_SPREAD = 200;
	private final static List<Piece> pieceTypes = new ArrayList<Piece>();

	
	private void initializePieceTypes() {

		for (PieceType pieceType : PieceType.values()) {
			for (Color color : Color.values()) {
				pieceTypes.add(pieceRepository.save(new Piece(pieceType, color)));
			}
		}
	}

	private Piece getPiece(PieceType pieceType, Color color) {
		for (Piece piece : pieceTypes) {
			if (piece.getColor() == color && piece.getType() == pieceType) {
				return piece;
			}
		}
		return null;
	}

	private List<Move> getFoolsMateMoveSequence() {

		Move[] moves = { new Move(0, true, new Position(5, 6), new Position(5, 5), null, "f2-f3", 1000),
				new Move(1, false, new Position(4, 1), new Position(4, 3), null, "e7-e5", 3500),
				new Move(2, true, new Position(6, 6), new Position(6, 4), null, "g2-g4", 4800),
				new Move(3, false, new Position(3, 0), new Position(7, 4), null, "d8-h4#", 5500) };

		return Arrays.asList(moves);
	}

	@Transactional
	private User createUser(String login, String pass, FideTitles fideTitle, String birthDate, String firstname,
			String secondname, String country, String avatarFileName) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date date = null;

		date = sdf.parse(birthDate);

		Calendar demoUserBirthDate = Calendar.getInstance();
		demoUserBirthDate.setTime(date);
		Calendar creationDate = Calendar.getInstance();
		creationDate.add(Calendar.DATE, -1000);
		UserDto demoUser = new UserDto(login, pass, fideTitle, demoUserBirthDate, firstname, secondname, country,
				creationDate, Themes.DEFAULT);
		demoUser.setEloRatings(new Double[] { 1500d, 1500d, 1500d });
		User newUser = userService.save(demoUser, DEMO_USER_ROLE);

		if (avatarFileName != null) {
			//File avatarFile = new File(getClass().getResourceAsStream("/images/" + avatarFileName).getFile());
			//System.out.println(getClass().getResource("/images/" + avatarFileName).getFile().toString());
			//File avatarFile = new File(Paths.get("/images/"+avatarFileName).toAbsolutePath().toString());
			BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/images/"+avatarFileName));
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(image, "png", outputStream);
			Avatar avatar = avatarRepository.save(new Avatar("image/png", outputStream.toByteArray(), newUser));
			newUser.setAvatar(avatar);
		}

		return userRepository.save(newUser);

	}

	private void createRoles() {
		Role userRole = new Role();
		userRole.setName("USER");
		roleRepository.save(userRole);
	}

	private EloScore createEloScore(User user, Calendar date, double eloRating) {
		EloScore eloScore = new EloScore(user, eloRating, date);
		return eloScoreRepository.save(eloScore);
	}

	private void updateUsersElo(User whiteUser, User blackUser, Match match) {
		double randomEloInc = Math.random() * 50;
		double whiteEloInc;
		double blackEloInc;
		if (match.getState() == MatchState.DRAW) {
			whiteEloInc = 0d;
			blackEloInc = 0d;
		} else if (match.getState() == MatchState.WHITE_WIN) {
			whiteEloInc = randomEloInc;
			blackEloInc = -randomEloInc;
		} else {
			whiteEloInc = -randomEloInc;
			blackEloInc = randomEloInc;
		}

		if (match.getTimeInSeconds() > 300) {

			double whiteUserElo = whiteUser.getRapidEloScores().get(whiteUser.getRapidEloScores().size() - 1)
					.getEloRating();
			double blackUserElo = blackUser.getRapidEloScores().get(blackUser.getRapidEloScores().size() - 1)
					.getEloRating();
			whiteUser.getRapidEloScores()
					.add(createEloScore(whiteUser, match.getCreationDate(), whiteUserElo + whiteEloInc));
			blackUser.getRapidEloScores()
					.add(createEloScore(blackUser, match.getCreationDate(), blackUserElo + blackEloInc));
		} else if (match.getTimeInSeconds() > 60) {

			double whiteUserElo = whiteUser.getBlitzEloScores().get(whiteUser.getBlitzEloScores().size() - 1)
					.getEloRating();
			double blackUserElo = blackUser.getBlitzEloScores().get(blackUser.getBlitzEloScores().size() - 1)
					.getEloRating();
			whiteUser.getBlitzEloScores()
					.add(createEloScore(whiteUser, match.getCreationDate(), whiteUserElo + whiteEloInc));
			blackUser.getBlitzEloScores()
					.add(createEloScore(blackUser, match.getCreationDate(), blackUserElo + blackEloInc));
		} else {

			double whiteUserElo = whiteUser.getBulletEloScores().get(whiteUser.getBulletEloScores().size() - 1)
					.getEloRating();
			double blackUserElo = blackUser.getBulletEloScores().get(blackUser.getBulletEloScores().size() - 1)
					.getEloRating();
			whiteUser.getBulletEloScores()
					.add(createEloScore(whiteUser, match.getCreationDate(), whiteUserElo + whiteEloInc));
			blackUser.getBulletEloScores()
					.add(createEloScore(blackUser, match.getCreationDate(), blackUserElo + blackEloInc));
		}
		userRepository.save(whiteUser);
		userRepository.save(blackUser);
	}

	private void generateMatchHistory(User firstUser, User secondUser, int numberOfMatches) {

		Calendar matchDate = Calendar.getInstance();
		matchDate.add(Calendar.DAY_OF_YEAR, -MATCH_HISTORY_DAYS_SPREAD);
		
		while (numberOfMatches-- > 0) {

			final int[] matchTime = { 600, 300, 180, 60 };
			final int[] matchIncrement = { 0, 1, 2, 3 };

			matchDate = (Calendar) matchDate.clone();
			matchDate.add(Calendar.DATE, 1 + (int) Math.round(Math.random() * 2));
			if (matchDate.after(Calendar.getInstance())) {
				matchDate=Calendar.getInstance();
			}

			double colorSeed = Math.random();

			User whitePiecesUser = colorSeed < 0.5 ? firstUser : secondUser;
			User blackPiecesUser = colorSeed < 0.5 ? secondUser : firstUser;

			final Match match = new Match(whitePiecesUser, blackPiecesUser, getFoolsMateMoveSequence(), matchDate,
					matchTime[(int) Math.floor(Math.random() * 4)],
					matchIncrement[(int) Math.floor(Math.random() * 4)]);

			match.getMoves().forEach(m -> m.setMatch(match));

			final MatchState[] matchResults = { MatchState.WHITE_WIN, MatchState.BLACK_WIN, MatchState.DRAW };

			match.setState(matchResults[(int) Math.floor(Math.random() * 3)]);

			Match savedMatch = matchRepository.save(match);
			updateUsersElo(whitePiecesUser, blackPiecesUser, savedMatch);

		}

	}

	private void printStats(UserStats userStats) {

		for (Double blitzRating : userStats.getBlitzEloRatings()) {
			System.out.println(blitzRating);
		}
	}

	@Transactional
	public void initDb() throws Exception {

		this.initializePieceTypes();

		this.createRoles();

		User demoUser = this.createUser("demo", "demo", FideTitles.CM, "12-11-1982", "Fernando", "Porto", "Spain",
				"me.png");
		User demo2User = this.createUser("demo2", "demo2", FideTitles.GM, "30-11-1990", "Magnus", "Carlsen", "Norway",
				"carlsen.png");

		User bot = this.createBot("stockfish1", FideTitles.NM, "StockFish", "Easy", "robot", "wall-e.png", 1500d,
				Difficulty.EASY);
		User bot2 = this.createBot("stockfish2", FideTitles.FM, "StockFish", "Medium", "Global", "hal.png", 2000d,
				Difficulty.MEDIUM);
		User bot3 = this.createBot("stockfish3", FideTitles.GM, "StockFish", "Medium", "Global", "terminator.png",
				2900d, Difficulty.HARD);

		EloScore[] initialEloScores = { demoUser.getBlitzEloScores().get(0), demoUser.getBulletEloScores().get(0),
				demoUser.getRapidEloScores().get(0), demo2User.getBlitzEloScores().get(0),
				demo2User.getBulletEloScores().get(0), demo2User.getRapidEloScores().get(0) };

		Arrays.asList(initialEloScores).forEach(e -> {
			e.getDate().add(Calendar.DATE, -MATCH_HISTORY_DAYS_SPREAD);
			this.eloScoreRepository.save(e);
		});

		generateMatchHistory(demoUser, demo2User, 100);

		demoUser = userRepository.findByUsername(demoUser.getUsername());
	}

	private String calendarToString(Calendar cal) {

		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(cal.getTimeZone());
		String date = simpleDateFormat.format(cal.getTime());
		return date;
	}

	@Transactional
	private User createBot(String login, FideTitles fideTitle, String firstname, String lastname, String country,
			String avatarFileName, double elo, Difficulty level) throws Exception {

		User newUser = new UCIEngine(level);
		newUser.setUsername(login);
		newUser.setPassword(UUID.randomUUID().toString());
		newUser.setFirstName(firstname);
		newUser.setLastName(lastname);
		newUser.setFideTitle(fideTitle);
		newUser.setCountry("robot");

		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, -1);

		newUser.setCreationDate(now);
		newUser = userRepository.save(newUser);
		// Arrays.asList(new EloScore[] {bulletElo}

		EloScore bulletElo = eloScoreRepository.save(new EloScore(newUser, elo, now));
		EloScore blitzElo = eloScoreRepository.save(new EloScore(newUser, elo, now));
		EloScore rapidElo = eloScoreRepository.save(new EloScore(newUser, elo, now));

		newUser.setBulletEloScores(Arrays.asList(new EloScore[] { bulletElo }));
		newUser.setBlitzEloScores(Arrays.asList(new EloScore[] { blitzElo }));
		newUser.setRapidEloScores(Arrays.asList(new EloScore[] { rapidElo }));

		userRepository.save(newUser);

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date date = null;

		if (avatarFileName != null) {
			//File avatarFile = new File(getClass().getResource("/images/" + avatarFileName).getFile());
			BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/images/"+avatarFileName));
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(image, "png", outputStream);
			Avatar avatar = avatarRepository.save(new Avatar("image/png", outputStream.toByteArray(), newUser));
			newUser.setAvatar(avatar);
		}

		return userRepository.save(newUser);

	}

	

	@Override
	public void run(String... args) throws Exception {
		for (String arg : args) {
			if ("initDb".equals(arg)) {
				initDb();
				openingsLoader.loadOpenings();
				puzzlesLoader.loadPuzzles();
			}
		}

	}
}
