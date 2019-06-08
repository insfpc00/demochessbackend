package es.fporto.demo.minichess.controller.model;

public class UserStats {

	private String timeUnit;
	
	private Double[] blitzEloRatings;
	private Double[] bulletEloRatings;
	private Double[] rapidEloRatings;
	
	private String[] labels;
	
	public String getTimeUnit() {
		return timeUnit;
	}
	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}
	public Double[] getBlitzEloRatings() {
		return blitzEloRatings;
	}
	public void setBlitzEloRatings(Double[] blitzEloRatings) {
		this.blitzEloRatings = blitzEloRatings;
	}
	public Double[] getBulletEloRatings() {
		return bulletEloRatings;
	}
	public void setBulletEloRatings(Double[] bulletEloRatings) {
		this.bulletEloRatings = bulletEloRatings;
	}
	public Double[] getRapidEloRatings() {
		return rapidEloRatings;
	}
	public void setRapidEloRatings(Double[] rapidEloRatings) {
		this.rapidEloRatings = rapidEloRatings;
	}
	
	public String[] getLabels() {
		return labels;
	}
	public void setLabels(String[] labels) {
		this.labels = labels;
	}
	
	public UserStats(String timeUnit, Double[] blitzEloRatings, Double[] bulletEloRatings,
			Double[] rapidEloRatings, String[] labels) {
		super();
		this.timeUnit = timeUnit;
		this.blitzEloRatings = blitzEloRatings;
		this.bulletEloRatings = bulletEloRatings;
		this.rapidEloRatings = rapidEloRatings;
	}
	
	public UserStats() {
		super();
	}
	
}
