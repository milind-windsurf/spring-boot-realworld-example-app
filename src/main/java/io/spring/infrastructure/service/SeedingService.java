package io.spring.infrastructure.service;

import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.UserData;
import io.spring.core.article.Article;
import io.spring.core.comment.Comment;
import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;

@Profile("hermetic")
public class SeedingService {
  private final ConcurrentHashMap<String, Object> seeds = new ConcurrentHashMap<>();

  public void seedUserData(String userId, UserData userData) {
    seeds.put("user:" + userId, userData);
  }

  public void seedUser(User user) {
    seeds.put("user_entity:" + user.getId(), user);
  }

  public void seedArticleData(String slug, ArticleData articleData) {
    seeds.put("article:" + slug, articleData);
  }

  public void seedArticle(Article article) {
    seeds.put("article_entity:" + article.getId(), article);
  }

  public void seedCommentData(String commentId, CommentData commentData) {
    seeds.put("comment:" + commentId, commentData);
  }

  public void seedComment(Comment comment) {
    seeds.put("comment_entity:" + comment.getId(), comment);
  }

  public void seedArticleFavorite(ArticleFavorite favorite) {
    seeds.put("favorite:" + favorite.getArticleId() + ":" + favorite.getUserId(), favorite);
  }

  public void seedFollowRelation(FollowRelation relation) {
    seeds.put("follow:" + relation.getUserId() + ":" + relation.getTargetId(), relation);
  }

  public void seedToken(String userId, String token) {
    seeds.put("token:" + userId, token);
  }

  public Optional<UserData> getSeededUserData(String userId) {
    return Optional.ofNullable((UserData) seeds.get("user:" + userId));
  }

  public Optional<User> getSeededUser(String userId) {
    return Optional.ofNullable((User) seeds.get("user_entity:" + userId));
  }

  public Optional<ArticleData> getSeededArticleData(String slug) {
    return Optional.ofNullable((ArticleData) seeds.get("article:" + slug));
  }

  public Optional<Article> getSeededArticle(String articleId) {
    return Optional.ofNullable((Article) seeds.get("article_entity:" + articleId));
  }

  public Optional<CommentData> getSeededCommentData(String commentId) {
    return Optional.ofNullable((CommentData) seeds.get("comment:" + commentId));
  }

  public Optional<Comment> getSeededComment(String commentId) {
    return Optional.ofNullable((Comment) seeds.get("comment_entity:" + commentId));
  }

  public Optional<ArticleFavorite> getSeededArticleFavorite(String articleId, String userId) {
    return Optional.ofNullable((ArticleFavorite) seeds.get("favorite:" + articleId + ":" + userId));
  }

  public Optional<FollowRelation> getSeededFollowRelation(String userId, String targetId) {
    return Optional.ofNullable((FollowRelation) seeds.get("follow:" + userId + ":" + targetId));
  }

  public Optional<String> getSeededToken(String userId) {
    return Optional.ofNullable((String) seeds.get("token:" + userId));
  }

  public void clearAllSeeds() {
    seeds.clear();
  }
}
