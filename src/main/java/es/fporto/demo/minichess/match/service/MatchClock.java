package es.fporto.demo.minichess.match.service;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.Timer;

public class MatchClock implements ActionListener{

	private ServiceTask task;
	private Timer timer;
	private int timeInMillis;
	private int timeIncrementInMMillis;
	private long lastUpdate;
	public MatchClock(ServiceTask task,int timeInSeconds, int timeIncrementInSeconds) {
		super();
		this.task = task;
		
		this.timeInMillis=timeInSeconds*1000;
		
		this.timeIncrementInMMillis=timeIncrementInSeconds*1000;
		this.timer = new Timer(timeInMillis, this);
		this.timer.stop();
		this.timer.setRepeats(false);
		
	}

	public void stop() {
		timeInMillis+= lastUpdate - Calendar.getInstance().getTimeInMillis() + timeIncrementInMMillis;
		timeInMillis = timeInMillis<0 ?0 :timeInMillis;
		timer.stop();
	}
	
	public int getTimeInMillis() {
		return timeInMillis;
	}

	public void start() {
		lastUpdate=Calendar.getInstance().getTimeInMillis();
		timer.setInitialDelay(timeInMillis);
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		task.run();
	}
	
}
