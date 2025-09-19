package io.spring.config;

import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.core.service.JwtService;
import io.spring.core.user.UserRepository;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.CommentRepository;
import io.spring.infrastructure.mybatis.readservice.ArticleFavoritesReadService;
import io.spring.infrastructure.mybatis.readservice.ArticleReadService;
import io.spring.infrastructure.mybatis.readservice.CommentReadService;
import io.spring.infrastructure.mybatis.readservice.TagReadService;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import io.spring.infrastructure.readservice.HermeticArticleFavoritesReadService;
import io.spring.infrastructure.readservice.HermeticArticleReadService;
import io.spring.infrastructure.readservice.HermeticCommentReadService;
import io.spring.infrastructure.readservice.HermeticTagReadService;
import io.spring.infrastructure.readservice.HermeticUserReadService;
import io.spring.infrastructure.readservice.HermeticUserRelationshipQueryService;
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
  @Primary
  public SeedingService seedingService() {
    SeedingService service = new SeedingService();
    service.seedDefaultData();
    return service;
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

  @Bean
  @Primary
  public ArticleReadService hermeticArticleReadService(SeedingService seedingService) {
    return new HermeticArticleReadService(seedingService);
  }

  @Bean
  @Primary
  public UserReadService hermeticUserReadService(SeedingService seedingService) {
    return new HermeticUserReadService(seedingService);
  }

  @Bean
  @Primary
  public CommentReadService hermeticCommentReadService(SeedingService seedingService) {
    return new HermeticCommentReadService(seedingService);
  }

  @Bean
  @Primary
  public ArticleFavoritesReadService hermeticArticleFavoritesReadService(SeedingService seedingService) {
    return new HermeticArticleFavoritesReadService(seedingService);
  }

  @Bean
  @Primary
  public UserRelationshipQueryService hermeticUserRelationshipQueryService(SeedingService seedingService) {
    return new HermeticUserRelationshipQueryService(seedingService);
  }

  @Bean
  @Primary
  public TagReadService hermeticTagReadService(SeedingService seedingService) {
    return new HermeticTagReadService(seedingService);
  }
}
