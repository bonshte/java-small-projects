package bg.sofia.uni.fmi.mjt.mail.decoder;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class WordExtractor {

    public static Set<String> extractWords(String line) {
        Set<String> wordsExtracted = new HashSet<>();
        String[] words = line.split(" ");
        for ( var word : words) {
            if (!word.isEmpty() && !word.isBlank()) {
                String realWord = word.endsWith(",") ? word.substring(0, word.length() - 1) : word;
                wordsExtracted.add(realWord);
            }
        }
        return wordsExtracted;
    }

    public static LocalDateTime extractLocalDateTime(String line) {
        String[] times = line.trim().split(" ");
        String[] firstHalf = times[0].split("-");
        String[] secondHalf = times[1].split(":");
        int year = Integer.parseInt(firstHalf[0]);
        int month = Integer.parseInt(firstHalf[1]);
        int day = Integer.parseInt(firstHalf[2]);

        int hours = Integer.parseInt(secondHalf[0]);
        int minutes = Integer.parseInt(secondHalf[1]);
        return LocalDateTime.of(year, month, day, hours, minutes);


    }
}
