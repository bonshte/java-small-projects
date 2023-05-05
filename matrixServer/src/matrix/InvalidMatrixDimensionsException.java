package matrix;

public class InvalidMatrixDimensionsException extends Exception {
    public InvalidMatrixDimensionsException(String str) {
        super(str);
    }
    public InvalidMatrixDimensionsException(String str, Throwable cause) {
        super(str, cause);
    }
}
