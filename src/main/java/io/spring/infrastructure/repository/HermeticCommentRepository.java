package io.spring.infrastructure.repository;

import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.infrastructure.service.SeedingService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("hermetic")
public class HermeticCommentRepository implements CommentRepository {
  private final SeedingService seedingService;

  @Autowired
  public HermeticCommentRepository(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public void save(Comment comment) {
    seedingService.putSeed("comment:entity:" + comment.getId(), comment);
    seedingService.putSeed("comment:article:" + comment.getArticleId() + ":" + comment.getId(), comment);
  }

  @Override
  public Optional<Comment> findById(String articleId, String id) {
    return Optional.ofNullable((Comment) seedingService.getSeed("comment:entity:" + id));
  }

  @Override
  public void remove(Comment comment) {
    seedingService.putSeed("comment:entity:" + comment.getId(), null);
    seedingService.putSeed("comment:article:" + comment.getArticleId() + ":" + comment.getId(), null);
  }
}
