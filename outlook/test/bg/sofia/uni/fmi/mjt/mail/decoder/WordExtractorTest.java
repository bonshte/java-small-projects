package bg.sofia.uni.fmi.mjt.mail.decoder;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WordExtractorTest {
    @Test
    void testExtractWordsWithoutMarks() {
        String line = "word1 word2 hello";
        Set<String> words = WordExtractor.extractWords(line);
        assertEquals(Set.of("word1", "word2", "hello"), words, "extracted words do not match");
    }

    @Test
    void testExtractWordsWithMarks() {
        String line = "word1, word2, hello";
        Set<String> words = WordExtractor.extractWords(line);
        assertEquals(Set.of("word1", "word2", "hello"), words, "extracted words do not match");
    }
    @Test
    void testExtractWordsWithMixed() {
        String line = "word1, word2 hello";
        Set<String> words = WordExtractor.extractWords(line);
        assertEquals(Set.of("word1", "word2", "hello"), words, "extracted words do not match");
    }
    @Test
    void testExtractWordsEmpty() {
        String line = "";
        Set<String> words = WordExtractor.extractWords(line);
        assertEquals(Set.of(), words, "there were no words");
    }

    @Test
    void testExtractLocalDateTime() {
        String time = "   1929-11-12 11:8  ";
        assertEquals(LocalDateTime.of(1929,11,12,11,8),
                WordExtractor.extractLocalDateTime(time), "wrong extracted time");
    }

}