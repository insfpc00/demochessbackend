package es.fporto.demo.minichess.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class PuzzleSolution {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@ManyToOne
	private Puzzle puzzle;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<SimpleMove> moves;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<SimpleMove> movesInResponse;

	public PuzzleSolution(Puzzle puzzle, List<SimpleMove> moves,List<SimpleMove> movesInResponse) {
		super();
		this.puzzle = puzzle;
		this.moves = moves;
		this.movesInResponse = movesInResponse;
	}
	
	public PuzzleSolution(List<SimpleMove> moves,List<SimpleMove> movesInResponse) {
		super();
		this.moves = moves;
		this.movesInResponse = movesInResponse;
	}
	
	public PuzzleSolution() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Puzzle getPuzzle() {
		return puzzle;
	}

	public void setPuzzle(Puzzle puzzle) {
		this.puzzle = puzzle;
	}

	public List<SimpleMove> getMoves() {
		return moves;
	}

	public void setMoves(List<SimpleMove> moves) {
		this.moves = moves;
	}

	public List<SimpleMove> getMovesInResponse() {
		return movesInResponse;
	}

	public void setMovesInResponse(List<SimpleMove> movesInResponse) {
		this.movesInResponse = movesInResponse;
	}
	
}
