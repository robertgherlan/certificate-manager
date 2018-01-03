package ro.certificate.manager.exceptions;

public class FileAlreadyExistException extends Exception {

	private static final long serialVersionUID = 6559430798199071048L;

	private String message;

	public FileAlreadyExistException() {

	}

	public FileAlreadyExistException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
