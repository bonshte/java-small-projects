package bg.sofia.uni.fmi.mjt.client.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class Article {
    private Source source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String content;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return Objects.equals(source, article.source) && Objects.equals(author, article.author)
                && Objects.equals(title, article.title) && Objects.equals(description, article.description)
                && Objects.equals(url, article.url) && Objects.equals(urlToImage, article.urlToImage)
                && Objects.equals(publishedAt, article.publishedAt) && Objects.equals(content, article.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, author, title, description, url, urlToImage, publishedAt, content);
    }
}
