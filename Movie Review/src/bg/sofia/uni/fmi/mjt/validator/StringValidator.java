package bg.sofia.uni.fmi.mjt.validator;

public class StringValidator {
    public static boolean areValid(String... strings) {
        for (var string : strings) {
            if (string == null || string.isBlank() || string.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
