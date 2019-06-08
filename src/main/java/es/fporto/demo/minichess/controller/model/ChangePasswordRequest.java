package es.fporto.demo.minichess.controller.model;

public class ChangePasswordRequest {
	
	private String newPassword;
	private String oldPassword;
	
	public ChangePasswordRequest() {
		super();
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
}
