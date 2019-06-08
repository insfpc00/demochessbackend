package es.fporto.demo.minichess.uciclient.exception;

public class UCIClientException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UCIClientException() {
        super();
    }

    public UCIClientException(String message) {
        super(message);
    }

    public UCIClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public UCIClientException(Throwable cause) {
        super(cause);
    }
}
