package es.fporto.demo.minichess.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.fporto.demo.minichess.model.PieceId.PieceType;

@Entity
public class Move extends SimpleMove{

	
	private int number;
	private Boolean color;
	@ManyToOne
	@JsonIgnore
	private Match match;
	private PieceType promotedTo;
	private String acn;
	private long timeInMillis;
	private String fenPosition;
	
	@OneToOne(cascade=CascadeType.ALL)
	//@JsonIgnore
	private MoveAnalysis analysis;
	
	
	public Move() {
		super();
	}
	public Move(int number, Boolean color, Position from, Position to,PieceType promotedTo, String acn, long timeInMillis) {
		super(from,to,number);
		this.number = number;
		this.color = color;
		this.promotedTo = promotedTo;
		this.acn = acn;
		this.timeInMillis = timeInMillis;
	}
	
	public long getTimeInMillis() {
		return timeInMillis;
	}
	public void setTimeInMillis(long timeInMillis) {
		this.timeInMillis = timeInMillis;
	}
	public PieceType getPromotedTo() {
		return promotedTo;
	}

	public void setPromotedTo(PieceType promotedTo) {
		this.promotedTo = promotedTo;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Boolean getColor() {
		return color;
	}
	public void setColor(Boolean color) {
		this.color = color;
	}
	
	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public String getAcn() {
		return acn;
	}

	public void setAcn(String acn) {
		this.acn = acn;
	}
	
	public String getFenPosition() {
		return fenPosition;
	}
	public void setFenPosition(String fenPosition) {
		this.fenPosition = fenPosition;
	}
	
	public MoveAnalysis getAnalysis() {
		return analysis;
	}
	public void setAnalysis(MoveAnalysis analysis) {
		this.analysis = analysis;
	}

	
}
