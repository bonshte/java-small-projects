package bg.sofia.uni.fmi.mjt.client;


import bg.sofia.uni.fmi.mjt.client.dto.Article;
import bg.sofia.uni.fmi.mjt.client.dto.ArticlesResponse;
import bg.sofia.uni.fmi.mjt.client.exceptions.NewsFeedClientException;
import bg.sofia.uni.fmi.mjt.client.exceptions.api.ApiBadParameterException;
import bg.sofia.uni.fmi.mjt.client.exceptions.api.ApiException;
import bg.sofia.uni.fmi.mjt.client.exceptions.api.ApiFreeVersionException;
import bg.sofia.uni.fmi.mjt.client.exceptions.api.ApiKeyException;
import bg.sofia.uni.fmi.mjt.client.exceptions.api.ApiRequestTimeLimitException;
import bg.sofia.uni.fmi.mjt.client.exceptions.api.ApiServerErrorException;
import bg.sofia.uni.fmi.mjt.client.exceptions.query.InvalidQueryException;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import org.mockito.ArgumentMatchers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;


class NewsFeedClientTest {
    private static final int TOO_MANY_REQUESTS_STATUS_CODE = 429;
    private static final int REACHED_RESULTS_AFTER_100_CODE = 426;
    private static final String VALID_KEY = "pretend-valid-key";
    private static final String RESPONSE_BODY_OK = "{\"status\":\"ok\",\"totalResults\":35,\"articles\":[{\"source\":{\"id\":\"google-news\",\"name\":\"Google News\"},\"author\":\"James Gosling\",\"title\":\"Democrats may have to bend on negotiations with GOP on debt ceiling - The Hill\",\"description\":null,\"url\":\"https://news.google.com/__i/rss/rd/articles/CBMicGh0dHBzOi8vdGhlaGlsbC5jb20vaG9tZW5ld3Mvc2VuYXRlLzM4MjE4MjUtZGVtb2NyYXRzLW1heS1oYXZlLXRvLWJlbmQtb24tbmVnb3RpYXRpb25zLXdpdGgtZ29wLW9uLWRlYnQtY2VpbGluZy_SAXRodHRwczovL3RoZWhpbGwuY29tL2hvbWVuZXdzL3NlbmF0ZS8zODIxODI1LWRlbW9jcmF0cy1tYXktaGF2ZS10by1iZW5kLW9uLW5lZ290aWF0aW9ucy13aXRoLWdvcC1vbi1kZWJ0LWNlaWxpbmcvYW1wLw?oc=5\",\"urlToImage\":null,\"publishedAt\":\"2023-01-22T11:00:00Z\",\"content\":\"some content\"}]}";
    private static final String RESPONSE_BODY_OK_2 = "{\"status\":\"ok\",\"totalResults\":10,\"articles\":[{\"source\":{\"id\":null,\"name\":\"NBCSports.com\"},\"author\":\"Josh Alper\",\"title\":\"Nick Sirianni on Jalen Hurts: It’s like having Michael Jordan out there - NBC Sports\",\"description\":\"It didn’t take long for Eagles quarterback Jalen Hurts to set the tone for Saturday night’s game against the Giants.Hurts his wide receiver DeVonta Smith for 40 yards on the second snap of the game and the Eagles were up 7-0 thanks to a touchdown pass to tigh…\",\"url\":\"https://profootballtalk.nbcsports.com/2023/01/22/nick-sirianni-on-jalen-hurts-its-like-having-michael-jordan-out-there/\",\"urlToImage\":\"https://profootballtalk.nbcsports.com/wp-content/uploads/sites/25/2023/01/GettyImages-1246436228-e1674392435281.jpg\",\"publishedAt\":\"2023-01-22T13:00:00Z\",\"content\":\"It didnt take long for Eagles quarterback Jalen Hurts to set the tone for Saturday nights game against the Giants.\\r\\nHurts his wide receiver DeVonta Smith for 40 yards on the second snap of the game a… [+1265 chars]\"},{\"source\":{\"id\":null,\"name\":\"YouTube\"},\"author\":null,\"title\":\"LIVE: 10 dead in Monterey Park mass shooting, police say - ABC7\",\"description\":\"Ten people were killed and 10 were wounded in a mass shooting at a Lunar New Year celebration in Monterey Park, police said. The gunman has not yet been loca...\",\"url\":\"https://www.youtube.com/watch?v=vJzx5SYl2ZI\",\"urlToImage\":\"https://i.ytimg.com/vi/vJzx5SYl2ZI/maxresdefault_live.jpg\",\"publishedAt\":\"2023-01-22T12:58:05Z\",\"content\":null}]}";
    private HttpClient newsFeedClientMock;
    private HttpResponse<String> httpArticleResponseMock;
    private NewsFeedClient client;
    private QueryParameters queryParameters;
    private static final Gson GSON = new Gson();
    private static final Article VALID_ARTICLE_TEST = Arrays.stream(GSON.fromJson(RESPONSE_BODY_OK,ArticlesResponse.class)
            .getArticles()).iterator().next();


    @BeforeEach
    void setUp() throws IOException, InterruptedException, InvalidQueryException {
        newsFeedClientMock = mock(HttpClient.class);
        httpArticleResponseMock = mock(HttpResponse.class);
        when(newsFeedClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpArticleResponseMock);
        client = new NewsFeedClient(VALID_KEY, newsFeedClientMock);
        queryParameters = QueryParameters.builder(List.of("word")).build();
    }



    @Test
    void testNewsFeedClientInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new NewsFeedClient(null, newsFeedClientMock),
                "null api key passed, should throw illegal argument");
        assertThrows(IllegalArgumentException.class, () -> new NewsFeedClient(VALID_KEY, null),
                "null client passed, should throw illegal argument");
    }

    @Test
    void testGetNArticlesExceptionThrownWhenBadRequestIsReturned() {
        try {
            when(httpArticleResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK,
                    HttpURLConnection.HTTP_OK,HttpURLConnection.HTTP_INTERNAL_ERROR);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK);
            assertThrows(ApiException.class, () ->client.getNArticles(queryParameters, 5),
                    "exception should be thrown in case the client is doing something wrong with the requests");

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void testGetNArticlesReturnsCorrectAmountIfResponseReturnedMoreThanNeeded() {
        try {
            when(httpArticleResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK_2);
            var articles = client.getNArticles(queryParameters, 5);
            assertEquals(5, articles.size(), "returned articles should be 5");
            assertNotEquals(articles.get(0), articles.get(1), "articles are not the same");
            assertEquals(articles.get(0), articles.get(2),"even and odd articles are the same in the list");
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void testGetNArticlesShouldReturnEvenIfDeveloperLimitIsReached() {
        try {
            when(httpArticleResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK,
                    HttpURLConnection.HTTP_OK, REACHED_RESULTS_AFTER_100_CODE);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK);
            var articles = client.getNArticles(queryParameters, 5);
            assertNotEquals(0, articles.size(), "the retrieved articles should be returned," +
                    " even if some later request tried to reach over the free limit");

        } catch (Exception e) {
            Assertions.fail();
        }
    }
    @Test
    void testGetNArticlesWithPositiveNumber() {
        try {
            when(httpArticleResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK);
            var articles = client.getNArticles(queryParameters, 10);
            assertEquals(10, articles.size(), "exactly 10 should articles should be stored");
            for (var article : articles) {
                assertEquals(article, VALID_ARTICLE_TEST, "all deserialized articles should match the expected");
            }
            var articles1 = client.getNArticles(queryParameters,100);
            assertEquals(100, articles1.size(), "articles returned should be 100");
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test void testGetNArticlesWithNonPositiveNumber() {
        try {
            when(httpArticleResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK);
            assertThrows(IllegalArgumentException.class, () -> client.getNArticles(queryParameters,0),
                    "articles requested must be positive amount");
            assertThrows(IllegalArgumentException.class, () -> client.getNArticles(queryParameters,-3),
                    "articles requested must be positive amount");
        } catch (Exception e) {
            Assertions.fail();
        }
    }
    @Test
    void testGetNArticlesWithBadRequestCode() {
        try {
            when(httpArticleResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK);
            assertThrows(ApiBadParameterException.class, () -> client.getNArticles(queryParameters,3),
                    "when first request returns bad response, it should be thrown");
        } catch (Exception e) {
            Assertions.fail();
        }
    }
    @Test
    void testGetNArticlesWithUnauthorizedCode() {
        try {
            when(httpArticleResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK);
            assertThrows(ApiKeyException.class, () -> client.getNArticles(queryParameters,3),
                    "when first request returns unauthorized request, it should be thrown");

        } catch (Exception e) {
            Assertions.fail();
        }
    }
    @Test
    void testGetNArticlesWithRequestLimitCode() {
        try {
            when(httpArticleResponseMock.statusCode()).thenReturn(TOO_MANY_REQUESTS_STATUS_CODE);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK);
            assertThrows(ApiRequestTimeLimitException.class, () -> client.getNArticles(queryParameters,3),
                    "when first request returns limit of requests reached code" +
                            ", it should be thrown");

        } catch (Exception e) {
            Assertions.fail();
        }
    }
    @Test
    void testGetNArticlesWithServerErrorCode() {
        try {
            when(httpArticleResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK);
            assertThrows(ApiServerErrorException.class, () -> client.getNArticles(queryParameters,3),
                    "when server responds with with server error, on first request of a series of a requests," +
                            "exception should be thrown");

        } catch (Exception e) {
            Assertions.fail();
        }
    }
    @Test
    void testGetNArticlesWithUnknownErrorCode() {
        try {
            when(httpArticleResponseMock.statusCode()).thenReturn(75);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK);
            assertThrows(NewsFeedClientException.class, () -> client.getNArticles(queryParameters,3),
                    "when unknown exception is thrown for the first request to a server," +
                            "newsFeedClientException should be thrown");

        } catch (Exception e) {
            Assertions.fail();
        }
    }
    @Test
    void testGetNArticlesWithNullQuery() {
        try {
            when(httpArticleResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK,
                    HttpURLConnection.HTTP_OK,HttpURLConnection.HTTP_INTERNAL_ERROR);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK);
            assertThrows(IllegalArgumentException.class, () ->client.getNArticles(null, 5),
                    "illegal argument should be thrown when null is passed");

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void testGetAllArticlesNoProblemsWithRequests() {
        try {
            QueryParameters queryParameters2 = QueryParameters.builder(List.of("word")).setPageSize(2).build();
            when(httpArticleResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK_2);
            var allArticles = client.getAllArticles(queryParameters2);
            //this is because the first response tells us how many are the total results
            assertEquals(10, allArticles.size(), "exactly 10 articles should be returned");

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void testGetAllArticlesProblemWithARequest() {
        try {
            QueryParameters queryParameters2 = QueryParameters.builder(List.of("word")).setPageSize(2).build();
            when(httpArticleResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK,
                    HttpURLConnection.HTTP_INTERNAL_ERROR);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK_2);
            assertThrows(ApiException.class, () -> client.getAllArticles(queryParameters2),
                    "if a request is thrown on the way to collecting all articles further attempts " +
                            "are considered meaningless to occupy the server with" +
                            "and exception should be thrown for possible malicious actions towards the api");

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void testGetAllArticlesFreeLicenseProblem() {
        try {
            QueryParameters queryParameters2 = QueryParameters.builder(List.of("word")).setPageSize(2).build();
            when(httpArticleResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK,
                    REACHED_RESULTS_AFTER_100_CODE);
            when(httpArticleResponseMock.body()).thenReturn(RESPONSE_BODY_OK_2);
            var collectedArticles = client.getAllArticles(queryParameters2);
            assertNotEquals(0, collectedArticles.size(),
                    "if the request limit is hit for users with free license, they get all allowed requests");

        } catch (Exception e) {
            Assertions.fail();
        }
    }
}