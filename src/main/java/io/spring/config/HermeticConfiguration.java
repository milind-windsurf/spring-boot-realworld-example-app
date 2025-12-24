package io.spring.config;

import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.CommentRepository;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.core.service.JwtService;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.repository.HermeticArticleFavoriteRepository;
import io.spring.infrastructure.repository.HermeticArticleRepository;
import io.spring.infrastructure.repository.HermeticCommentRepository;
import io.spring.infrastructure.repository.HermeticUserRepository;
import io.spring.infrastructure.service.HermeticJwtService;
import io.spring.infrastructure.service.SeedingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("hermetic")
public class HermeticConfiguration {

    @Bean
    public SeedingService seedingService() {
        return new SeedingService();
    }

    @Bean
    @Primary
    public JwtService hermeticJwtService(SeedingService seedingService) {
        return new HermeticJwtService(seedingService);
    }

    @Bean
    @Primary
    public UserRepository hermeticUserRepository(SeedingService seedingService) {
        return new HermeticUserRepository(seedingService);
    }

    @Bean
    @Primary
    public ArticleRepository hermeticArticleRepository(SeedingService seedingService) {
        return new HermeticArticleRepository(seedingService);
    }

    @Bean
    @Primary
    public CommentRepository hermeticCommentRepository(SeedingService seedingService) {
        return new HermeticCommentRepository(seedingService);
    }

    @Bean
    @Primary
    public ArticleFavoriteRepository hermeticArticleFavoriteRepository(SeedingService seedingService) {
        return new HermeticArticleFavoriteRepository(seedingService);
    }
}
