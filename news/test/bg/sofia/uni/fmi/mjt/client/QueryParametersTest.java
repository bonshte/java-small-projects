package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.client.exceptions.query.InvalidCountryException;
import bg.sofia.uni.fmi.mjt.client.exceptions.query.InvalidQueryException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueryParametersTest {
    @Test
    void testBuilderPattern() {
        try {
            QueryParameters queryParameters = QueryParameters.builder(List.of("word", "no"))
                    .setCountry("Bulgaria")
                    .setCategory(Category.BUSINESS)
                    .setPageSize(25)
                    .build();
            assertEquals("bg", queryParameters.getCountry(), "country should be bg code");
            assertEquals("business", queryParameters.getCategory(), "category should be business");
            assertEquals(25, queryParameters.getPageSize()," page size should be 25");
            assertIterableEquals(List.of("word", "no"), queryParameters.getKeywords(), "keywords should match");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testBuilderFailsWithNullArgument() {
        assertThrows(InvalidQueryException.class, () -> QueryParameters.builder(null).build(),
                "query requires words");
    }
    @Test
    void testBuilderFailsWithEmptyList() {
        assertThrows(InvalidQueryException.class, () -> QueryParameters.builder(List.of()).build(),
                "query requires words");
    }
    @Test
    void testBuilderFailsWithListOfBlankWords() {
        assertThrows(InvalidQueryException.class, () -> QueryParameters.builder(List.of(" ", " ")).build(),
                "query requires non blank word");
    }
    @Test
    void testBuilderWorksWithAtLeastOneNonBlankWord() {
        assertDoesNotThrow( () -> QueryParameters.builder(List.of(" ", " ", "w")).build(),
                "query should be built with at least one word");
    }
    @Test
    void setCategoryWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> QueryParameters.builder(List.of("word")).setCategory(null),
                "category passed cannot be null");
    }
    @Test
    void setCountryWithInvalidCountry() {
        assertThrows(InvalidCountryException.class,
                () -> QueryParameters.builder(List.of("word")).setCountry("tanzaniq").build(), "no such country");
    }

    @Test
    void setCountryWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> QueryParameters.builder(List.of("word")).setCountry(null),
                "null is not valid argument");
        assertThrows(IllegalArgumentException.class,
                () -> QueryParameters.builder(List.of("word")).setCountry(" "),
                "empty or blank is not a valid argument");
    }
    @Test
    void testGetAsUriField() {
        try {
            QueryParameters queryParameters = QueryParameters.builder(List.of("word", "no"))
                    .setCountry("Bulgaria")
                    .setCategory(Category.BUSINESS)
                    .setPageSize(25)
                    .build();
            assertEquals("q=word+no&country=bg&category=business&pageSize=25&page=1",
                    queryParameters.getAsUriField(),
                    "uri field is not constructed as desired");
        } catch (Exception e) {
            fail();
        }
    }


}