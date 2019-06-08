package es.fporto.demo.minichess.user.service;

public class AccountExistsException extends UserException{

	private static final long serialVersionUID = 1L;

	public AccountExistsException() {
        super();
    }

    public AccountExistsException(String message) {
        super(message);
    }

    public AccountExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountExistsException(Throwable cause) {
        super(cause);
    }
	
}
