package io.spring.infrastructure.repository;

import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.infrastructure.service.SeedingService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("hermetic")
public class HermeticArticleFavoriteRepository implements ArticleFavoriteRepository {
  private final SeedingService seedingService;

  @Autowired
  public HermeticArticleFavoriteRepository(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public void save(ArticleFavorite articleFavorite) {
    String key = "favorite:" + articleFavorite.getArticleId() + ":" + articleFavorite.getUserId();
    seedingService.putSeed(key, articleFavorite);
  }

  @Override
  public Optional<ArticleFavorite> find(String articleId, String userId) {
    String key = "favorite:" + articleId + ":" + userId;
    return Optional.ofNullable((ArticleFavorite) seedingService.getSeed(key));
  }

  @Override
  public void remove(ArticleFavorite favorite) {
    String key = "favorite:" + favorite.getArticleId() + ":" + favorite.getUserId();
    seedingService.putSeed(key, null);
  }
}
