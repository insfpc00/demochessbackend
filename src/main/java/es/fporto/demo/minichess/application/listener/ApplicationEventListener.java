package es.fporto.demo.minichess.application.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import es.fporto.demo.minichess.player.UCIEngine;
import es.fporto.demo.minichess.repository.UserRepository;
import es.fporto.demo.minichess.user.model.User;

@Component

public class ApplicationEventListener {

	@Autowired
	private UserRepository userRepository;
	
	@EventListener
	public void afterInit(final ApplicationReadyEvent event) {
		List<User> engines=userRepository.findAllEngines();
		engines.forEach(e -> ((UCIEngine) e).activate());
	}
	
}
