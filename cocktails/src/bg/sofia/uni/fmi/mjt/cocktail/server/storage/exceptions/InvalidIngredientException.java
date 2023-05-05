package bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions;

public class InvalidIngredientException extends Exception {
    public InvalidIngredientException(String msg) {
        super(msg);
    }

    public InvalidIngredientException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
