package es.fporto.demo.minichess.challenge.service;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import es.fporto.demo.minichess.match.service.MatchService;
import es.fporto.demo.minichess.model.Challenge;
import es.fporto.demo.minichess.model.Challenge.ChallengeColor;
import es.fporto.demo.minichess.user.model.User;
import es.fporto.demo.minichess.user.service.UserService;
import es.fporto.demo.minichess.model.Match;
import es.fporto.demo.minichess.model.TimeControlType;

@Service
public class ChallengeService {

	private static final List<Challenge> challenges = new CopyOnWriteArrayList<>();
	
	public static final String DELETED_CHALLENGES_TOPIC = "/topic/deletedchallenges";
	public static final String CHALLENGES_TOPIC = "/topic/challenges";
	public static final String ACCEPTED_CHALLENGES_TOPIC = "/topic/acceptedchallenges";
	
	@Autowired
	private MatchService matchService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private UserService userService;

	private static long challengeId = 0l;

	private Challenge addChallenge(Challenge challenge) {
		challenge.setCreationDate(Calendar.getInstance());
		challenge.setChallengeId(ChallengeService.challengeId++);
		challenges.add(challenge);
		return challenge;
	}

	public Match createChallenge(Challenge challenge) {
		Challenge complementaryChallenge = getComplementaryChallenge(challenge);

		if (complementaryChallenge != null) {
			return acceptChallenge(complementaryChallenge, challenge.getUserName());
		} else {
			User user = userService.findOne(challenge.getUserName());
			challenge
					.setEloRating(user.getCurrentRating(TimeControlType.getFromDuration(challenge.getTimeInSeconds())));
			Challenge newChallenge = addChallenge(challenge);
			simpMessagingTemplate.convertAndSend(CHALLENGES_TOPIC, newChallenge);
			return null;
		}
	}

	public Challenge getComplementaryChallenge(Challenge challenge) {

		Set<Challenge> complementaryChallenges = challenges.stream()
				.filter(c -> c.matches(challenge.getColor(), challenge.getTimeInSeconds(),
						challenge.getTimeIncrementInSeconds()) && !c.getUserName().equals(challenge.getUserName()) && c.isFromHuman())
				.limit(1).collect(Collectors.toSet());

		if (complementaryChallenges.isEmpty()) {
			return null;
		} else {
			return complementaryChallenges.iterator().next();
		}

	}

	public List<Challenge> filterChallenges(ChallengeColor color, int timeInSeconds, int timeIncrementInSeconds) {

		return challenges.stream().filter(c -> c.matches(color, timeInSeconds, timeIncrementInSeconds))
				.collect(Collectors.toList());
	}

	public List<Challenge> getAllChallenges() {

		return challenges;
	}

	public Challenge getChallenge(long id) {
		List<Challenge> challenges = ChallengeService.challenges.stream().filter(c -> c.getChallengeId() == id).limit(1)
				.collect(Collectors.toList());

		if (challenges.isEmpty()) {
			return null;
		} else {
			return challenges.get(0);
		}
	}

	public void removeChallenge(Challenge challenge) {
		ChallengeService.challenges.remove(challenge);
	}

	public void removeChallengesFromUser(String username) {

		List<Challenge> challengesToBeRemoved = challenges.stream().filter(c -> username.equals(c.getUserName()))
				.collect(Collectors.toList());
		challengesToBeRemoved.forEach(c -> simpMessagingTemplate.convertAndSend(DELETED_CHALLENGES_TOPIC, c));
		challengesToBeRemoved.forEach(challenges::remove);
	}

	public Match acceptChallenge(Challenge challenge, String username) {
		Challenge acceptedChallenge = getChallenge(challenge.getChallengeId());
		Match newMatch = matchService.createMatchFromChallenge(acceptedChallenge, username);
		acceptedChallenge.setMatchId(newMatch.getMatchId());
		removeChallenge(acceptedChallenge);
		newMatch.getBlackPiecesUser().notifyAcceptedChallenge(acceptedChallenge);
		newMatch.getWhitePiecesUser().notifyAcceptedChallenge(acceptedChallenge);
		simpMessagingTemplate.convertAndSend(ACCEPTED_CHALLENGES_TOPIC, acceptedChallenge);
		return newMatch;
	}
}
