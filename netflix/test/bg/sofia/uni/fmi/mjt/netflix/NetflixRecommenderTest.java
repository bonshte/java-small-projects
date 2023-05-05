package bg.sofia.uni.fmi.mjt.netflix;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NetflixRecommenderTest {

    private final String TESTING = "id,title,type,description,release_year,runtime,genres,seasons,imdb_id,imdb_score,imdb_votes\n" +
            "tm84618,Taxi Driver,MOVIE,A mentally unstable Vietnam War veteran works as a night-time taxi driver in New York City where the perceived decadence and sleaze feed his urge for violent action.,1976,114,['drama'; 'crime'],-1,tt0075314,8.2,808582.0\n" +
            "tm154986,Deliverance,MOVIE,Intent on seeing the Cahulawassee River before it's turned into one huge lake; outdoor fanatic Lewis Medlock takes his friends on a river-rafting trip they'll never forget into the dangerous American back-country.,1972,109,['drama'; 'action'; 'thriller'; 'european'],-1,tt0068473,7.7,107673.0\n" +
            "tm127384,Monty Python and the Holy Grail,MOVIE,\"King Arthur; accompanied by his squire; recruits his Knights of the Round Table; including Sir Bedevere the Wise; Sir Lancelot the Brave; Sir Robin the Not-Quite-So-Brave-As-Sir-Lancelot and Sir Galahad the Pure. On the way; Arthur battles the Black Knight who; despite having had all his limbs chopped off; insists he can still fight. They reach Camelot; but Arthur decides not  to enter; as \"\"it is a silly place\"\".\",1975,91,['fantasy'; 'action'; 'comedy'],-1,tt0071853,8.2,534486.0\n" +
            "tm120801,The Dirty Dozen,MOVIE,12 American military prisoners in World War II are ordered to infiltrate a well-guarded enemy château and kill the Nazi officers vacationing there. The soldiers; most of whom are facing death sentences for a variety of violent crimes; agree to the mission and the possible commuting of their sentences.,1967,150,['war'; 'action'],-1,tt0061578,7.7,72662.0\n" +
            "ts22164,Monty Python's Flying Circus,SHOW,A British sketch comedy series with the shows being composed of surreality; risqué or innuendo-laden humour; sight gags and observational sketches without punchlines.,1969,30,['comedy'; 'european'],1,tt0063929,8.8,73424.0\n" +
            "tm70993,Life of Brian,MOVIE,Brian Cohen is an average young Jewish man; but through a series of ridiculous events; he gains a reputation as the Messiah. When he's not dodging his followers or being scolded by his shrill mother; the hapless Brian has to contend with the pompous Pontius Pilate and acronym-obsessed members of a separatist movement. Rife with Monty Python's signature absurdity; the tale finds Brian's life paralleling Biblical lore; albeit with many more laughs.,1979,94,['comedy'],-1,tt0079470,8.0,395024.0\n";
    private NetflixRecommender recommender;

    @BeforeEach
    void setup() {
        StringReader reader = new StringReader(TESTING);
        recommender = new NetflixRecommender(reader);
    }
    @Test
    void testDatabaseLoadCorrect() {
        assertEquals(6,recommender.getAllContent().size(), "did not load correct");
    }

    @Test
    void testGroupContentByType() {
        var grouping = recommender.groupContentByType();
        assertEquals(1, grouping.get(ContentType.SHOW).size(), "only one show must be present");
        assertEquals(5, grouping.get(ContentType.MOVIE).size(), "5 movies must be present");
    }

    @Test
    void testGetGenresCorrect() {
        assertEquals(8, recommender.getAllGenres().size(), "genres count does not match");
    }

    @Test
    void testGetLongestMovie() {
        assertEquals("tm120801",recommender.getTheLongestMovie().id(), "wrong calculation on longest movie");
    }

    @Test
    void getGetAverageRating() {
        String third = "tm127384,Monty Python and the Holy Grail,MOVIE,\"King Arthur; accompanied by his squire; recruits his Knights of the Round Table; including Sir Bedevere the Wise; Sir Lancelot the Brave; Sir Robin the Not-Quite-So-Brave-As-Sir-Lancelot and Sir Galahad the Pure. On the way; Arthur battles the Black Knight who; despite having had all his limbs chopped off; insists he can still fight. They reach Camelot; but Arthur decides not  to enter; as \"\"it is a silly place\"\".\",1975,91,['fantasy'; 'action'; 'comedy'],-1,tt0071853,8.2,534486.0";
        String second ="tm84618,Taxi Driver,MOVIE,A mentally unstable Vietnam War veteran works as a night-time taxi driver in New York City where the perceived decadence and sleaze feed his urge for violent action.,1976,114,['drama'; 'crime'],-1,tt0075314,8.2,808582.0";
        String first = "ts22164,Monty Python's Flying Circus,SHOW,A British sketch comedy series with the shows being composed of surreality; risqué or innuendo-laden humour; sight gags and observational sketches without punchlines.,1969,30,['comedy'; 'european'],1,tt0063929,8.8,73424.0";
        var top3BestRated = recommender.getTopNRatedContent(3);
        Content firstRating,secondRating,thirdRating;
        try {
            firstRating = Content.createContentFromFields(first.split(","));
            secondRating = Content.createContentFromFields(second.split(","));
            thirdRating = Content.createContentFromFields(third.split(","));
        } catch (Exception e) {
            throw new IllegalArgumentException("bad testing example");
        }
        assertTrue(top3BestRated.containsAll(List.of(firstRating,secondRating,thirdRating)));
        assertEquals(3, top3BestRated.size(), "only 3 should be included");
    }

    @Test
    void testGetSimilarContentShow() {
        String description = "ts22164,Monty Python's Flying Circus,SHOW,A British sketch comedy series with the shows being composed of surreality; risqué or innuendo-laden humour; sight gags and observational sketches without punchlines.,1969,30,['comedy'; 'european'],1,tt0063929,8.8,73424.0";
        Content content;
        try {
            content = Content.createContentFromFields(description.split(","));
        } catch(Exception e) {
            throw new IllegalArgumentException("bad example");
        }
        var similarTo = recommender.getSimilarContent(content);
        assertEquals(1, similarTo.size(), "no other shows were in the data");
    }

    @Test
    void testGetSimilarContentMovie() {
        String desc1 = "tm127384,Monty Python and the Holy Grail,MOVIE,\"King Arthur; accompanied by his squire; recruits his Knights of the Round Table; including Sir Bedevere the Wise; Sir Lancelot the Brave; Sir Robin the Not-Quite-So-Brave-As-Sir-Lancelot and Sir Galahad the Pure. On the way; Arthur battles the Black Knight who; despite having had all his limbs chopped off; insists he can still fight. They reach Camelot; but Arthur decides not  to enter; as \"\"it is a silly place\"\".\",1975,91,['fantasy'; 'action'; 'comedy'],-1,tt0071853,8.2,534486.0";
        Content cont1;
        try {
            cont1 = Content.createContentFromFields(desc1.split(","));
        } catch (Exception e) {
            throw new IllegalArgumentException("bad testing example");
        }
        var similarContent = recommender.getSimilarContent(cont1);
        assertEquals(5, similarContent.size(), "only 4 other movies are in the database");
    }

    @Test
    void testGetContentByKeywords() {
        String desc1 = "tm127384,Monty Python and the Holy Grail,MOVIE,\"King Arthur; accompanied by his squire; recruits his Knights of the Round Table; including Sir Bedevere the Wise; Sir Lancelot the Brave; Sir Robin the Not-Quite-So-Brave-As-Sir-Lancelot and Sir Galahad the Pure. On the way; Arthur battles the Black Knight who; despite having had all his limbs chopped off; insists he can still fight. They reach Camelot; but Arthur decides not  to enter; as \"\"it is a silly place\"\".\",1975,91,['fantasy'; 'action'; 'comedy'],-1,tt0071853,8.2,534486.0";
        Content cont1;
        try {
            cont1 = Content.createContentFromFields(desc1.split(","));
        } catch (Exception e) {
            throw new IllegalArgumentException("bad testing example");
        }
        var found = recommender.getContentByKeywords("king");
        assertEquals(1, found.size(), "only tm127384 matches");
        assertTrue(found.contains(cont1), "tm127384 should be found");
    }
}