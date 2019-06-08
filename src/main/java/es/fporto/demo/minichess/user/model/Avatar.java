package es.fporto.demo.minichess.user.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Avatar {

	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
	private String fileType;
	@Lob
	private byte[] data;
	@OneToOne
	@JsonIgnore
	private User user;
	
	public Avatar() {
		super();
	}
	
	public Avatar(String fileType, byte[] data, User user) {
		super();
		this.fileType = fileType;
		this.data = data;
		this.user = user;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
	
}
