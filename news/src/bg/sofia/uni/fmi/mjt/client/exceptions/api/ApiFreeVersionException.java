package bg.sofia.uni.fmi.mjt.client.exceptions.api;

public class ApiFreeVersionException extends ApiException {
    public ApiFreeVersionException(String msg) {
        super(msg);
    }
    public ApiFreeVersionException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
