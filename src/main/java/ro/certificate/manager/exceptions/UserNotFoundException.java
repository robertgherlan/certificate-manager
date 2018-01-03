package ro.certificate.manager.exceptions;

public class UserNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1416881930861219440L;

	private String message = null;

	public UserNotFoundException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
