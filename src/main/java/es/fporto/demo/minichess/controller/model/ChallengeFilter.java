package es.fporto.demo.minichess.controller.model;

import es.fporto.demo.minichess.model.Challenge.ChallengeColor;

public class ChallengeFilter {

	private ChallengeColor color;
	private int timeInSeconds;
	private int timeIncrementInSeconds;
	
	public ChallengeColor getColor() {
		return color;
	}
	public void setColor(ChallengeColor color) {
		this.color = color;
	}
	public int getTimeInSeconds() {
		return timeInSeconds;
	}
	public void setTimeInSeconds(int timeInSeconds) {
		this.timeInSeconds = timeInSeconds;
	}
	public int getTimeIncrementInSeconds() {
		return timeIncrementInSeconds;
	}
	public void setTimeIncrementInSeconds(int timeIncrementInSeconds) {
		this.timeIncrementInSeconds = timeIncrementInSeconds;
	}
	
}
