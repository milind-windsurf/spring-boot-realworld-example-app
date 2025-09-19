package io.spring.infrastructure.service;

import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.application.data.UserData;
import io.spring.core.article.Article;
import io.spring.core.article.Tag;
import io.spring.core.comment.Comment;
import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hermetic")
public class SeedingService {
  private final Map<String, Object> seeds = new ConcurrentHashMap<>();
  private final Map<String, String> tokenToUserMap = new ConcurrentHashMap<>();

  public void seedUserData(String userId, UserData userData) {
    seeds.put("user:" + userId, userData);
  }

  public void seedArticleData(String slug, ArticleData articleData) {
    seeds.put("article:" + slug, articleData);
  }

  public void seedToken(String token, String userId) {
    tokenToUserMap.put(token, userId);
  }

  public Optional<UserData> getSeededUser(String userId) {
    return Optional.ofNullable((UserData) seeds.get("user:" + userId));
  }

  public Optional<String> getSeededToken(String userId) {
    return tokenToUserMap.entrySet().stream()
        .filter(entry -> entry.getValue().equals(userId))
        .map(Map.Entry::getKey)
        .findFirst();
  }

  public Optional<String> getUserFromToken(String token) {
    return Optional.ofNullable(tokenToUserMap.get(token));
  }

  public void seedDefaultData() {
    seedDefaultUsers();
    seedDefaultArticles();
    seedDefaultTags();
  }

  private void seedDefaultUsers() {
    User user1 = new User("john@example.com", "john", "password", "John's bio", "https://example.com/john.jpg");
    User user2 = new User("jane@example.com", "jane", "password", "Jane's bio", "https://example.com/jane.jpg");
    
    seeds.put("user:entity:" + user1.getId(), user1);
    seeds.put("user:entity:" + user2.getId(), user2);
    seeds.put("user:username:john", user1);
    seeds.put("user:username:jane", user2);
    seeds.put("user:email:john@example.com", user1);
    seeds.put("user:email:jane@example.com", user2);

    UserData userData1 = new UserData(user1.getId(), user1.getEmail(), user1.getUsername(), user1.getBio(), user1.getImage());
    UserData userData2 = new UserData(user2.getId(), user2.getEmail(), user2.getUsername(), user2.getBio(), user2.getImage());
    
    seedUserData(user1.getId(), userData1);
    seedUserData(user2.getId(), userData2);

    String token1 = "hermetic-token-" + user1.getId();
    String token2 = "hermetic-token-" + user2.getId();
    seedToken(token1, user1.getId());
    seedToken(token2, user2.getId());

    FollowRelation followRelation = new FollowRelation(user1.getId(), user2.getId());
    seeds.put("follow:" + user1.getId() + ":" + user2.getId(), followRelation);
  }

  private void seedDefaultArticles() {
    User user1 = (User) seeds.get("user:username:john");
    User user2 = (User) seeds.get("user:username:jane");

    Article article1 = new Article("How to train your dragon", "Ever wonder how?", "You have to believe", Arrays.asList("dragons", "training"), user1.getId());
    Article article2 = new Article("How to train your cat", "Ever wonder how?", "You have to be patient", Arrays.asList("cats", "training"), user2.getId());

    seeds.put("article:entity:" + article1.getId(), article1);
    seeds.put("article:entity:" + article2.getId(), article2);
    seeds.put("article:slug:" + article1.getSlug(), article1);
    seeds.put("article:slug:" + article2.getSlug(), article2);

    ProfileData profile1 = new ProfileData(user1.getId(), user1.getUsername(), user1.getBio(), user1.getImage(), false);
    ProfileData profile2 = new ProfileData(user2.getId(), user2.getUsername(), user2.getBio(), user2.getImage(), false);

    ArticleData articleData1 = new ArticleData(article1.getId(), article1.getSlug(), article1.getTitle(), 
        article1.getDescription(), article1.getBody(), false, 0, article1.getCreatedAt(), article1.getUpdatedAt(),
        Arrays.asList("dragons", "training"), profile1);
    ArticleData articleData2 = new ArticleData(article2.getId(), article2.getSlug(), article2.getTitle(),
        article2.getDescription(), article2.getBody(), false, 0, article2.getCreatedAt(), article2.getUpdatedAt(),
        Arrays.asList("cats", "training"), profile2);

    seedArticleData(article1.getSlug(), articleData1);
    seedArticleData(article2.getSlug(), articleData2);
    seeds.put("articledata:" + article1.getId(), articleData1);
    seeds.put("articledata:" + article2.getId(), articleData2);

    Comment comment1 = new Comment("Great article!", user2.getId(), article1.getId());
    seeds.put("comment:entity:" + comment1.getId(), comment1);
    seeds.put("comment:article:" + article1.getId(), Arrays.asList(comment1.getId()));

    CommentData commentData1 = new CommentData(comment1.getId(), comment1.getBody(), comment1.getArticleId(),
        comment1.getCreatedAt(), comment1.getCreatedAt(), profile2);
    seeds.put("commentdata:" + comment1.getId(), commentData1);

    ArticleFavorite favorite = new ArticleFavorite(article1.getId(), user2.getId());
    seeds.put("favorite:" + article1.getId() + ":" + user2.getId(), favorite);
  }

  private void seedDefaultTags() {
    seeds.put("tags:all", Arrays.asList("dragons", "training", "cats"));
  }

  public Object getSeed(String key) {
    return seeds.get(key);
  }

  public void putSeed(String key, Object value) {
    seeds.put(key, value);
  }
}
