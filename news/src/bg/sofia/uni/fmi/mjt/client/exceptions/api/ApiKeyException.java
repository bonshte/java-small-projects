package bg.sofia.uni.fmi.mjt.client.exceptions.api;

public class ApiKeyException extends ApiException {
    public ApiKeyException(String msg) {
        super(msg);
    }

    public ApiKeyException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
