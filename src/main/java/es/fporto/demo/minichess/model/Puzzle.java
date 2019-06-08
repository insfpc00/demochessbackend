package es.fporto.demo.minichess.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Puzzle {

	@Id
	private String label;
	private String startingFen;
	@OneToMany (cascade=CascadeType.ALL)
	@JsonIgnore
	private List<PuzzleSolution> solutions;
	private int complexity;
	private String description;
	private long secondsAvailable;
	@OneToOne(cascade=CascadeType.ALL)
	private SimpleMove firstMove;
	@ElementCollection
	private List<String> tips;
	private int numberOfWrongMovesAllowed;
	private String category;
	
	public Puzzle() {
		super();
	}
	
	public Puzzle(String label, String startingFen, List<PuzzleSolution> solutions, SimpleMove firstMove, int complexity, String description,
			long secondsAvailable, List<String> tips,int numberOfWrongMovesAllowed,String category) {
		super();
		this.label = label;
		this.startingFen = startingFen;
		this.solutions = solutions;
		this.complexity = complexity;
		this.description = description;
		this.firstMove= firstMove;
		this.secondsAvailable = secondsAvailable;
		this.tips = tips;
		this.numberOfWrongMovesAllowed = numberOfWrongMovesAllowed;
		this.category = category;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getStartingFen() {
		return startingFen;
	}
	public void setStartingFen(String startingFen) {
		this.startingFen = startingFen;
	}
	public List<PuzzleSolution> getSolutions() {
		return solutions;
	}
	public void setSolutions(List<PuzzleSolution> solutions) {
		this.solutions = solutions;
	}
	public int getComplexity() {
		return complexity;
	}
	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getSecondsAvailable() {
		return secondsAvailable;
	}
	public void setSecondsAvailable(long secondsAvailable) {
		this.secondsAvailable = secondsAvailable;
	}
	public List<String> getTips() {
		return tips;
	}
	public void setTips(List<String> tips) {
		this.tips = tips;
	}

	public SimpleMove getFirstMove() {
		return firstMove;
	}

	public void setFirstMove(SimpleMove firstMove) {
		this.firstMove = firstMove;
	}

	public int getNumberOfWrongMovesAllowed() {
		return numberOfWrongMovesAllowed;
	}

	public void setNumberOfWrongMovesAllowed(int numberOfWrongMovesAllowed) {
		this.numberOfWrongMovesAllowed = numberOfWrongMovesAllowed;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
