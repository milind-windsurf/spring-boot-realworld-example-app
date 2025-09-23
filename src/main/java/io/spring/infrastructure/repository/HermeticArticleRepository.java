package io.spring.infrastructure.repository;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.infrastructure.service.SeedingService;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hermetic")
public class HermeticArticleRepository implements ArticleRepository {
    private final Map<String, Article> articles = new ConcurrentHashMap<>();
    private final Map<String, Article> articlesBySlug = new ConcurrentHashMap<>();
    private final SeedingService seedingService;

    public HermeticArticleRepository(SeedingService seedingService) {
        this.seedingService = seedingService;
    }

    @Override
    public void save(Article article) {
        articles.put(article.getId(), article);
        articlesBySlug.put(article.getSlug(), article);
        seedingService.seedArticleData(article.getSlug(), article);
    }

    @Override
    public Optional<Article> findById(String id) {
        return Optional.ofNullable(articles.get(id));
    }

    @Override
    public Optional<Article> findBySlug(String slug) {
        return seedingService.getSeededArticle(slug)
            .or(() -> Optional.ofNullable(articlesBySlug.get(slug)));
    }

    @Override
    public void remove(Article article) {
        articles.remove(article.getId());
        articlesBySlug.remove(article.getSlug());
    }
}
