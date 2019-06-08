package es.fporto.demo.minichess.websocket.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import es.fporto.demo.minichess.challenge.service.ChallengeService;
import es.fporto.demo.minichess.controller.model.ChatMessage;
import es.fporto.demo.minichess.controller.model.UserMatchMessage;
import es.fporto.demo.minichess.match.service.MatchService;
import es.fporto.demo.minichess.model.Challenge;
import es.fporto.demo.minichess.model.Move;

@Controller
public class WebSocketController {

	public final static String CHAT_DESTINATION_FORMAT = "/user/%s/exchange/amq.direct/chat";

	@Autowired
	private ChallengeService challengeService;
	@Autowired
	private MatchService matchService;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/move.{matchId}")
	public void move(@Payload Move move, @DestinationVariable("matchId") long matchId) {
		matchService.move(matchId, move);
	}

	@MessageMapping("/match.{matchId}")
	public void matchMessage(@Payload UserMatchMessage userMatchMessage, @DestinationVariable String matchId,
			Principal principal) {

		Long match = Long.valueOf(matchId);

		switch (userMatchMessage.getMessage()) {
		case USER_RESIGNS:
			matchService.userResigns(principal.getName(), match);
			break;
		case USER_ACCEPTS_DRAW:
			matchService.userAcceptsDraw(principal.getName(), match);
			break;
		case USER_OFFERS_DRAW:
			matchService.userOffersDraw(principal.getName(), match);
			break;
		case USER_READY:
			matchService.userReady(principal.getName(), match);
			break;
		case CHAT_DISABLED:
			matchService.chatDisabled(principal.getName(), match);
			break;
		default:
			break;
		}
	}

	@MessageMapping("/chat")
	public void chatMessage(@Payload ChatMessage chatMessage, Principal principal) {
		if (principal.getName().equals(chatMessage.getFrom())) {
			messagingTemplate.convertAndSend(String.format(CHAT_DESTINATION_FORMAT, chatMessage.getTo()), chatMessage);
		}
	}

	@SubscribeMapping("/topic/challenges")
	public List<Challenge> retrieveChallenges() {
		return challengeService.getAllChallenges();
	}

}
