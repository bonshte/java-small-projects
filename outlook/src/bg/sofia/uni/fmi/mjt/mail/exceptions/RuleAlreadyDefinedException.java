package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class RuleAlreadyDefinedException extends RuntimeException {
    public RuleAlreadyDefinedException(String msg) {
        super(msg);
    }

    public RuleAlreadyDefinedException(String msg, Throwable e) {
        super(msg, e);
    }
}
