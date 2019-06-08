package es.fporto.demo.minichess.analysis.service;

public class AnalysisException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AnalysisException() {
        super();
    }

    public AnalysisException(String message) {
        super(message);
    }

    public AnalysisException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnalysisException(Throwable cause) {
        super(cause);
    }

}
