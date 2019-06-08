package es.fporto.demo.minichess.uciclient.stockfish;

public enum StockFishVariant {
	BMI2("_bmi2"), POPCNT("_popcnt"), DEFAULT("");
	
	private String suffix;

	public String getSuffix() {
		return suffix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	private StockFishVariant(String suffix) {
		this.suffix = suffix;
	}
}
