package es.fporto.demo.minichess.user.model;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class EloScore {

	@Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
	@ManyToOne
	@JsonIgnore
	private User user;
	@JsonIgnore
	private Double eloRating;
    @Temporal(TemporalType.DATE)
	private Calendar date;

	public EloScore() {
		super();
	}
	
	public EloScore(User user, double eloRating, Calendar date) {
		super();
		this.user = user;
		this.eloRating = eloRating;
		this.date = date;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Double getEloRating() {
		return eloRating;
	}
	public void setEloRating(Double eloRating) {
		this.eloRating = eloRating;
	}
	public Calendar getDate() {
		return date;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
