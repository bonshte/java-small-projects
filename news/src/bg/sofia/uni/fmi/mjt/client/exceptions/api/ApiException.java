package bg.sofia.uni.fmi.mjt.client.exceptions.api;

public abstract class ApiException extends Exception {
    public ApiException(String msg) {
        super(msg);
    }
    public ApiException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
