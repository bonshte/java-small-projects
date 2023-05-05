package bg.sofia.uni.fmi.mjt.client.exceptions.api;

public class ApiBadParameterException extends ApiException {
    public ApiBadParameterException(String msg) {
        super(msg);
    }
    public ApiBadParameterException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
