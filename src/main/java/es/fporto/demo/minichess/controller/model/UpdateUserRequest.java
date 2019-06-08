package es.fporto.demo.minichess.controller.model;

import java.util.Optional;

import es.fporto.demo.minichess.user.model.User.FideTitles;
import es.fporto.demo.minichess.user.model.User.Themes;

public class UpdateUserRequest {

	private Optional<FideTitles> title;
	private Optional<Themes> theme;
	private Optional<String> country;
	private Optional<Boolean> soundEnabled;
	
	public UpdateUserRequest() {
		super();
	}
	
	public Optional<FideTitles> getTitle() {
		return title;
	}
	public void setTitle(Optional<FideTitles> title) {
		this.title = title;
	}
	public Optional<Themes> getTheme() {
		return theme;
	}
	public void setTheme(Optional<Themes> theme) {
		this.theme = theme;
	}
	public Optional<String> getCountry() {
		return country;
	}
	public void setCountry(Optional<String> country) {
		this.country = country;
	}
	
	public Optional<Boolean> getSoundEnabled() {
		return soundEnabled;
	}

	public void setSoundEnabled(Optional<Boolean> soundEnabled) {
		this.soundEnabled = soundEnabled;
	}	
}