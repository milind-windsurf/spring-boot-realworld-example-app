package io.spring;

import io.spring.core.article.Article;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.infrastructure.service.SeedingService;
import java.util.Arrays;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("hermetic")
public class HermeticRealWorldApplication {

  public static void main(String[] args) {
    System.setProperty("spring.profiles.active", "hermetic");
    SpringApplication app = new SpringApplication(RealWorldApplication.class);
    app.setAdditionalProfiles("hermetic");
    app.run(args);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void seedDefaultData(ApplicationReadyEvent event) {
    SeedingService seedingService = event.getApplicationContext().getBean(SeedingService.class);
    seedDefaultUsers(seedingService);
    seedDefaultArticles(seedingService);
  }

  private void seedDefaultUsers(SeedingService seedingService) {
    User user1 = new User("user1@example.com", "user1", "password123", "User One Bio", "");
    User user2 = new User("user2@example.com", "user2", "password456", "User Two Bio", "");

    seedingService.seedUser(user1);
    seedingService.seedUser(user2);
    seedingService.seedToken(user1.getId(), "hermetic-token-" + user1.getId());
    seedingService.seedToken(user2.getId(), "hermetic-token-" + user2.getId());

    FollowRelation followRelation = new FollowRelation(user1.getId(), user2.getId());
    seedingService.seedFollowRelation(followRelation);
  }

  private void seedDefaultArticles(SeedingService seedingService) {
    User user1 = new User("user1@example.com", "user1", "password123", "User One Bio", "");
    seedingService.seedUser(user1);

    Article article1 =
        new Article(
            "How to Build Hermetic Servers",
            "A comprehensive guide to building hermetic servers for testing",
            "This article explains the principles and implementation of hermetic servers...",
            Arrays.asList("testing", "hermetic", "servers"),
            user1.getId());

    Article article2 =
        new Article(
            "Spring Boot Best Practices",
            "Essential patterns for Spring Boot applications",
            "Learn the most important patterns and practices for Spring Boot development...",
            Arrays.asList("spring-boot", "java", "best-practices"),
            user1.getId());

    seedingService.seedArticle(article1);
    seedingService.seedArticle(article2);
  }
}
