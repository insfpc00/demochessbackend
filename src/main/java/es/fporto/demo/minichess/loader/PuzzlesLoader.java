package es.fporto.demo.minichess.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveList;

import es.fporto.demo.minichess.model.Position;
import es.fporto.demo.minichess.model.Puzzle;
import es.fporto.demo.minichess.model.PuzzleSolution;
import es.fporto.demo.minichess.model.SimpleMove;
import es.fporto.demo.minichess.repository.PuzzleRepository;

@Component
public class PuzzlesLoader {

	@Autowired
	private PuzzleRepository puzzleRepository;
	
	@Value ("${es.fporto.demo.minichess.puzzles.files}")
	private String[] files;
	
	@Value ("${es.fporto.demo.minichess.puzzles.labels.prefix}")
	private String[] prefixes;

	@Value ("${es.fporto.demo.minichess.puzzles.category}")
	private String[] categories;
	
	@Value ("${es.fporto.demo.minichess.puzzles.path}")
	private String path;
	
	@Value ("${es.fporto.demo.minichess.puzzles.complexity}")
	private int[] complexities;
	
	@Value ("${es.fporto.demo.minichess.puzzles.timeInSeconds}")
	private int time;
	
	@Value ("${es.fporto.demo.minichess.puzzles.retries}")
	private int retries;
	
	public void loadPuzzles() {
	
	
		for (int i=0; i<files.length;i++) {
			loadPuzzles(path, files[i],prefixes[i],categories[i],complexities[i],time,retries);
		}
	}
	
	private void loadPuzzles(String path, String file, String prefix, String category,int complexity,int time,int retries) {
		InputStream inputStream = PuzzlesLoader.class.getResourceAsStream(path+"/"+file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		int number=0;
		boolean end = false;
		try {
			while (!end) {
				Puzzle puzzle = readPuzzle(reader,prefix+number++,category,complexity,time,retries);
				puzzleRepository.save(puzzle);
				end = reader.readLine() == null;
			}
		} catch (IOException ioe) {
			System.out.println("Exception while reading input " + ioe);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ioe) {
				System.out.println("Error while closing stream: " + ioe);
			}

		}
	}

	private Puzzle readPuzzle(BufferedReader reader,String label,String category,int complexity,int time, int retries) throws IOException {
		String description = reader.readLine();
		String fen = reader.readLine();
		String solution = reader.readLine();
		solution= solution.replace("...", ".").replaceAll("[\\d]\\.", "").replaceAll("[\\s]+"," ");
		MoveList moveList=new MoveList(fen);
		
		try {
			moveList.loadFromSan(solution);
			PuzzleSolution puzzleSolution= convertMoveList(moveList);
			Puzzle puzzle=new Puzzle(label,fen,Arrays.asList(puzzleSolution),null,complexity,description,time,null,retries,category);
			puzzleSolution.setPuzzle(puzzle);
			return puzzle;
		} catch (MoveConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private PuzzleSolution convertMoveList(MoveList moveList) {

		List<SimpleMove> convertedMoves = moveList.stream().map(move -> convertMove(move, moveList.indexOf(move)))
				.collect(Collectors.toList());

		List<SimpleMove> movesInResponse = convertedMoves.stream()
				.filter(move -> (convertedMoves.indexOf(move) % 2) == 1).collect(Collectors.toList());
		List<SimpleMove> moves = convertedMoves.stream().filter(move -> (convertedMoves.indexOf(move) % 2) == 0)
				.collect(Collectors.toList());

		PuzzleSolution ps = new PuzzleSolution(moves, movesInResponse);

		return ps;
	}

	private SimpleMove convertMove(Move m, int position) {
		return new SimpleMove(convertPositionFromSquare(m.getFrom()), convertPositionFromSquare(m.getTo()), position);
	}

	private Position convertPositionFromSquare(Square s) {
		final String files = "ABCDEFGH";
		final String ranks = "12345678";
		int x = files.indexOf(s.getFile().getNotation());
		int y = 7 - ranks.indexOf(s.getRank().getNotation());
		return new Position(x, y);
	}
}
