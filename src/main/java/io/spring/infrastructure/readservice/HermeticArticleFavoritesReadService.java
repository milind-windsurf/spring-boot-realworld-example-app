package io.spring.infrastructure.readservice;

import io.spring.application.data.ArticleFavoriteCount;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.ArticleFavoritesReadService;
import io.spring.infrastructure.service.SeedingService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hermetic")
public class HermeticArticleFavoritesReadService implements ArticleFavoritesReadService {
  private final SeedingService seedingService;

  @Autowired
  public HermeticArticleFavoritesReadService(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public boolean isUserFavorite(String userId, String articleId) {
    String key = "favorite:" + articleId + ":" + userId;
    return seedingService.getSeed(key) != null;
  }

  @Override
  public int articleFavoriteCount(String articleId) {
    int count = 0;
    Object favorite = seedingService.getSeed("favorite:" + articleId + ":" + getJaneUserId());
    if (favorite != null) count++;
    return count;
  }

  private String getJaneUserId() {
    Object user = seedingService.getSeed("user:username:jane");
    return user != null ? ((io.spring.core.user.User) user).getId() : null;
  }

  @Override
  public List<ArticleFavoriteCount> articlesFavoriteCount(List<String> ids) {
    return ids.stream()
        .map(id -> new ArticleFavoriteCount(id, articleFavoriteCount(id)))
        .collect(Collectors.toList());
  }

  @Override
  public Set<String> userFavorites(List<String> ids, User currentUser) {
    return ids.stream()
        .filter(id -> isUserFavorite(currentUser.getId(), id))
        .collect(Collectors.toSet());
  }
}
