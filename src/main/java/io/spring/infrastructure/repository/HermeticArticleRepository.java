package io.spring.infrastructure.repository;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.article.Tag;
import io.spring.infrastructure.service.SeedingService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("hermetic")
public class HermeticArticleRepository implements ArticleRepository {
  private final SeedingService seedingService;

  @Autowired
  public HermeticArticleRepository(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public void save(Article article) {
    seedingService.putSeed("article:entity:" + article.getId(), article);
    seedingService.putSeed("article:slug:" + article.getSlug(), article);
    
    for (Tag tag : article.getTags()) {
      seedingService.putSeed("tag:entity:" + tag.getId(), tag);
      seedingService.putSeed("tag:name:" + tag.getName(), tag);
      seedingService.putSeed("article:tag:" + article.getId() + ":" + tag.getId(), tag);
    }
  }

  @Override
  public Optional<Article> findById(String id) {
    return Optional.ofNullable((Article) seedingService.getSeed("article:entity:" + id));
  }

  @Override
  public Optional<Article> findBySlug(String slug) {
    return Optional.ofNullable((Article) seedingService.getSeed("article:slug:" + slug));
  }

  @Override
  public void remove(Article article) {
    seedingService.putSeed("article:entity:" + article.getId(), null);
    seedingService.putSeed("article:slug:" + article.getSlug(), null);
  }
}
