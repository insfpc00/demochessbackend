package es.fporto.demo.minichess.analysis.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import es.fporto.demo.minichess.analysis.service.AnalysisService;
import es.fporto.demo.minichess.controller.model.GetOpeningNamesResponse.MovesOpeningPair;
import es.fporto.demo.minichess.model.MoveAnalysis;
import es.fporto.demo.minichess.model.OpeningTree;
import es.fporto.demo.minichess.repository.OpeningTreeRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/analysis")

public class AnalysisController {

	@Autowired
	private OpeningTreeRepository openingRepository;

	@Autowired
	private AnalysisService analysisService;

	@GetMapping("/openings")
	public List<MovesOpeningPair> getOpeningNames(@RequestParam String moves) {

		List<String> splitedMoves = Arrays.asList((moves.trim().split(" ")));

		List<String> idsToSearch = splitedMoves.stream().map(
				m -> splitedMoves.subList(0, splitedMoves.indexOf(m) + 1).stream().collect(Collectors.joining(" ")))
				.collect(Collectors.toList());

		Iterable<OpeningTree> openings = openingRepository.findAllById(idsToSearch);

		List<MovesOpeningPair> results = new ArrayList<MovesOpeningPair>();
		openings.forEach(o -> results.add(new MovesOpeningPair(o.getId(), o.getName())));
		return results;
	}

	@GetMapping("/opening")
	public ResponseEntity<String> getOpening(@RequestParam String moves) throws Exception {

		Optional<OpeningTree> opening = openingRepository.findById(moves);
		if (opening.isPresent()) {
			return new ResponseEntity<String>(openingRepository.findById(moves).get().getName(), HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}

	}

	@PutMapping("/match/{matchId}")

	public DeferredResult<List<MoveAnalysis>> analizeMatch(@PathVariable("matchId") long matchId) {

		DeferredResult<List<MoveAnalysis>> deferredResult = new DeferredResult<>();

		ForkJoinPool.commonPool().submit(() -> {
			try {
				deferredResult.setResult(analysisService.analyseMatch(matchId));
			} catch (Exception e) {
				deferredResult.setErrorResult(e);
			}
		});

		return deferredResult;
	}

}
