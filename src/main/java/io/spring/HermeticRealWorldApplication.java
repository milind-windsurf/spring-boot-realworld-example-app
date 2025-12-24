package io.spring;

import io.spring.core.article.Article;
import io.spring.core.user.User;
import io.spring.infrastructure.service.SeedingService;
import java.util.Arrays;
import org.joda.time.DateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

@SpringBootApplication
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
        User defaultUser = new User(
            "test@example.com",
            "testuser",
            "password123",
            "Test user bio",
            "https://static.productionready.io/images/smiley-cyrus.jpg"
        );
        seedingService.seedUserData(defaultUser.getId(), defaultUser);
        seedingService.seedUserData("testuser", defaultUser);

        User authorUser = new User(
            "author@example.com",
            "author",
            "password123",
            "Article author bio",
            "https://static.productionready.io/images/smiley-cyrus.jpg"
        );
        seedingService.seedUserData(authorUser.getId(), authorUser);
        seedingService.seedUserData("author", authorUser);
    }

    private void seedDefaultArticles(SeedingService seedingService) {
        Article defaultArticle = new Article(
            "How to build webapps that scale",
            "Web development technologies have evolved at an incredible clip over the past few years.",
            "# Introduction\n\nThis is a sample article body with markdown content.\n\n## Section 1\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit.",
            Arrays.asList("web", "development", "scaling"),
            "author-user-id",
            new DateTime()
        );
        seedingService.seedArticleData(defaultArticle.getSlug(), defaultArticle);

        Article secondArticle = new Article(
            "The song you won't ever stop singing. No matter how hard you try.",
            "Ever wonder which song was the most annoying song of all time? Look no further.",
            "# The Most Annoying Song\n\nThis article explores the psychology behind catchy tunes.\n\n## Why Songs Get Stuck\n\nScientific research shows...",
            Arrays.asList("music", "psychology", "entertainment"),
            "author-user-id",
            new DateTime()
        );
        seedingService.seedArticleData(secondArticle.getSlug(), secondArticle);
    }
}
