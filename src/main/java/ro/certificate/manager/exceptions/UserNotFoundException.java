package ro.certificate.manager.exceptions;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1416881930861219440L;

    public UserNotFoundException(String message) {
        super(message);
    }

}
