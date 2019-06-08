package es.fporto.demo.minichess.user.model;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import es.fporto.demo.minichess.model.TimeControlType;
import es.fporto.demo.minichess.player.Player;

@Entity
public abstract class User implements Player{

	public enum FideTitles {
		GM, WGM, IM, WIM, FM, WFM, NM, CM, NONE;
	}

	public enum Themes {
		DEFAULT, DARKLY, CYBORG, PULSE, SOLAR, LUMEN
	}

	@Column
	@Id
	private String username;
	@Column
	@JsonIgnore
	private String password;
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Role> roles;
	@OneToOne
	private Avatar avatar;
	private FideTitles fideTitle;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Calendar dateOfBirth;
	private String firstName;
	private String lastName;
	private String country;
	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	@OrderBy("date DESC")
	private List<EloScore> bulletEloScores;
	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@OrderBy("date DESC")
	@Fetch(FetchMode.SELECT)
	private List<EloScore> blitzEloScores;
	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@OrderBy("date DESC")
	@Fetch(FetchMode.SELECT)
	private List<EloScore> rapidEloScores;
	private Calendar creationDate;
	private Themes theme;
	private boolean soundEnabled;

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public FideTitles getFideTitle() {
		return fideTitle;
	}

	public void setFideTitle(FideTitles fideTitle) {
		this.fideTitle = fideTitle;
	}

	public Calendar getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Calendar dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Avatar getAvatar() {
		return avatar;
	}

	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Double[] getEloRatings() {
		return new Double[] { this.bulletEloScores.get(0).getEloRating(), this.blitzEloScores.get(0).getEloRating(),
				this.rapidEloScores.get(0).getEloRating() };

	}

	public List<EloScore> getBulletEloScores() {
		return bulletEloScores;
	}

	public void setBulletEloScores(List<EloScore> bulletEloScores) {
		this.bulletEloScores = bulletEloScores;
	}

	public List<EloScore> getBlitzEloScores() {
		return blitzEloScores;
	}

	public void setBlitzEloScores(List<EloScore> blitzEloScores) {
		this.blitzEloScores = blitzEloScores;
	}

	public List<EloScore> getRapidEloScores() {
		return rapidEloScores;
	}

	public void setRapidEloScores(List<EloScore> rapidEloScores) {
		this.rapidEloScores = rapidEloScores;
	}

	public Themes getTheme() {
		return theme;
	}

	public void setTheme(Themes theme) {
		this.theme = theme;
	}

	public double getCurrentRating(TimeControlType timeControl) {
		return this.getRatings(timeControl).get(0).getEloRating();
	}

	public List<EloScore> getRatings(TimeControlType timeControl) {
		switch (timeControl) {
		case RAPID:
			return this.rapidEloScores;
		case BLITZ:
			return this.blitzEloScores;
		case BULLET:
			return this.bulletEloScores;
		}
		return null;
	}
	
	public boolean isSoundEnabled() {
		return soundEnabled;
	}

	public void setSoundEnabled(boolean soundEnabled) {
		this.soundEnabled = soundEnabled;
	}

}
