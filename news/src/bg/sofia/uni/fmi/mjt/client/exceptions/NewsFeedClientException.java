package bg.sofia.uni.fmi.mjt.client.exceptions;

import bg.sofia.uni.fmi.mjt.client.NewsFeedClient;

public class NewsFeedClientException extends Exception {
    public NewsFeedClientException(String msg) {
        super(msg);
    }

    public NewsFeedClientException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
