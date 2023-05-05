package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class FolderNotFoundException extends RuntimeException {
    public FolderNotFoundException(String msg) {
        super(msg);
    }

    public FolderNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
