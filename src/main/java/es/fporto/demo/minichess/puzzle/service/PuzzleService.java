package es.fporto.demo.minichess.puzzle.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.fporto.demo.minichess.model.PlayedPuzzle;
import es.fporto.demo.minichess.model.Puzzle;
import es.fporto.demo.minichess.model.PuzzleSolution;
import es.fporto.demo.minichess.model.SimpleMove;
import es.fporto.demo.minichess.repository.PlayedPuzzleRepository;
import es.fporto.demo.minichess.repository.PuzzleRepository;
import es.fporto.demo.minichess.repository.SimpleMoveRepository;
import es.fporto.demo.minichess.repository.UserRepository;
import es.fporto.demo.minichess.user.model.User;

@Service
public class PuzzleService {

	@Autowired
	private PuzzleRepository puzzleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PlayedPuzzleRepository playedPuzzleRepository;

	@Autowired
	private SimpleMoveRepository simplemoveRepository;

	public Puzzle playPuzzle(String label, String username) {
		Optional<Puzzle> puzzleOpt = puzzleRepository.findById(label);
		if (puzzleOpt.isEmpty()) {
			return null;
		}
		Puzzle puzzle = puzzleOpt.get();

		User user = userRepository.findById(username).get();

		Optional<PlayedPuzzle> playedPuzzleOpt = playedPuzzleRepository.findByPlayerAndPuzzle(user, puzzle);

		if (!playedPuzzleOpt.isEmpty()) {
			playedPuzzleRepository.delete(playedPuzzleOpt.get());
		}

		PlayedPuzzle newPlayedPuzzle = new PlayedPuzzle(user, puzzle, 0l, false);

		playedPuzzleRepository.save(newPlayedPuzzle);

		return puzzle;
	}

	public List<Puzzle> getAllPuzzles() {
		return puzzleRepository.findAll();
	}

	public List<PlayedPuzzle> getAllPlayedPuzzles(String username) {
		return playedPuzzleRepository.findByPlayerUsername(username);
	}

	public MoveResponse move(String puzzle, String username, SimpleMove move) {
		Optional<PlayedPuzzle> ppOpt = playedPuzzleRepository.findByPlayerUsernameAndPuzzleLabel(username, puzzle);
		PlayedPuzzle pp = ppOpt.get();
		List<PuzzleSolution> solutions = pp.getPuzzle().getSolutions();
		List<SimpleMove> tempMoves = new ArrayList<>(pp.getMoves());
		tempMoves.add(simplemoveRepository.save(move));
		List<PuzzleSolution> validSolutions = solutions.stream().filter(s -> isValidSolution(s, tempMoves)).limit(1)
				.collect(Collectors.toList());
		if (validSolutions.isEmpty()) {
			return new MoveResponse(null, false);
		} else {
			MoveResponse result;
			pp.getMoves().add(simplemoveRepository.save(move));
			PuzzleSolution solution = validSolutions.iterator().next();
			if (solution.getMovesInResponse().size() >= pp.getMoves().size()) {
				result= new MoveResponse(solution.getMovesInResponse().get(pp.getMoves().size()-1), false);
			} else {
				pp.setEnded(true);
				result = new MoveResponse(null, true);
			}
			playedPuzzleRepository.save(pp);
			return result;
		}

	}

	private boolean isValidSolution(PuzzleSolution puzzleSolution, List<SimpleMove> moves) {

		return moves.size() <= puzzleSolution.getMoves().size() && moves.stream().allMatch(m -> {
			SimpleMove m2 = puzzleSolution.getMoves().get(moves.indexOf(m));
			return m2.getFrom().equals(m.getFrom()) && m2.getTo().equals(m.getTo());
		});
	}
}
