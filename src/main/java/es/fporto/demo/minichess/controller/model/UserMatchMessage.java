package es.fporto.demo.minichess.controller.model;

public class UserMatchMessage {

	public static enum UserMatchMessageType{
		USER_RESIGNS,USER_OFFERS_DRAW,USER_ACCEPTS_DRAW, USER_READY, CHAT_ENABLED, CHAT_DISABLED
	}
	
	private UserMatchMessageType message;


	public UserMatchMessage() {
		super();
	}

	public UserMatchMessage(UserMatchMessageType message) {
		super();
		this.message = message;
	}

	public UserMatchMessageType getMessage() {
		return message;
	}

	public void setMessage(UserMatchMessageType message) {
		this.message = message;
	}
	
}
