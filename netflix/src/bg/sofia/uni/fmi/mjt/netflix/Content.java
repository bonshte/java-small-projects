package bg.sofia.uni.fmi.mjt.netflix;

import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public record Content(String id, String title, ContentType type, String description, int releaseYear,
                      int runtime, List<String> genres, int seasons, String imdbId,
                      double imdbScore, double imdbVotes) {
    private static final int RELEASE_DATE = 4;
    private static final int TYPE = 2;
    private static final int RUN_TIME = 5;
    private static final int SEASONS = 7;
    private static final int SCORE = 9;
    private static final int VOTES = 10;
    private static final int DEFINITION = 6;
    private static final int ID = 8;

    private static final int DESCRIPTION = 3;


    public static Content createContentFromFields(String[] extractedFields) throws Exception {
        ContentType type = ContentType.valueOf(extractedFields[TYPE]);
        int releaseDate = Integer.parseInt(extractedFields[RELEASE_DATE]);
        int runTime = Integer.parseInt(extractedFields[RUN_TIME]);
        int seasons = Integer.parseInt(extractedFields[SEASONS]);
        double imdbScore = Double.parseDouble(extractedFields[SCORE]);
        double imdbVotes = Double.parseDouble(extractedFields[VOTES]);
        List<String> genres = new LinkedList<>();
        String genresDefinition = extractedFields[DEFINITION].substring(1, extractedFields[DEFINITION].length() - 1);
        if (genresDefinition.isEmpty()) {
            throw new Exception("empty list of genres");
        }

        String[] genresWithQMarks = genresDefinition.split(";");
        for (var genre : genresWithQMarks) {
            String genreWithQMarks = genre.trim();
            genres.add(genreWithQMarks.substring(1, genreWithQMarks.length() - 1));
        }
        return  new Content(extractedFields[0], extractedFields[1], type, extractedFields[DESCRIPTION], releaseDate,
                runTime, genres, seasons, extractedFields[ID], imdbScore, imdbVotes);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Content content)) return false;
        return Objects.equals(id, content.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
