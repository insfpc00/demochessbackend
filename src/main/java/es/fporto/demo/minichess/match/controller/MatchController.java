package es.fporto.demo.minichess.match.controller;

import java.security.Principal;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.fporto.demo.minichess.match.service.MatchService;
import es.fporto.demo.minichess.model.Match;
import es.fporto.demo.minichess.model.MatchDto;
import es.fporto.demo.minichess.model.Move;
import es.fporto.demo.minichess.model.PieceId.Color;
import es.fporto.demo.minichess.model.TimeControlType;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/match")

public class MatchController {

	@Autowired 
	private MatchService matchService;
	
	@GetMapping("{id}")
	public ResponseEntity<MatchDto> getMatch(@PathVariable(value="id") long matchId, Principal principal) {
		
		Match match = matchService.findById(matchId);
		if (match != null && matchContainsUser(match, principal.getName())) {
			return new ResponseEntity<MatchDto>(new MatchDto(match), HttpStatus.OK);
		} else
			return new ResponseEntity<MatchDto>(HttpStatus.UNAUTHORIZED);
	}
	
	@GetMapping("/moves")
	public ResponseEntity<List<Move>> getMoves(@RequestParam long matchId, Principal principal) {

		Match match = matchService.findById(matchId);
		if (match != null && matchContainsUser(match, principal.getName())) {
			return new ResponseEntity<List<Move>>(match.getMoves(), HttpStatus.OK);
		} else
			return new ResponseEntity<List<Move>>(HttpStatus.UNAUTHORIZED);
	}
	
//	@GetMapping("/all")
//	public List<MatchDto> getMatches(Principal principal) {
//s
//		List<Match> matches = matchService.findByUser(principal.getName());
//		return matches.stream().map(m -> new MatchDto(m))
//				.sorted((c1, c2) -> c2.getCreationDate().compareTo(c1.getCreationDate())).collect(Collectors.toList());
//	}

	@GetMapping()
	public Page<Match> findMatches(@RequestParam Optional<List<Color>> color,
			@RequestParam Optional<List<TimeControlType>> timeControlTypes, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Optional<Calendar> since,
			@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Optional<Calendar> until, @RequestParam Optional<List<String>> result,
			@RequestParam Optional<String> opponent, @RequestParam int page, @RequestParam int count, Principal principal) {
		
		return matchService.findByUser(color, timeControlTypes, since, until, result, opponent, page, count ,principal.getName());
		
	}

	private boolean matchContainsUser(Match match, String username) {
		
		return (match.getWhitePiecesUser().getUsername().equals(username) || match.getBlackPiecesUser().getUsername().equals(username));
	}
}
