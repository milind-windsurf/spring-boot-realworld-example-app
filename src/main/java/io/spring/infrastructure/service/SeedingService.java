package io.spring.infrastructure.service;

import io.spring.core.article.Article;
import io.spring.core.comment.Comment;
import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hermetic")
public class SeedingService {
    private final Map<String, Object> seeds = new ConcurrentHashMap<>();

    public void seedUserData(String userId, User userData) {
        seeds.put("user:" + userId, userData);
    }

    public void seedArticleData(String slug, Article articleData) {
        seeds.put("article:" + slug, articleData);
    }

    public void seedCommentData(String commentId, Comment commentData) {
        seeds.put("comment:" + commentId, commentData);
    }

    public void seedArticleFavoriteData(String key, ArticleFavorite favoriteData) {
        seeds.put("favorite:" + key, favoriteData);
    }

    public void seedFollowRelationData(String key, FollowRelation relationData) {
        seeds.put("follow:" + key, relationData);
    }

    public Optional<User> getSeededUser(String userId) {
        return Optional.ofNullable((User) seeds.get("user:" + userId));
    }

    public Optional<Article> getSeededArticle(String slug) {
        return Optional.ofNullable((Article) seeds.get("article:" + slug));
    }

    public Optional<Comment> getSeededComment(String commentId) {
        return Optional.ofNullable((Comment) seeds.get("comment:" + commentId));
    }

    public Optional<ArticleFavorite> getSeededArticleFavorite(String key) {
        return Optional.ofNullable((ArticleFavorite) seeds.get("favorite:" + key));
    }

    public Optional<FollowRelation> getSeededFollowRelation(String key) {
        return Optional.ofNullable((FollowRelation) seeds.get("follow:" + key));
    }

    public Optional<String> getSeededToken(String userId) {
        return Optional.ofNullable((String) seeds.get("token:" + userId));
    }

    public void seedToken(String userId, String token) {
        seeds.put("token:" + userId, token);
    }

    public void clearAllSeeds() {
        seeds.clear();
    }
}
