package bg.sofia.uni.fmi.mjt.client.dto;

import java.util.Arrays;

public class ArticlesResponse {
    private Article[] articles;
    private Integer totalResults;
    public Article[] getArticles() {
        return articles;
    }
    public Integer getTotalResults() {
        return totalResults;
    }

}
