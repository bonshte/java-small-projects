package bg.sofia.uni.fmi.mjt.netflix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NetflixRecommender {
    private Set<Content> shows;

    private static final Pattern REGEX = Pattern.compile("[\\p{IsPunctuation}\\s]+");
    private static final int CONTENT_FIELDS = 11;
    private static final int PARAMETER = 10000;

    /**
     * Loads the dataset from the given {@code reader}.
     *
     * @param reader Reader from which the dataset can be read.
     */
    public NetflixRecommender(Reader reader) {
        shows = new HashSet<>();
        try (BufferedReader databaseReader = new BufferedReader(reader)) {
            String firstLine = databaseReader.readLine();
            String lineWithShowData;
            while ((lineWithShowData = databaseReader.readLine()) != null) {
                String[] extractedFields = lineWithShowData.split(",");
                if (extractedFields.length != CONTENT_FIELDS) {
                    continue;
                }
                try {
                    Content content = Content.createContentFromFields(extractedFields);
                    shows.add(content);
                } catch (Exception e) {
                    //if we only want to operate when all data is valid, exception should be thrown here
                }

            }
        } catch (IOException e) {
            throw new IllegalStateException("could not load data from database");
        }
    }

    /**
     * Returns all movies and shows from the dataset in undefined order as an unmodifiable List.
     * If the dataset is empty, returns an empty List.
     *
     * @return the list of all movies and shows.
     */
    public List<Content> getAllContent() {
        if (shows.isEmpty()) {
            return new ArrayList<>(0);
        }
        return List.copyOf(shows);
    }

    /**
     * Returns a list of all unique genres of movies and shows in the dataset in undefined order.
     * If the dataset is empty, returns an empty List.
     *
     * @return the list of all genres
     */
    public List<String> getAllGenres() {
        return getAllContent().stream()
                .flatMap(x -> x.genres().stream())
                .distinct()
                .toList();
    }

    /**
     * Returns the movie with the longest duration / run time. If there are two or more movies
     * with equal maximum run time, returns any of them. Shows in the dataset are not considered by this method.
     *
     * @return the movie with the longest run time
     * @throws NoSuchElementException in case there are no movies in the dataset.
     */
    public Content getTheLongestMovie() {
        return getAllContent().stream()
                .filter(x -> x.type().equals(ContentType.MOVIE))
                .max(Comparator.comparingInt(Content::runtime))
                .orElseThrow(() -> new NoSuchElementException("no movies"));

    }

    /**
     * Returns a breakdown of content by type (movie or show).
     *
     * @return a Map with key: a ContentType and value: the set of movies or shows on the dataset, in undefined order.
     */
    public Map<ContentType, Set<Content>> groupContentByType() {
        return getAllContent().stream()
                .collect(Collectors.groupingBy(Content::type, Collectors.toSet()));
    }

    private static double getWeightedRating(Content content, double averageInAll) {
        double ownAverage = content.imdbVotes() == 0 ? 0 : content.imdbScore();
        double votes = content.imdbVotes();
        double parameter = PARAMETER;
        return (votes / (votes + parameter)) * ownAverage + (parameter / (votes + parameter)) * averageInAll;

    }
    /**
     * Returns the top N movies and shows sorted by weighed IMDB rating in descending order.
     * If there are fewer movies and shows than {@code n} in the dataset, return all of them.
     * If {@code n} is zero, returns an empty list.
     *
     * The weighed rating is calculated by the following formula:
     * Weighted Rating (WR) = (v ÷ (v + m)) × R + (m ÷ (v + m)) × C
     * where
     * R is the content's own average rating across all votes. If it has no votes, its R is 0.
     * C is the average rating of content across the dataset
     * v is the number of votes for a content
     * m is a tunable parameter: sensitivity threshold. In our algorithm, it's a constant equal to 10_000.
     *
     * Check https://stackoverflow.com/questions/1411199/what-is-a-better-way-to-sort-by-a-5-star-rating for details.
     *
     * @param n the number of the top-rated movies and shows to return
     * @return the list of the top-rated movies and shows
     * @throws IllegalArgumentException if {@code n} is negative.
     */
    public List<Content> getTopNRatedContent(int n) {
        double averageRatingInDataSet = getAllContent().stream()
                        .mapToDouble(x -> x.imdbVotes() == 0 ? 0 : x.imdbScore())
                                .average()
                                        .orElse(0);

        return getAllContent().stream()
                    .sorted( (x2, x1) -> Double.compare(getWeightedRating(x1, averageRatingInDataSet),
                            getWeightedRating(x2, averageRatingInDataSet)))
                    .limit(n)
                    .toList();

    }




    public static int similarityMeasure(Content content, Content viable) {
        int common = 0;
        for (var genre : viable.genres()) {
            if (content.genres().contains(genre)) {
                common++;
            }
        }
        return common;
    }
    /**
     * Returns a list of content similar to the specified one sorted by similarity is descending order.
     * Two contents are considered similar, only if they are of the same type (movie or show).
     * The used measure of similarity is the number of genres two contents share.
     * If two contents have equal number of common genres with the specified one, their mutual oder
     * in the result is undefined.
     *
     * @param content the specified movie or show.
     * @return the sorted list of content similar to the specified one.
     */
    public List<Content> getSimilarContent(Content content) {
        return getAllContent().stream()
                .filter(x -> x.type().equals(content.type()))
                .sorted((y, x) -> Integer.compare(similarityMeasure(content, x), similarityMeasure(content, y)))
                .toList();

    }

    /**
     * Searches content by keywords in the description (case-insensitive).
     *
     * @param keywords the keywords to search for
     * @return an unmodifiable set of movies and shows whose description contains all specified keywords.
     */
    public Set<Content> getContentByKeywords(String... keywords) {
        List<String> keywordsInLower = new LinkedList<>();
        for (var keyword: keywords) {
            keywordsInLower.add(keyword.toLowerCase());
        }
        return getAllContent().stream()
                .filter(x -> REGEX.splitAsStream(x.description())
                        .map(String::toLowerCase)
                        .toList()
                        .containsAll(keywordsInLower))
                .collect(Collectors.toUnmodifiableSet());

    }

}