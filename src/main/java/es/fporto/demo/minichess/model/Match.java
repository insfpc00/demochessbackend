package es.fporto.demo.minichess.model;

import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import es.fporto.demo.minichess.user.model.User;

@Entity
public class Match {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long matchId;
	
	@ManyToOne
	private User whitePiecesUser;
	@ManyToOne
	private User blackPiecesUser;
	@OneToMany(mappedBy="match", cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Move> moves;
	
	private Calendar creationDate;
	private int timeInSeconds;
	private int timeIncrementInSeconds;
	private MatchState state;
	


	public Match(User whitePiecesUser, User blackPiecesUser, List<Move> moves, Calendar creationDate, int timeInSeconds,
			int timeIncrementInSeconds) {
		super();
		this.whitePiecesUser = whitePiecesUser;
		this.blackPiecesUser = blackPiecesUser;
		this.moves = moves;
		this.creationDate = creationDate;
		this.timeInSeconds = timeInSeconds;
		this.timeIncrementInSeconds = timeIncrementInSeconds;
		this.state=MatchState.STARTED;
	}
	
	public Match() {
		super();
	}
	
	public TimeControlType getType() {
		if (timeInSeconds >= 60*10) {
			return TimeControlType.RAPID;
		} else if (timeInSeconds >= 30*10) {
			return TimeControlType.BLITZ;
		} else {
			return TimeControlType.BULLET;
		}
	}

	public MatchState getState() {
		return state;
	}

	public void setState(MatchState state) {
		this.state = state;
	}

	public long getMatchId() {
		return matchId;
	}

	public void setMatchId(long matchId) {
		this.matchId = matchId;
	}

	public User getWhitePiecesUser() {
		return whitePiecesUser;
	}

	public void setWhitePiecesUser(User whitePiecesUser) {
		this.whitePiecesUser = whitePiecesUser;
	}

	public User getBlackPiecesUser() {
		return blackPiecesUser;
	}

	public void setBlackPiecesUser(User blackPiecesUser) {
		this.blackPiecesUser = blackPiecesUser;
	}

	public List<Move> getMoves() {
		return moves;
	}

	public void setMoves(List<Move> moves) {
		this.moves = moves;
	}
	
	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public int getTimeInSeconds() {
		return timeInSeconds;
	}

	public void setTimeInSeconds(int timeInSeconds) {
		this.timeInSeconds = timeInSeconds;
	}

	public int getTimeIncrementInSeconds() {
		return timeIncrementInSeconds;
	}

	public void setTimeIncrementInSeconds(int timeIncrementInSeconds) {
		this.timeIncrementInSeconds = timeIncrementInSeconds;
	}
	
}
