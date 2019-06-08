package es.fporto.demo.minichess.challenge.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.fporto.demo.minichess.challenge.service.ChallengeService;
import es.fporto.demo.minichess.controller.model.ChallengeFilter;
import es.fporto.demo.minichess.model.Challenge;
import es.fporto.demo.minichess.model.Match;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/challenge")

public class ChallengeController {

	
	@Autowired 
	private ChallengeService challengeService;

	@PostMapping
	public Match createChallenge(@RequestBody Challenge challenge) {
		return challengeService.createChallenge(challenge);

	}

	@GetMapping 
	public List<Challenge> getChallenges() {
		return challengeService.getAllChallenges();
	}
	
	@GetMapping("/filter") 
	public List<Challenge> filterChallenges(@RequestBody ChallengeFilter filter) {
		return challengeService.filterChallenges(filter.getColor(), filter.getTimeInSeconds(), filter.getTimeIncrementInSeconds());
	}
	
	@PostMapping("/accept") 
	public Match acceptChallenge(@RequestBody Challenge challenge,Principal principal) {
		
		Match match=challengeService.acceptChallenge(challenge, principal.getName());
		
		challengeService.removeChallengesFromUser(match.getBlackPiecesUser().getUsername());
		challengeService.removeChallengesFromUser(match.getWhitePiecesUser().getUsername());
		
		return match;
	}
	
	@DeleteMapping()
	public void removeChallengesFromUser(Principal principal) {
		 challengeService.removeChallengesFromUser(principal.getName());
	}
		
}
