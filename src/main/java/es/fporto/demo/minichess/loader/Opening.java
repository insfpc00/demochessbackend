package es.fporto.demo.minichess.loader;

public class Opening {
	private String ecoCode;
	private String name;
	private String moves;

	public Opening() {
		super();
	}

	public Opening(String ecoCode, String name, String moves) {
		super();
		this.ecoCode = ecoCode;
		this.name = name;
		this.moves = moves;
	}

	public String getEcoCode() {
		return ecoCode;
	}

	public void setEcoCode(String ecoCode) {
		this.ecoCode = ecoCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMoves() {
		return moves;
	}

	public void setMoves(String moves) {
		this.moves = moves;
	}

}
