package ro.certificate.manager.exceptions;

public final class ReCaptchaInvalidException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public ReCaptchaInvalidException(final String message) {
        super(message);
    }

}
