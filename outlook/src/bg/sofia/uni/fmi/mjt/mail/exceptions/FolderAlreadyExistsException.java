package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class FolderAlreadyExistsException extends RuntimeException {
    public FolderAlreadyExistsException(String msg) {
        super(msg);
    }

    public FolderAlreadyExistsException(String msg, Throwable e) {
        super(msg, e);
    }
}
