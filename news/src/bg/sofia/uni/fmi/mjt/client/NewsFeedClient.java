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
import com.google.gson.Gson;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsFeedClient implements NewsFeedClientAPI {
    private static final String API_KEY = "ee17c126fcb74591874837af01f23ee6";
    private static final String API_ENDPOINT_SCHEME = "http";
    private static final String API_ENDPOINT_HOST = "newsapi.org";
    private static final String API_ENDPOINT_PATH = "/v2/top-headlines";
    private static final int TOO_MANY_REQUESTS_STATUS_CODE = 429;
    private static final int REACHED_RESULTS_AFTER_100_CODE = 426;
    private static final Gson GSON = new Gson();
    private String apiKey;
    private HttpClient newsFeedClient;

    public NewsFeedClient(String apiKey, HttpClient client) {
        if (apiKey == null || apiKey.isBlank() || apiKey.isEmpty() || client == null) {
            throw new IllegalArgumentException("null, empty or blank parameter passed to constructor");
        }
        this.apiKey = apiKey;
        this.newsFeedClient = client;
    }

    public List<Article> getNArticles(QueryParameters queryParameters, int amount)
            throws NewsFeedClientException, ApiException {
        if ( amount < 1 || queryParameters == null) {
            throw new IllegalArgumentException("null or non positive amount passed to get n articles");
        }
        List<Article> articles = new ArrayList<>();
        while (amount > 0) {
            try {
                var response = getArticlesResponse(queryParameters);
                if (response.getArticles().length == 0) {
                    break;
                }
                if (amount < response.getArticles().length) {
                    articles.addAll(Arrays.stream(response.getArticles()).limit(amount).toList());
                    return articles;
                } else {
                    articles.addAll(Arrays.stream(response.getArticles()).toList());
                }
                amount -= response.getArticles().length;
                queryParameters.nextPage();
            } catch (ApiFreeVersionException e) {
                return articles;
            }
        }
        return articles;
    }
    //returns as many articles as it can, before hitting the limit of 100,
    // as it is created it will return 5 pages of 20 each, if the total results are more than 100
    public List<Article> getAllArticles(QueryParameters queryParameters) throws NewsFeedClientException, ApiException {
        if (queryParameters == null) {
            throw new IllegalArgumentException("null passed");
        }
        List<Article> articles = new ArrayList<>();
        ArticlesResponse response = getArticlesResponse(queryParameters);
        int extractedArticlesCount = response.getArticles().length;
        int pagesLeft = extractedArticlesCount < queryParameters.getPageSize() ? 0 :
                Math.ceilDiv((response.getTotalResults() - extractedArticlesCount), queryParameters.getPageSize());
        articles.addAll(Arrays.stream(response.getArticles()).toList());

        for (int i = 0; i < pagesLeft; ++i) {
            try {
                queryParameters.nextPage();
                ArticlesResponse nextResponse = getArticlesResponse(queryParameters);
                articles.addAll(Arrays.stream(nextResponse.getArticles()).toList());
            } catch (ApiFreeVersionException e) {
                return articles;
            }
        }

        return articles;
    }

    private ArticlesResponse getArticlesResponse(QueryParameters queryParams)
            throws NewsFeedClientException, ApiException {
        if (queryParams == null) {
            throw new IllegalArgumentException("null passed");
        }
        HttpResponse<String> response;

        try {
            URI uri = new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH,
                    queryParams.getAsUriField(), null);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).header("x-api-key", apiKey).build();
            response = newsFeedClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new NewsFeedClientException("could not connect to remote api", e);
        }
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            return GSON.fromJson(response.body(), ArticlesResponse.class);
        }

        switch (response.statusCode()) {
            case HttpURLConnection.HTTP_BAD_REQUEST ->
                    throw new ApiBadParameterException("missing or wrong parameter");
            case HttpURLConnection.HTTP_UNAUTHORIZED ->
                    throw new ApiKeyException("invalid or missing key");
            case TOO_MANY_REQUESTS_STATUS_CODE ->
                    throw new ApiRequestTimeLimitException("too many requests sent in a moment");
            case HttpURLConnection.HTTP_INTERNAL_ERROR ->
                    throw new ApiServerErrorException("something went wrong");
            case REACHED_RESULTS_AFTER_100_CODE ->
                    throw new ApiFreeVersionException("free version does not support after the 100th result");
        }
        throw new NewsFeedClientException("unexpected response code from api");
    }

}
