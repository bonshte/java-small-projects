package bg.sofia.uni.fmi.mjt.mail.validators;

public class StringValidator {
    public static boolean isValidString(String str) {
        return str != null && !str.isEmpty() && !str.isBlank();
    }

}
