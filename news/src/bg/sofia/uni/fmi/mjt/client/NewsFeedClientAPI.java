package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.client.dto.Article;
import bg.sofia.uni.fmi.mjt.client.exceptions.NewsFeedClientException;
import bg.sofia.uni.fmi.mjt.client.exceptions.api.ApiException;

import java.util.List;

public interface NewsFeedClientAPI {
    public List<Article> getAllArticles(QueryParameters queryParameters)
            throws NewsFeedClientException, ApiException;
    public List<Article> getNArticles(QueryParameters queryParameters, int amount)
            throws NewsFeedClientException, ApiException;
}
