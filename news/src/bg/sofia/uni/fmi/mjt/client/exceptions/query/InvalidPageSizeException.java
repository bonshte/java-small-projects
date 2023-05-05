package bg.sofia.uni.fmi.mjt.client.exceptions.query;

public class InvalidPageSizeException extends InvalidQueryException {
    public InvalidPageSizeException(String msg) {
        super(msg);
    }
    public InvalidPageSizeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
