package es.fporto.demo.minichess.model;

public class MatchStats {

	private long matchesWonAsWhite;
	private long matchesWonAsBlack;
	private long drawMatches;
	private long totalMatches;
	private int timeInSeconds;

	public MatchStats() {
		super();
	}

	public MatchStats(long matchesWonAsWhite, long matchesWonAsBlack, long drawMatches, long totalMatches,
			int timeInSeconds) {
		super();
		this.matchesWonAsWhite = matchesWonAsWhite;
		this.matchesWonAsBlack = matchesWonAsBlack;
		this.drawMatches = drawMatches;
		this.totalMatches = totalMatches;
		this.timeInSeconds = timeInSeconds;
	}
	
	public TimeControlType getType() {
		return TimeControlType.getFromDuration(timeInSeconds);
	}

	public int getTimeInSeconds() {
		return timeInSeconds;
	}

	public void setTimeInSeconds(int timeInSeconds) {
		this.timeInSeconds = timeInSeconds;
	}

	public long getMatchesWonAsWhite() {
		return matchesWonAsWhite;
	}

	public void setMatchesWonAsWhite(long matchesWonAsWhite) {
		this.matchesWonAsWhite = matchesWonAsWhite;
	}

	public long getMatchesWonAsBlack() {
		return matchesWonAsBlack;
	}

	public void setMatchesWonAsBlack(long matchesWonAsBlack) {
		this.matchesWonAsBlack = matchesWonAsBlack;
	}

	public long getDrawMatches() {
		return drawMatches;
	}

	public void setDrawMatches(long drawMatches) {
		this.drawMatches = drawMatches;
	}

	public long getTotalMatches() {
		return totalMatches;
	}

	public void setTotalMatches(long totalMatches) {
		this.totalMatches = totalMatches;
	}
	
	public String toString() {
		return this.drawMatches + "/" + this.matchesWonAsWhite + "/" + this.matchesWonAsBlack +" : " + this.timeInSeconds + " - "+ this.totalMatches;
	}

}
