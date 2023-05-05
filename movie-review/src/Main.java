import bg.sofia.uni.fmi.mjt.sentiment.MovieReviewSentimentAnalyzer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try (Reader stopWordsReader = new FileReader("stop");
            Reader reviewsReader = new FileReader("file");
             Writer outWords = new FileWriter("outfiLE")) {
            MovieReviewSentimentAnalyzer analyzer = new MovieReviewSentimentAnalyzer(stopWordsReader, reviewsReader, outWords);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
