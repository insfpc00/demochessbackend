package es.fporto.demo.minichess.controller.model;

import java.util.List;

public class GetOpeningNamesResponse {
	
	public static class MovesOpeningPair{
		private String moves;
		private String openingName;
		
		public String getMoves() {
			return moves;
		}
		public void setMoves(String moves) {
			this.moves = moves;
		}
		public String getOpeningName() {
			return openingName;
		}
		public void setOpeningName(String openingName) {
			this.openingName = openingName;
		}
		
		public MovesOpeningPair(String moves, String openingName) {
			super();
			this.moves = moves;
			this.openingName = openingName;
		}
		
		public MovesOpeningPair() {
			super();
		}
	}
	
	private List<MovesOpeningPair> openingNames;

	public List<MovesOpeningPair> getOpeningNames() {
		return openingNames;
	}

	public void setOpeningNames(List<MovesOpeningPair> openingNames) {
		this.openingNames = openingNames;
	}

	public GetOpeningNamesResponse(List<MovesOpeningPair> openingNames) {
		super();
		this.openingNames = openingNames;
	}
	
	public GetOpeningNamesResponse() {}
		 
}
