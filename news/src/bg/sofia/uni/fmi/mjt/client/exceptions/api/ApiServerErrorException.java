package bg.sofia.uni.fmi.mjt.client.exceptions.api;

public class ApiServerErrorException extends ApiException {
    public ApiServerErrorException(String msg) {
        super(msg);
    }
    public ApiServerErrorException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
