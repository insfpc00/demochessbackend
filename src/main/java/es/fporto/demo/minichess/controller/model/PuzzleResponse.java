package es.fporto.demo.minichess.controller.model;

import java.util.Calendar;
import java.util.Optional;

import es.fporto.demo.minichess.model.Puzzle;

public class PuzzleResponse {
 
	private Puzzle puzzle;
	private Boolean ended;
	private Optional<Calendar> lastTry;
	
	public PuzzleResponse(Puzzle puzzle, Boolean ended, Optional<Calendar> lastTry) {
		super();
		this.puzzle = puzzle;
		this.ended = ended;
		this.lastTry = lastTry;
	}
	public Puzzle getPuzzle() {
		return puzzle;
	}
	public void setPuzzle(Puzzle puzzle) {
		this.puzzle = puzzle;
	}
	public Boolean getEnded() {
		return ended;
	}
	public void setEnded(Boolean ended) {
		this.ended = ended;
	}
	public Optional<Calendar> getLastTry() {
		return lastTry;
	}
	public void setLastTry(Optional<Calendar> lastTry) {
		this.lastTry = lastTry;
	}
	
	
}
