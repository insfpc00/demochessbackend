package es.fporto.demo.minichess.match.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveList;

import es.fporto.demo.minichess.model.Move;
import es.fporto.demo.minichess.model.Position;

@Component
public class MoveValidator {

	public static class MoveValidationResult {
		private boolean isValid;
		private String newFenPosition;
		private String san;
		private boolean isCheckMate;
		private boolean isStaleMate;
		private boolean isInsuficientMaterial;

		public MoveValidationResult(boolean isValid, String newFenPosition, String san, boolean isCheckMate,
				boolean isStaleMate, boolean isInsuficientMaterial) {
			super();
			this.isValid = isValid;
			this.newFenPosition = newFenPosition;
			this.san = san;
			this.isCheckMate = isCheckMate;
			this.isStaleMate = isStaleMate;
			this.isInsuficientMaterial = isInsuficientMaterial;
		}

		public MoveValidationResult(boolean isValid) {
			this.isValid = isValid;
		}

		public MoveValidationResult() {
			super();
		}

		public boolean isValid() {
			return isValid;
		}

		public void setValid(boolean isValid) {
			this.isValid = isValid;
		}

		public String getNewFenPosition() {
			return newFenPosition;
		}

		public void setNewFenPosition(String newFenPosition) {
			this.newFenPosition = newFenPosition;
		}

		public boolean isCheckMate() {
			return isCheckMate;
		}

		public void setCheckMate(boolean isCheckMate) {
			this.isCheckMate = isCheckMate;
		}

		public boolean isStaleMate() {
			return isStaleMate;
		}

		public void setStaleMate(boolean isStaleMate) {
			this.isStaleMate = isStaleMate;
		}

		public boolean isInsuficientMaterial() {
			return isInsuficientMaterial;
		}

		public void setInsuficientMaterial(boolean isInsuficientMaterial) {
			this.isInsuficientMaterial = isInsuficientMaterial;
		}

		public String getSan() {
			return san;
		}

		public void setSan(String san) {
			this.san = san;
		}

	}

	private static final List<String> positions = Arrays
			.asList(new String[] { "A", "B", "C", "D", "E", "F", "G", "H" });

	private Square positionToSquare(Position position) {

		return Square.fromValue(positions.get(position.getX()) + (7 - position.getY() + 1));
	}

	public MoveValidationResult validateMove(Board board, MoveList moveList, Move move) {

		Square from = positionToSquare(move.getFrom());
		Square to = positionToSquare(move.getTo());

		Piece promotion = Piece.NONE;

		if (move.getPromotedTo() != null) {
			switch (move.getPromotedTo()) {
			case QUEEN:
				promotion = move.getColor() ? Piece.WHITE_QUEEN : Piece.BLACK_QUEEN;
				break;
			case HORSE:
				promotion = move.getColor() ? Piece.WHITE_KNIGHT : Piece.BLACK_KNIGHT;
				break;
			case BISHOP:
				promotion = move.getColor() ? Piece.WHITE_BISHOP : Piece.BLACK_BISHOP;
				break;
			case ROOK:
				promotion = move.getColor() ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;
				break;
			default:
				break;
			}
		}

		com.github.bhlangonijr.chesslib.move.Move boardMove = new com.github.bhlangonijr.chesslib.move.Move(from, to,
				promotion);
		boolean isValid = board.doMove(boardMove, true);
		if (isValid) {

			moveList.add(boardMove);
			String[] sanMoves;
			try {
				sanMoves = moveList.toSanArray();
				return new MoveValidationResult(isValid, board.getFen(), sanMoves[sanMoves.length - 1], board.isMated(),
						board.isStaleMate(), board.isInsufficientMaterial());
			} catch (MoveConversionException e) {
				e.printStackTrace();
			}
		}

		return new MoveValidationResult(false);

	}

}
