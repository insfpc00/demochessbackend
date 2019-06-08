package es.fporto.demo.minichess.player;

import java.util.Optional;

import es.fporto.demo.minichess.match.model.MatchMessage;
import es.fporto.demo.minichess.match.model.MatchMessage.MessageType;
import es.fporto.demo.minichess.model.Challenge;
import es.fporto.demo.minichess.model.Move;


public interface Player{

	public abstract void notifyMatchMessage(Long matchId, MessageType message,
			Optional<MatchMessage.NewEloRating> newElo);
	
	public abstract void notifyAcceptedChallenge(Challenge acceptedChallenge);
	
	public abstract void notifyMove(Move move);
}
