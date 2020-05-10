package ro.certificate.manager.exceptions;

public class FileAlreadyExistException extends RuntimeException {

    private static final long serialVersionUID = 6559430798199071048L;

    public FileAlreadyExistException(String message) {
        super(message);
    }
}
