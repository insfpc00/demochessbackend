package es.fporto.demo.minichess.model;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import es.fporto.demo.minichess.user.model.User;

@Entity
public class PlayedPuzzle {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@ManyToOne
	private User player;
	@ManyToOne
	private Puzzle puzzle;
	
	private Calendar createdAt;
	private long milliSecondsSpent;
	private boolean ended;
	
	@OneToMany
	private List<SimpleMove> moves;

	public PlayedPuzzle() {
		super();
	}
	
	public PlayedPuzzle(User player, Puzzle puzzle, long milliSecondsSpent, boolean ended) {
		this();
		this.player = player;
		this.puzzle = puzzle;
		this.milliSecondsSpent = milliSecondsSpent;
		this.ended = ended;
		this.createdAt = Calendar.getInstance();
	}
	public long getId() {
		return id;
	}
	public User getPlayer() {
		return player;
	}
	public void setPlayer(User player) {
		this.player = player;
	}
	public Puzzle getPuzzle() {
		return puzzle;
	}
	public void setPuzzle(Puzzle puzzle) {
		this.puzzle = puzzle;
	}
	public long getMilliSecondsSpent() {
		return milliSecondsSpent;
	}
	public void setMilliSecondsSpent(long milliSecondsSpent) {
		this.milliSecondsSpent = milliSecondsSpent;
	}
	public boolean isEnded() {
		return ended;
	}
	public void setEnded(boolean ended) {
		this.ended = ended;
	}

	public Calendar getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Calendar createdAt) {
		this.createdAt = createdAt;
	}

	public List<SimpleMove> getMoves() {
		return moves;
	}

	public void setMoves(List<SimpleMove> moves) {
		this.moves = moves;
	}
	
}
