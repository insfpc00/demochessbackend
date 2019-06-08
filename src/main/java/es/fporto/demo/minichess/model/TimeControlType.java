package es.fporto.demo.minichess.model;

public enum TimeControlType {
	BULLET, BLITZ, RAPID;

	public static TimeControlType getFromDuration(int durationInSeconds) {
		if (durationInSeconds >= 600) {
			return RAPID;
		} else if (durationInSeconds >= 300) {
			return BLITZ;
		} else {
			return BULLET;
		}
	}
	
}