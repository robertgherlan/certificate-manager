package ro.certificate.manager.exceptions;

public class InternalServerError extends RuntimeException {

	private static final long serialVersionUID = 6559430798199071048L;

	private String message;

	public InternalServerError() {

	}

	public InternalServerError(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
