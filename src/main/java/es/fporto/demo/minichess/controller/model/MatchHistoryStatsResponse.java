package es.fporto.demo.minichess.controller.model;

import es.fporto.demo.minichess.model.MatchStats;

public class MatchHistoryStatsResponse {
	
	private MatchStats blitzStats;
	private MatchStats rapidStats;
	private MatchStats bulletStats;

	public MatchHistoryStatsResponse() {
		super();
	}

	public MatchHistoryStatsResponse(MatchStats rapidStats, MatchStats blitzStats, MatchStats bulletStats) {
		super();
		this.blitzStats = blitzStats;
		this.rapidStats = rapidStats;
		this.bulletStats = bulletStats;
	}

	public MatchStats getBlitzStats() {
		return blitzStats;
	}
  
	public void setBlitzStats(MatchStats blitzStats) {
		this.blitzStats = blitzStats;
	}

	public MatchStats getRapidStats() {
		return rapidStats;
	}

	public void setRapidStats(MatchStats rapidStats) {
		this.rapidStats = rapidStats;
	}

	public MatchStats getBulletStats() {
		return bulletStats;
	}

	public void setBulletStats(MatchStats bulletStats) {
		this.bulletStats = bulletStats;
	}

	
}
