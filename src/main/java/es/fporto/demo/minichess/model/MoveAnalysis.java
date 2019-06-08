package es.fporto.demo.minichess.model;

import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class MoveAnalysis {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String bestMove;
	private Integer score;
	private Integer mateInX;
	@OneToOne
	@JsonIgnore
	private Move move;

	
	public MoveAnalysis() {
		super();
	}

	public MoveAnalysis(Move move, String bestMove, Integer score, Integer mateInX) {
		super();
		this.move=move;
		this.bestMove = bestMove;
		this.score = score;
		this.mateInX = mateInX;
	}

	public long getId() {
		return id;
	}

	public Move getMove() {
		return move;
	}

	public void setMove(Move move) {
		this.move = move;
	}

	public String getBestMove() {
		return bestMove;
	}

	public void setBestMove(String bestMove) {
		this.bestMove = bestMove;
	}

	public Optional<Integer> getScore() {
		return Optional.ofNullable(score);
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Optional<Integer> getMateInX() {
		return Optional.ofNullable(mateInX);
	}

	public void setMateInX(Integer mateInX) {
		this.mateInX = mateInX;
	}

}