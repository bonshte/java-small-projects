package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class InvalidPathException extends RuntimeException {
    public InvalidPathException(String msg) {
        super(msg);
    }

    public InvalidPathException(String msg, Throwable e) {
        super(msg, e);
    }
}
