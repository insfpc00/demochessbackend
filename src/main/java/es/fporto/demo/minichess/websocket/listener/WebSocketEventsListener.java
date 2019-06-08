package es.fporto.demo.minichess.websocket.listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import es.fporto.demo.minichess.challenge.service.ChallengeService;
import es.fporto.demo.minichess.match.service.MatchService;;

@Component
public class WebSocketEventsListener {

	@Autowired
	private MatchService matchService;
	@Autowired
	private ChallengeService challengeService;

	private static Map<String, Map<String, String>> subscriptions = new HashMap<>();

	@EventListener
	public void handleSubscribe(final SessionSubscribeEvent event) {
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());

		if (!subscriptions.containsKey(headers.getSessionId())) {
			subscriptions.put(headers.getSessionId(), new HashMap<String, String>());
		}

		if (!headers.getNativeHeader("id").isEmpty()) {
			subscriptions.get(headers.getSessionId()).put(headers.getNativeHeader("id").iterator().next(),
					headers.getDestination());
		}
	}

	@EventListener
	public void handleUnSubscribe(final SessionUnsubscribeEvent event) {
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());

		if (subscriptions.containsKey(headers.getSessionId())) {
			if (!headers.getNativeHeader("id").isEmpty()) {
				String subId = headers.getNativeHeader("id").iterator().next();
				String destination = subscriptions.get(headers.getSessionId()).get(subId);
				handleUnSubscribes(headers.getUser().getName(), Arrays.asList(destination));
				subscriptions.get(headers.getSessionId()).remove(subId);

			}
		}
	}

	@EventListener
	public void handleDisconnect(final SessionDisconnectEvent event) {

		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());

		if (subscriptions.containsKey(headers.getSessionId())) {
			handleUnSubscribes(headers.getUser().getName(), subscriptions.get(headers.getSessionId()).values());
			subscriptions.get(headers.getSessionId()).clear();
		}

	}

	private void handleUnSubscribes(String username, Collection<String> destinations) {

		for (String destination : destinations) {
			if (destination.contains(MatchService.DESTINATION)) {
				String[] splittedDestination = destination.split("\\.");
				String matchIdStr = splittedDestination[splittedDestination.length - 1];
				Long matchId = Long.valueOf(matchIdStr);
				this.matchService.userDisconnects(username, matchId);
			} else if (destination.contains(ChallengeService.ACCEPTED_CHALLENGES_TOPIC)) {
				challengeService.removeChallengesFromUser(username);
			}
		}
	}

}