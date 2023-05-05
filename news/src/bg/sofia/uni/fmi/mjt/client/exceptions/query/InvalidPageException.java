package bg.sofia.uni.fmi.mjt.client.exceptions.query;

public class InvalidPageException extends InvalidQueryException {
    public InvalidPageException(String msg) {
        super(msg);
    }
    public InvalidPageException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
