package exceptions;

public class CloningException extends RuntimeException {

    public CloningException(String message) {
        super(message);
    }

    public CloningException(Exception e) {
        super(e);
    }
}