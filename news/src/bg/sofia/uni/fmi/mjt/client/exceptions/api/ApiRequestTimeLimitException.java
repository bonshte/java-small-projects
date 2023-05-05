package bg.sofia.uni.fmi.mjt.client.exceptions.api;

public class ApiRequestTimeLimitException extends ApiException {
    public ApiRequestTimeLimitException(String msg) {
        super(msg);
    }
    public ApiRequestTimeLimitException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
