package es.fporto.demo.minichess.match.model;

import java.util.Optional;

public class MatchMessage {

	public static class NewEloRating {
		private double eloRating;
		private double delta;

		public double getEloRating() {
			return eloRating;
		}

		public void setEloRating(double eloRating) {
			this.eloRating = eloRating;
		}

		public double getDelta() {
			return delta;
		}

		public void setDelta(double delta) {
			this.delta = delta;
		}

		public NewEloRating(double eloRating, double delta) {
			super();
			this.eloRating = eloRating;
			this.delta = delta;
		}
	}

	public enum MessageType {
		USER_DISCONNECTED,WIN_BY_DISCONNECTION, LOSS_BY_DISCONNECTION, USER_OFFERS_DRAW, LOSS_BY_RESIGNATION, WIN_BY_RESIGNATION, DRAW_BY_AGREEMENT, CHECKMATE, CHECKMATED,
		STALEMATE, THREEFOLD_REPETITION_DRAW , LOSS_ON_TIME, WIN_BY_TIME, WRONG_MOVE, MATCH_STARTED, CHAT_DISABLED, INSUFFICIENT_MATERIAL,FIFTY_MOVE_RULE_REPETITION_DRAW
	}

	private MessageType message;
	private Optional<NewEloRating> newEloRating;

	public MatchMessage(MessageType message, Optional<NewEloRating> newEloRating) {
		super();
		this.message = message;
		this.newEloRating = newEloRating;
	}
	
	public MatchMessage(MessageType message) {
		super();
		this.message = message;
		this.newEloRating = Optional.empty();
	}

	public MatchMessage() {
		super();

	}

	public Optional<NewEloRating> getNewEloRating() {
		return newEloRating;
	}

	public void setNewEloRating(Optional<NewEloRating> newEloRating) {
		this.newEloRating = newEloRating;
	}

	public MessageType getMessage() {
		return message;
	}

	public void setMessage(MessageType message) {
		this.message = message;
	}

}
