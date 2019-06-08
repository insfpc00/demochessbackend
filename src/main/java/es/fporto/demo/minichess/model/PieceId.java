package es.fporto.demo.minichess.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class PieceId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public enum PieceType{
		QUEEN,KING,BISHOP,HORSE,ROOK,PAWN;
	}
	public enum Color{
		WHITE, BLACK;
	}

	private PieceType type;
	private Color color;
	
	public PieceId(PieceType type, Color color) {
		super();
		this.type = type;
		this.color = color;
	}
	
	public PieceId() {
		super();
	}

	public PieceType getType() {
		return type;
	}
	public void setType(PieceType type) {
		this.type = type;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
}
	
