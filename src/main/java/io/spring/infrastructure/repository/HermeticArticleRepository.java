package io.spring.infrastructure.repository;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.infrastructure.service.SeedingService;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;

@Profile("hermetic")
public class HermeticArticleRepository implements ArticleRepository {
  private final ConcurrentHashMap<String, Article> articles = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Article> articlesBySlug = new ConcurrentHashMap<>();
  private final SeedingService seedingService;

  public HermeticArticleRepository(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public void save(Article article) {
    articles.put(article.getId(), article);
    articlesBySlug.put(article.getSlug(), article);
    seedingService.seedArticle(article);
  }

  @Override
  public Optional<Article> findById(String id) {
    return seedingService.getSeededArticle(id).or(() -> Optional.ofNullable(articles.get(id)));
  }

  @Override
  public Optional<Article> findBySlug(String slug) {
    return Optional.ofNullable(articlesBySlug.get(slug))
        .or(
            () ->
                articles.values().stream()
                    .filter(article -> slug.equals(article.getSlug()))
                    .findFirst());
  }

  @Override
  public void remove(Article article) {
    articles.remove(article.getId());
    articlesBySlug.remove(article.getSlug());
  }
}
