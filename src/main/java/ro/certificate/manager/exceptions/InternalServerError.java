package ro.certificate.manager.exceptions;

public class InternalServerError extends RuntimeException {

    private static final long serialVersionUID = 6559430798199071048L;

    public InternalServerError(String message) {
        super(message);
    }
}
