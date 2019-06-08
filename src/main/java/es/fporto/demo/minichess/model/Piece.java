package es.fporto.demo.minichess.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import es.fporto.demo.minichess.model.PieceId.Color;
import es.fporto.demo.minichess.model.PieceId.PieceType;

@Entity
public class Piece {

	
	@EmbeddedId
	private PieceId id;
	
	public Piece(PieceType type, Color color) {
		super();
		this.id= new PieceId(type,color);
	}
	
	public void setId(PieceId id) {
		this.id = id;
	}

	public Piece() {
		super();
		this.id= new PieceId();
	}

	public PieceType getType() {
		return id.getType();
	}
	
	public PieceId getId() {
		return id;
	}

	public Color getColor() {
		return id.getColor();
	}
	
	public boolean isWhite() {
		return (this.getColor().equals(Color.WHITE));
	}
	
	public void setColor(Color color) {
		id.setColor(color);
	}
	
	public void setType(PieceType type) {
		id.setType(type);
	}
		

}