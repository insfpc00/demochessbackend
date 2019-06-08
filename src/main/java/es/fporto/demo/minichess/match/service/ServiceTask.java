package es.fporto.demo.minichess.match.service;

import java.util.TimerTask;
import java.util.function.Consumer;

public class ServiceTask extends TimerTask implements Cloneable{
	
	private MatchService matchService;
	private Consumer<MatchService> matchServiceConsumer;
	
	public ServiceTask clone() {
		return new ServiceTask(matchService,matchServiceConsumer);
	}
		
	public ServiceTask(MatchService matchService,Consumer<MatchService> consumer) {
		super();
		this.matchService=matchService;
		this.matchServiceConsumer=consumer;
	}

	public void run() {
		matchServiceConsumer.accept(matchService);
	}
}