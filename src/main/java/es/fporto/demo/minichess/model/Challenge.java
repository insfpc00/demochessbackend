package es.fporto.demo.minichess.model;

import java.util.Calendar;

public class Challenge {

	public enum ChallengeColor{
		WHITE,BLACK,RANDOM;
	}
	private long challengeId;
	private String userName;
	private double eloRating;
	private Calendar creationDate;
	private ChallengeColor color;
	private int timeInSeconds;
	private int timeIncrementInSeconds;
	private long matchId;
	private boolean fromHuman;

	public Challenge(long challengeId,String userName, Calendar creationDate, ChallengeColor color, int timeInSeconds,
			int timeIncrementInSeconds) {
		super();
		this.challengeId = challengeId;
		this.userName = userName;
		this.creationDate = creationDate;
		this.color = color;
		this.timeInSeconds = timeInSeconds;
		this.timeIncrementInSeconds = timeIncrementInSeconds;
		this.fromHuman=true;
	}
	
	public Challenge(String userName, ChallengeColor color,
			int timeInSeconds, int timeIncrementInSeconds, boolean fromHuman) {
		super();
		this.userName = userName;
		this.color = color;
		this.timeInSeconds = timeInSeconds;
		this.timeIncrementInSeconds = timeIncrementInSeconds;
		this.fromHuman = fromHuman;
	}

	public Challenge(String userName, ChallengeColor color, int timeInSeconds,
			int timeIncrementInSeconds) {
		super();
		this.userName = userName;
		this.color = color;
		this.timeInSeconds = timeInSeconds;
		this.timeIncrementInSeconds = timeIncrementInSeconds;
		this.fromHuman=true;
	}
	
	public Challenge() {
		super();
		this.fromHuman=true;
	}

	public boolean isFromHuman() {
		return fromHuman;
	}

	public void setFromHuman(boolean fromHuman) {
		this.fromHuman = fromHuman;
	}

	public long getMatchId() {
		return matchId;
	}

	public void setMatchId(long matchId) {
		this.matchId = matchId;
	}
	
	public long getChallengeId() {
		return challengeId;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUser(String userName) {
		this.userName = userName;
	}
	public Calendar getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}
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
	
	public void setChallengeId(long challengeId) {
		this.challengeId = challengeId;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public double getEloRating() {
		return eloRating;
	}
	public void setEloRating(double eloRating) {
		this.eloRating = eloRating;
	}

	public boolean matches(ChallengeColor color, int timeInSeconds, int timeIncrementInSeconds) {
		
		return (color == ChallengeColor.RANDOM || this.color == ChallengeColor.RANDOM
				|| color == ChallengeColor.WHITE && this.color == ChallengeColor.BLACK
				|| color == ChallengeColor.BLACK && this.color == ChallengeColor.WHITE)
				&& timeInSeconds == this.timeInSeconds
				&& timeIncrementInSeconds == this.timeIncrementInSeconds;
		
		
	}
	
	}
