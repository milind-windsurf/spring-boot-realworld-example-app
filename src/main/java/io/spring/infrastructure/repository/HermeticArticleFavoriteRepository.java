package io.spring.infrastructure.repository;

import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.infrastructure.service.SeedingService;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hermetic")
public class HermeticArticleFavoriteRepository implements ArticleFavoriteRepository {
    private final Map<String, ArticleFavorite> favorites = new ConcurrentHashMap<>();
    private final SeedingService seedingService;

    public HermeticArticleFavoriteRepository(SeedingService seedingService) {
        this.seedingService = seedingService;
    }

    @Override
    public void save(ArticleFavorite articleFavorite) {
        String key = articleFavorite.getArticleId() + ":" + articleFavorite.getUserId();
        favorites.put(key, articleFavorite);
        seedingService.seedArticleFavoriteData(key, articleFavorite);
    }

    @Override
    public Optional<ArticleFavorite> find(String articleId, String userId) {
        String key = articleId + ":" + userId;
        return seedingService.getSeededArticleFavorite(key)
            .or(() -> Optional.ofNullable(favorites.get(key)));
    }

    @Override
    public void remove(ArticleFavorite favorite) {
        String key = favorite.getArticleId() + ":" + favorite.getUserId();
        favorites.remove(key);
    }
}
