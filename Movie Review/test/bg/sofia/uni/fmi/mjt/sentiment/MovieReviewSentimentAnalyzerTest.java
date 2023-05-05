package bg.sofia.uni.fmi.mjt.sentiment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MovieReviewSentimentAnalyzerTest {


    private static final String TEST_STOP_WORDS = "a\n" +
            "about\n" +
            "above\n" +
            "after\n" +
            "again\n" +
            "against\n" +
            "all\n" +
            "am\n" +
            "an\n" +
            "and\n" +
            "any\n" +
            "are\n" +
            "aren't\n" +
            "as\n" +
            "at\n" +
            "be\n" +
            "because\n" +
            "been\n" +
            "before\n" +
            "being\n" +
            "below\n" +
            "between\n" +
            "both\n" +
            "but\n" +
            "by\n" +
            "can't\n" +
            "cannot\n" +
            "could\n" +
            "couldn't\n" +
            "did\n" +
            "didn't\n" +
            "do\n" +
            "does\n" +
            "doesn't\n" +
            "doing\n" +
            "don't\n" +
            "down\n" +
            "during\n" +
            "each\n" +
            "few\n" +
            "for\n" +
            "from\n" +
            "further\n" +
            "had\n" +
            "hadn't\n" +
            "has\n" +
            "hasn't\n" +
            "have\n" +
            "haven't\n" +
            "having\n" +
            "he\n" +
            "he'd\n" +
            "he'll\n" +
            "he's\n" +
            "her\n" +
            "here\n" +
            "here's\n" +
            "hers\n" +
            "herself\n" +
            "him\n" +
            "himself\n" +
            "his\n" +
            "how\n" +
            "how's\n" +
            "i\n" +
            "i'd\n" +
            "i'll\n" +
            "i'm\n" +
            "i've\n" +
            "if\n" +
            "in\n" +
            "into\n" +
            "is\n" +
            "isn't\n" +
            "it\n" +
            "it's\n" +
            "its\n" +
            "itself\n" +
            "let's\n" +
            "me\n" +
            "more\n" +
            "most\n" +
            "mustn't\n" +
            "my\n" +
            "myself\n" +
            "no\n" +
            "nor\n" +
            "not\n" +
            "of\n" +
            "off\n" +
            "on\n" +
            "once\n" +
            "only\n" +
            "or\n" +
            "other\n" +
            "ought\n" +
            "our\n" +
            "ours\n" +
            "ourselves\n" +
            "out\n" +
            "over\n" +
            "own\n" +
            "same\n" +
            "shan't\n" +
            "she\n" +
            "she'd\n" +
            "she'll\n" +
            "she's\n" +
            "should\n" +
            "shouldn't\n" +
            "so\n" +
            "some\n" +
            "such\n" +
            "than\n" +
            "that\n" +
            "that's\n" +
            "the\n" +
            "their\n" +
            "theirs\n" +
            "them\n" +
            "themselves\n" +
            "then\n" +
            "there\n" +
            "there's\n" +
            "these\n" +
            "they\n" +
            "they'd\n" +
            "they'll\n" +
            "they're\n" +
            "they've\n" +
            "this\n" +
            "those\n" +
            "through\n" +
            "to\n" +
            "too\n" +
            "under\n" +
            "until\n" +
            "up\n" +
            "very\n" +
            "was\n" +
            "wasn't\n" +
            "we\n" +
            "we'd\n" +
            "we'll\n" +
            "we're\n" +
            "we've\n" +
            "were\n" +
            "weren't\n" +
            "what\n" +
            "what's\n" +
            "when\n" +
            "when's\n" +
            "where\n" +
            "where's\n" +
            "which\n" +
            "while\n" +
            "who\n" +
            "who's\n" +
            "whom\n" +
            "why\n" +
            "why's\n" +
            "with\n" +
            "won't\n" +
            "would\n" +
            "wouldn't\n" +
            "you\n" +
            "you'd\n" +
            "you'll\n" +
            "you're\n" +
            "you've\n" +
            "your\n" +
            "yours\n" +
            "yourself\n" +
            "yourselves";
    private static final String TEST_RESOURCES = "1 A series of escapades demonstrating the adage that what is good for the goose is also good for the gander , some of which occasionally amuses but none of which amounts to much of a story .\n" +
            "4 This quiet , introspective and entertaining independent is worth seeking .\n" +
            "1 Even fans of Ismail Merchant's work , I suspect , would have a hard time sitting through this one .\n" +
            "3 A positively thrilling combination of ethnography and all the intrigue , betrayal , deceit and murder of a Shakespearean tragedy or a juicy soap opera .\n" +
            "1 Aggressive self-glorification and a manipulative whitewash .\n" +
            "0 not good\n" +
            "0 poor\n";


    Path testReviewsPath;
    Path testStopWordsPath;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setupResources() {
            this.testReviewsPath = this.tempDir.resolve("tr");
            this.testStopWordsPath = this.tempDir.resolve("sw");
            try (FileWriter reviewsWriter = new FileWriter(testReviewsPath.toString(),false);
                FileWriter stopWordsWriter = new FileWriter(testStopWordsPath.toString(),false )) {
                reviewsWriter.write(TEST_RESOURCES);
                reviewsWriter.flush();
                stopWordsWriter.write(TEST_STOP_WORDS);
                stopWordsWriter.flush();

            } catch (IOException e) {
                throw new IllegalStateException("could not create testing files", e);
            }

    }

    @AfterEach
    void tearDownResources() {
        try (FileWriter reviewsCleaner = new FileWriter(testReviewsPath.toString(),false);
            FileWriter stopWordsCleaner = new FileWriter(testStopWordsPath.toString(),false)) {

        } catch (IOException e) {
            throw new RuntimeException("could not clean testing resource files", e);
        }
    }

    private MovieReviewSentimentAnalyzer analyzer;
    @Test
    void testAppendReviewUpdatesSentimentForWords() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            analyzer.appendReview("cool movie dawg", 4);
            System.out.println(analyzer.getReviewSentiment("daWg"));
            assertEquals(4, analyzer.getWordSentiment("dawG"),
                    "should be added as a new word with sentiment 4");

        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testAppendReviewUpdatesDatabase() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            boolean written = analyzer.appendReview("cool movie dawg", 4);
            if (written) {
                try (BufferedReader newReaderFromReviews = new BufferedReader(new FileReader(testReviewsPath.toString()))) {
                    List<String> lines = newReaderFromReviews.lines().toList();
                    String lastReview = lines.stream().skip(lines.size() - 1).findFirst().get();
                    assertEquals("4 cool movie dawg", lastReview, "last review should be in this format");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testAppendReviewInvalidArg() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            assertThrows(IllegalArgumentException.class,
                    () -> analyzer.appendReview(null, 2), "should throw when called with null review");
            assertThrows(IllegalArgumentException.class,
                    () -> analyzer.appendReview("nice movie for real", -1),
                    "should throw when sentiment is out of the allowed range");

        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testGetWordSentiment() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            assertEquals((double) 2/3, analyzer.getWordSentiment("GOoD"), "average rating of good should be 2/3");
            assertEquals(-1, analyzer.getWordSentiment("not"), "stop words are not included");
            assertEquals(-1, analyzer.getWordSentiment("random"), "unknown words should have -1");
            assertThrows(IllegalArgumentException.class, () ->analyzer.getWordSentiment(null),
                    "null passed should throw exception");

        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testGetWordFrequency() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            assertEquals(3, analyzer.getWordFrequency("GoOd"), "good is met 3 times");
            assertEquals(0, analyzer.getWordFrequency("crinGy"), "cringy is not in resources");
            assertEquals(1, analyzer.getWordFrequency("Ismail"), "Ismail is only once");
            assertThrows(IllegalArgumentException.class, () -> analyzer.getWordFrequency(""),
                    "passed work is blank");
            assertEquals(0, analyzer.getWordFrequency("NOT"), "stop words should not be taken into account");

        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testMostFrequentWords() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
            FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
            FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            assertTrue("good".equalsIgnoreCase(analyzer.getMostFrequentWords(1).iterator().next()),
                    "good is the most frequent word");

        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testMostFrequentWordInvalid() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            assertThrows(IllegalArgumentException.class, () -> analyzer.getMostFrequentWords(0),
                    "should throw when called with non positive");

        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testMostNegativeWords() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            assertTrue("PooR".equalsIgnoreCase(analyzer.getMostNegativeWords(1).iterator().next()),
                    "poor should be with least rating");
            assertTrue("Good".equalsIgnoreCase(analyzer.getMostNegativeWords(2).get(1)),
                    "good should be with second worst rating");
        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testMostNegativeWordsInvalid() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            assertThrows(IllegalArgumentException.class, () -> analyzer.getMostNegativeWords(0),
                    "should throw when called with non positive");

        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testMostPositiveWords() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            Set<String> expectedMostPositiveWords = Set.of("quiet", "introspective", "entertaining", "independent", "worth", "seeking");
            assertTrue(expectedMostPositiveWords.containsAll(analyzer.getMostPositiveWords(6)),
                    "6 most positive words do not match");

        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testMostPositiveWordsInvalid() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            assertThrows(IllegalArgumentException.class, () -> analyzer.getMostPositiveWords(0),
                    "should throw when called with non positive");

        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testIsStopWord() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            assertTrue(analyzer.isStopWord("a"), "a is a stop word");
            assertTrue(analyzer.isStopWord("an"), "an is a stop word");
            assertTrue(analyzer.isStopWord("and"), "and is a stop word");
            assertFalse(analyzer.isStopWord("good"), "good is not a stop word");
            assertThrows(IllegalArgumentException.class, () -> analyzer.isStopWord(null),
                    "null passed should throw exception");

        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testGetSentimentDictionarySize() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            assertEquals(49, analyzer.getSentimentDictionarySize(), "words are 49");
        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testGetReviewSentiment() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            assertThrows(IllegalArgumentException.class, () -> analyzer.getReviewSentiment(null),
                    "null review should throw exception when grading");
            assertEquals(-1, analyzer.getReviewSentiment("not and a"),
                    "when only stop words are passed it should be -1");
            assertEquals(-1, analyzer.getReviewSentiment("unknown OKKKAY 1MORE UNKNOWNN"),
                    "when only unknown words are passed the result should be -1");
            double fansRating = analyzer.getWordSentiment("fans");
            double workRating = analyzer.getWordSentiment("work");
            double suspectRating = analyzer.getWordSentiment("suspect");
            double goodRating = analyzer.getWordSentiment("good");
            assertEquals((fansRating + workRating + suspectRating + goodRating) / 4,
                    analyzer.getReviewSentiment("fans suspect work is good"),
                    "review is not correct");
        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void getTestReviewSentimentAsName() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString());
             FileWriter writerToReviewsFile = new FileWriter(testReviewsPath.toString(), true)) {
            this.analyzer = new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, writerToReviewsFile);
            assertEquals("negative", analyzer.getReviewSentimentAsName("very poor"),
                    "should be returned as a negative feedback");
            assertEquals("somewhat negative", analyzer.getReviewSentimentAsName("positive, but hard time sitting through this one"),
                    "should be considered somewhat negative");
            assertEquals("neutral", analyzer.getReviewSentimentAsName("poor but entertaining"),
                    "should be considered neutral");
            assertEquals("unknown", analyzer.getReviewSentimentAsName("sak gfb sda gr"),
                    "should be undefined");
            assertEquals("somewhat positive",
                    analyzer.getReviewSentimentAsName("whitewash, but it is worth seeking"),
                    "should be considered somewhat positive");
            assertEquals("positive", analyzer.getReviewSentimentAsName("very worth and entertaining"),
                    "should be considered positive");
            assertThrows(IllegalArgumentException.class, () -> analyzer.getReviewSentimentAsName(null),
                    "null should throw exception");

        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }

    @Test
    void testConstructorWithNullParameter() {
        try (FileReader readerFromReviewsFile = new FileReader(testReviewsPath.toString());
             FileReader readerFromStopWordsFile = new FileReader(testStopWordsPath.toString())) {
            assertThrows(IllegalArgumentException.class,
                    () -> new MovieReviewSentimentAnalyzer(readerFromStopWordsFile, readerFromReviewsFile, null));
        } catch (IOException e) {
            throw new RuntimeException("could not open resource files", e);
        }
    }


}