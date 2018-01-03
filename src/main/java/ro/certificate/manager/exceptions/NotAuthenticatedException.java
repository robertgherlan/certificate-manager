package ro.certificate.manager.exceptions;

public class NotAuthenticatedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3510699183733000205L;

	private String message;

	public NotAuthenticatedException() {
	}

	public NotAuthenticatedException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
