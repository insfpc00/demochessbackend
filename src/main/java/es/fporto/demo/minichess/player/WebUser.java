package es.fporto.demo.minichess.player;

import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowire;

import es.fporto.demo.minichess.match.model.MatchMessage;
import es.fporto.demo.minichess.match.model.MatchMessage.MessageType;
import es.fporto.demo.minichess.match.model.MatchMessage.NewEloRating;
import es.fporto.demo.minichess.match.service.MatchService;
import es.fporto.demo.minichess.model.Challenge;
import es.fporto.demo.minichess.model.Move;
import es.fporto.demo.minichess.user.model.User;

@Entity
@Configurable(autowire=Autowire.BY_TYPE)
public class WebUser extends User{

	@Transient
    @Autowired
	private SimpMessagingTemplate messagingTemplate;

	public WebUser() {
	}
	
	@Override
	public void notifyMatchMessage(Long matchId, MessageType message, Optional<NewEloRating> newElo) {
		messagingTemplate.convertAndSend(String.format(MatchService.DESTINATION_FORMAT, this.getUsername(), matchId),
				new MatchMessage(message, newElo));
	}

	@Override
	public void notifyAcceptedChallenge(Challenge acceptedChallenge) {
		
	}

	@Override
	public void notifyMove(Move move) {
		messagingTemplate.convertAndSend(
				String.format(MatchService.MOVE_DESTINATION_FORMAT, this.getUsername(), move.getMatch().getMatchId()), move);
		
	}

}
