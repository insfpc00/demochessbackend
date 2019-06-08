package es.fporto.demo.minichess.user.model;

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import es.fporto.demo.minichess.user.model.User.FideTitles;
import es.fporto.demo.minichess.user.model.User.Themes;
@JsonIgnoreProperties(value={ "password" }, allowSetters= true)
public class UserDto {
	
	private static final double INITIAL_ELO = 1500d;
	private String username;
	private String password;

    private FideTitles fideTitle;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Calendar dateOfBirth;
    private String firstName;
    private String lastName;
    private String avatarType;
    private byte[] avatarData;
    private String country;
    private Double[] eloRatings;
    private Calendar creationDate;
    private Themes theme;
    private boolean soundEnabled;
    


	public UserDto(String username, String password,String firstName,String lastName) {
		super();
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.country="Earth";
		this.eloRatings = new Double[] {INITIAL_ELO,INITIAL_ELO,INITIAL_ELO};
		this.creationDate = Calendar.getInstance();
		this.dateOfBirth= this.creationDate;
		this.theme= Themes.DEFAULT;
	}
    
    public UserDto(String username, String password, FideTitles fideTitle, Calendar dateOfBirth, String firstName,
			String lastName, String country,Calendar creationDate,Themes theme) {
		super();
		this.username = username;
		this.password = password;
		this.fideTitle = fideTitle;
		this.dateOfBirth = dateOfBirth;
		this.firstName = firstName;
		this.lastName = lastName;
		this.country = country;
		this.creationDate = creationDate;
		this.theme=theme;
	}

	public UserDto(User user) {
    	this.username=user.getUsername();
    	this.password=user.getPassword();
    	this.firstName=user.getFirstName();
    	this.setLastName(user.getLastName());
    	this.setFideTitle(user.getFideTitle());
    	this.setDateOfBirth(user.getDateOfBirth());
    	this.setCountry(user.getCountry());
    	this.setEloRatings(user.getEloRatings());
    	if (user.getAvatar()!=null) {
    		this.setAvatarType(user.getAvatar().getFileType());
    		this.setAvatarData(user.getAvatar().getData());
    	}
    	this.setCreationDate(user.getCreationDate());
    	this.setTheme(user.getTheme());
    	this.setSoundEnabled(user.isSoundEnabled());
    }
    public UserDto() {
		super();
	}

    public FideTitles getFideTitle() {
		return fideTitle;
	}

	public void setFideTitle(FideTitles fideTitle) {
		this.fideTitle = fideTitle;
	}
    
	public boolean isSoundEnabled() {
		return soundEnabled;
	}

	public void setSoundEnabled(boolean soundEnabled) {
		this.soundEnabled = soundEnabled;
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
    
    public String getAvatarType() {
 		return avatarType;
 	}

 	public void setAvatarType(String avatarType) {
 		this.avatarType = avatarType;
 	}

 	public byte[] getAvatarData() {
 		return avatarData;
 	}

 	public void setAvatarData(byte[] avatarData) {
 		this.avatarData = avatarData;
 	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Double[] getEloRatings() {
		return eloRatings;
	}

	public void setEloRatings(Double[] eloRatings) {
		this.eloRatings = eloRatings;
	}
 	
	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public Themes getTheme() {
		return theme;
	}

	public void setTheme(Themes theme) {
		this.theme = theme;
	}
 	
}
