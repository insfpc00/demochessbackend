package es.fporto.demo.minichess.puzzle.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.fporto.demo.minichess.controller.model.PuzzleResponse;
import es.fporto.demo.minichess.model.PlayedPuzzle;
import es.fporto.demo.minichess.model.Puzzle;
import es.fporto.demo.minichess.model.SimpleMove;
import es.fporto.demo.minichess.puzzle.service.MoveResponse;
import es.fporto.demo.minichess.puzzle.service.PuzzleService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/puzzle")

public class PuzzleController {

	@Autowired
	private PuzzleService puzzleService;

	@PostMapping("{label}/play")
	public ResponseEntity<Puzzle> postPlayPuzzle(@PathVariable(value = "label") String label, Principal principal) {

		Puzzle puzzle = puzzleService.playPuzzle(label, principal.getName());

		if (puzzle != null) {
			return new ResponseEntity<Puzzle>(puzzle, HttpStatus.OK);
		} else {
			return new ResponseEntity<Puzzle>(HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping
	public List<PuzzleResponse> getPuzzles(Principal principal) {

		List<Puzzle> puzzles = puzzleService.getAllPuzzles();
		List<PlayedPuzzle> myPuzzles = puzzleService.getAllPlayedPuzzles(principal.getName());
		final Map<String, PlayedPuzzle> playedPuzzlesMap = myPuzzles.stream()
				.collect(Collectors.toMap(p -> p.getPuzzle().getLabel(), Function.identity()));

		List<PuzzleResponse> response = puzzles.stream().map(p -> {
			PlayedPuzzle pp = playedPuzzlesMap.get(p.getLabel());
			return new PuzzleResponse(p, pp != null ? pp.isEnded() : false,
					pp != null ? Optional.of(pp.getCreatedAt()) : Optional.empty());
		}).collect(Collectors.toList());

		return response;

	}

	@PostMapping("{label}/move")
	public ResponseEntity<MoveResponse> move(@RequestBody SimpleMove simpleMove, @PathVariable(value="label") String puzzle, Principal principal){
		
		
		MoveResponse moveResponse = puzzleService.move(puzzle, principal.getName(),simpleMove);
		
		if (!moveResponse.isFinalMove() && moveResponse.getMove()==null) {
			return new ResponseEntity<MoveResponse>(HttpStatus.NOT_ACCEPTABLE);
		} else {
			return new ResponseEntity<MoveResponse>(moveResponse,HttpStatus.OK);
		}
		
		
	}
}
