package es.fporto.demo.minichess.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Position {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private int x;
	private int y;
	@OneToOne
	private Move move;

	public Position(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Position(int x, int y, Move move) {
		super();
		this.x = x;
		this.y = y;
		this.move = move;
	}

	public Position() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Move getMove() {
		return move;
	}

	public void setMove(Move move) {
		this.move = move;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Position)) {
			return false;
		}
		Position p = (Position) o;
		return p.x == this.x && p.y == this.y;
	}

}
