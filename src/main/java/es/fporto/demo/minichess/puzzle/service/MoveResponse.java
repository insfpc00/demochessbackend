package es.fporto.demo.minichess.puzzle.service;

import es.fporto.demo.minichess.model.SimpleMove;

public class MoveResponse {
	private SimpleMove move;
	private boolean finalMove;
	
	public MoveResponse() {
		super();
	}
	public MoveResponse(SimpleMove move, boolean finalMove) {
		super();
		this.move = move;
		this.finalMove = finalMove;
	}
	public SimpleMove getMove() {
		return move;
	}
	public void setMove(SimpleMove move) {
		this.move = move;
	}
	public boolean isFinalMove() {
		return finalMove;
	}
	public void setFinalMove(boolean finalMove) {
		this.finalMove = finalMove;
	}
	
}
