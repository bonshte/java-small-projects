package bg.sofia.uni.fmi.mjt.client.exceptions.query;

public class InvalidCountryException extends InvalidQueryException {
    public InvalidCountryException(String msg) {
        super(msg);
    }

    public InvalidCountryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
