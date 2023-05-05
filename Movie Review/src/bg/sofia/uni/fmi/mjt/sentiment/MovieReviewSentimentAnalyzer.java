package bg.sofia.uni.fmi.mjt.sentiment;

import bg.sofia.uni.fmi.mjt.validator.StringValidator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MovieReviewSentimentAnalyzer implements SentimentAnalyzer {
    private static final String UNKNOWN = "unknown";
    private static final String NEGATIVE = "negative";
    private static final String SOMEWHAT_NEGATIVE = "somewhat negative";
    private static final String NEUTRAL = "neutral";
    private static final String SOMEWHAT_POSITIVE = "somewhat positive";
    private static final String POSITIVE = "positive";
    private static final int NEGATIVE_INDEX = 0;
    private static final int SOMEWHAT_NEGATIVE_INDEX = 1;
    private static final int NEUTRAL_INDEX = 2;
    private static final int SOMEWHAT_POSITIVE_INDEX = 3;
    private static final int POSITIVE_INDEX = 4;

    private HashMap<String, Map.Entry<Double, Integer>> wordToRatingAndTime;
    private Set<String> wordsToIgnore;
    private Writer reviewsOut;


    public MovieReviewSentimentAnalyzer(Reader stopWordsIn, Reader reviewsIn, Writer reviewsOut) {
        if (stopWordsIn == null || reviewsIn == null || reviewsOut == null) {
            throw new IllegalArgumentException("null stream passed");
        }
        this.reviewsOut = reviewsOut;
        this.wordsToIgnore = new HashSet<>();
        try (BufferedReader stopWordsReader = new BufferedReader(stopWordsIn)) {
            stopWordsReader.lines()
                    .forEach(x -> wordsToIgnore.add(x));
        } catch (IOException e) {
            throw new RuntimeException("could not load stop words", e);
        }
        this.wordToRatingAndTime = new HashMap<>();
        try (BufferedReader reviewsReader = new BufferedReader(reviewsIn)) {
            reviewsReader.lines()
                    .forEach(x -> trainOnFullReview(x));
        } catch (IOException e) {
            throw new RuntimeException("could not load training file", e);
        }
    }





    @Override
    public double getReviewSentiment(String review) {
        if (!StringValidator.areValid(review)) {
            throw new IllegalArgumentException("null, empty or blank passed");
        }
        List<String> words = extractRealWords(review);
        List<String> knownWords = words.stream()
                .filter(x -> !wordsToIgnore.contains(x))
                .filter(x -> isTrainedWithWord(x))
                .map(String::toLowerCase)
                .toList();

        if (knownWords.size() == 0) {
            return -1;
        }

        return knownWords.stream()
                .mapToDouble(x -> wordToRatingAndTime.get(x).getKey())
                .average()
                .orElse(-1);
    }

    @Override
    public String getReviewSentimentAsName(String review) {
        if (!StringValidator.areValid(review)) {
            throw new IllegalArgumentException("null, empty or blank passed");
        }
        return switch ((int) Math.round(getReviewSentiment(review))) {
            case NEGATIVE_INDEX -> NEGATIVE;
            case SOMEWHAT_NEGATIVE_INDEX -> SOMEWHAT_NEGATIVE;
            case NEUTRAL_INDEX -> NEUTRAL;
            case SOMEWHAT_POSITIVE_INDEX -> SOMEWHAT_POSITIVE;
            case POSITIVE_INDEX -> POSITIVE;
            default -> UNKNOWN;
        };
    }



    @Override
    public double getWordSentiment(String word) {
        if (!StringValidator.areValid(word)) {
            throw new IllegalArgumentException("null, empty or blank passed");
        }
        if (isTrainedWithWord(word)) {
            return wordToRatingAndTime.get(word.toLowerCase()).getKey();
        }
        return -1;
    }


    @Override
    public int getWordFrequency(String word) {
        if (!StringValidator.areValid(word)) {
            throw new IllegalArgumentException("null, empty or blank passed");
        }
        if (isTrainedWithWord(word)) {
            return wordToRatingAndTime.get(word.toLowerCase()).getValue();
        }
        return 0;
    }


    @Override
    public List<String> getMostFrequentWords(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("must be positive");
        }
        return wordToRatingAndTime.entrySet().stream()
                .sorted((x, y) -> y.getValue().getValue().compareTo(x.getValue().getValue()))
                .limit(n)
                .map(x -> x.getKey())
                .toList();
    }


    @Override
    public List<String> getMostPositiveWords(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("must be positive");
        }
        return wordToRatingAndTime.entrySet().stream()
                .sorted((x, y) -> y.getValue().getKey().compareTo(x.getValue().getKey()))
                .map(x -> x.getKey())
                .limit(n)
                .toList();
    }


    @Override
    public List<String> getMostNegativeWords(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("must be positive");
        }
        return wordToRatingAndTime.entrySet().stream()
                .sorted((x, y) -> x.getValue().getKey().compareTo(y.getValue().getKey()))
                .map(Map.Entry::getKey)
                .limit(n)
                .toList();
    }

    @Override
    public boolean appendReview(String review, int sentiment) {
        if (!StringValidator.areValid(review)) {
            throw new IllegalArgumentException("null,empty or blank review passed");
        }
        if (sentiment < NEGATIVE_INDEX || sentiment > POSITIVE_INDEX) {
            throw new IllegalArgumentException("sentiment is out of the allowed range");
        }

        List<String> words = extractRealWords(review);
        List<String> wordsToUpdate = words.stream()
                .filter(x -> !isStopWord(x))
                .toList();

        wordsToUpdate.stream()
                .forEach(x -> trainWithWord(x, sentiment));
        try {
            reviewsOut.write(sentiment + " " + review + System.lineSeparator());
            reviewsOut.flush();
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    @Override
    public int getSentimentDictionarySize() {
        return wordToRatingAndTime.size();
    }


    @Override
    public boolean isStopWord(String word) {
        if (!StringValidator.areValid(word)) {
            throw new IllegalArgumentException("null, empty or blank string passed");
        }
        return wordsToIgnore.stream().anyMatch(x -> x.equalsIgnoreCase(word));

    }

    private void trainOnFullReview(String review) {
        int rating = Integer.parseInt(review.substring(0, 1));
        List<String> realWords = extractRealWords(review.substring(1));
        List<String> realNotStopWords = realWords.stream().filter(x -> !isStopWord(x)).toList();
        for (var realNotStopWord : realNotStopWords) {
            trainWithWord(realNotStopWord, rating);
        }
    }

    private List<String> extractRealWords(String review) {
        List<String> realWords = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        for (int i = 0 ; i < review.length(); ++i) {
            if (isValidLetter(review.charAt(i))) {
                currentWord.append(review.charAt(i));
            } else {
                if (isRealWord(currentWord.toString())) {
                    realWords.add(currentWord.toString());
                }
                currentWord.setLength(0);
            }
        }
        if (isRealWord(currentWord.toString())) {
            realWords.add(currentWord.toString());
        }
        return realWords;
    }


    private boolean isValidLetter(char letter) {
        return (letter >= 'A' && letter <= 'Z') || (letter >= 'a' && letter <= 'z') ||
                (letter >= '0' && letter <= '9') || letter == '\'';
    }

    private boolean isRealWord(String word) {
        if (word.length() < 2) {
            return false;
        }
        for (int i = 0 ; i < word.length(); ++i) {
            char letter = word.charAt(i);
            if (!isValidLetter(letter)) {
                return false;
            }
        }
        return true;
    }


    private boolean isTrainedWithWord(String word) {
        return wordToRatingAndTime.entrySet().stream()
                .map(x -> x.getKey())
                .anyMatch(x -> x.equalsIgnoreCase(word));
    }

    //all words in the data set will be saved as lowercase
    private void trainWithWord(String word, int sentiment) {
        if (!isTrainedWithWord(word)) {
            wordToRatingAndTime.put(word.toLowerCase(), Map.entry((double) sentiment, 1));
        } else {
            double currentAverage = wordToRatingAndTime.get(word).getKey();
            int currentTimesTrained = wordToRatingAndTime.get(word).getValue();
            double newAverage = ((currentAverage * currentTimesTrained) + sentiment) / (currentTimesTrained + 1);
            wordToRatingAndTime.put(word.toLowerCase(), Map.entry(newAverage, currentTimesTrained + 1));
        }
    }
}
