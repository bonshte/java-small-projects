package bg.sofia.uni.fmi.mjt.cocktail.command;

import java.util.Arrays;
import java.util.List;

public class CommandCreator {
    public static Command createCommand(String clientInput) {
        List<String> words = Arrays.stream(clientInput.split(" ")).toList();
        if (words.isEmpty()) {
            return new Command(null, null);
        }
        return new Command(words.get(0), words.subList(1, words.size()));
    }
}
