package es.fporto.demo.minichess.model;

import java.util.Calendar;

import es.fporto.demo.minichess.user.model.UserDto;

public class MatchDto {

	private long matchId;
	private Calendar creationDate;
	private UserDto whitePiecesUser;
	private UserDto blackPiecesUser;
	private int timeInSeconds;
	private int timeIncrementInSeconds;
	private MatchState state;
	
	public long getMatchId() {
		return matchId;
	}
	public MatchDto(long matchId, Calendar creationDate, UserDto whitePiecesUser, UserDto blackPiecesUser,
			int timeInSeconds, int timeIncrementInSeconds) {
		super();
		this.matchId = matchId;
		this.creationDate = creationDate;
		this.whitePiecesUser = whitePiecesUser;
		this.blackPiecesUser = blackPiecesUser;
		this.timeInSeconds = timeInSeconds;
		this.timeIncrementInSeconds = timeIncrementInSeconds;
	}
	
	public MatchDto() {
		super();
	}
	
	public MatchDto(Match match) {
		super();
		this.matchId=match.getMatchId();
		this.creationDate=match.getCreationDate();
		this.whitePiecesUser=new UserDto(match.getWhitePiecesUser());
		this.blackPiecesUser=new UserDto(match.getBlackPiecesUser());
		this.timeInSeconds=match.getTimeInSeconds();
		this.timeIncrementInSeconds=match.getTimeIncrementInSeconds();
		this.setState(match.getState());
		
	}
	public void setMatchId(long matchId) {
		this.matchId = matchId;
	}
	public Calendar getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public UserDto getWhitePiecesUser() {
		return whitePiecesUser;
	}
	public void setWhitePiecesUser(UserDto whitePiecesUser) {
		this.whitePiecesUser = whitePiecesUser;
	}
	public UserDto getBlackPiecesUser() {
		return blackPiecesUser;
	}
	public void setBlackPiecesUser(UserDto blackPiecesUser) {
		this.blackPiecesUser = blackPiecesUser;
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
	public MatchState getState() {
		return state;
	}
	public void setState(MatchState state) {
		this.state = state;
	}
	
}
