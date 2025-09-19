package io.spring.infrastructure.repository;

import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.infrastructure.service.SeedingService;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;

@Profile("hermetic")
public class HermeticCommentRepository implements CommentRepository {
  private final ConcurrentHashMap<String, Comment> comments = new ConcurrentHashMap<>();
  private final SeedingService seedingService;

  public HermeticCommentRepository(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public void save(Comment comment) {
    comments.put(comment.getId(), comment);
    seedingService.seedComment(comment);
  }

  @Override
  public Optional<Comment> findById(String articleId, String id) {
    return seedingService
        .getSeededComment(id)
        .or(
            () ->
                Optional.ofNullable(comments.get(id))
                    .filter(comment -> articleId.equals(comment.getArticleId())));
  }

  @Override
  public void remove(Comment comment) {
    comments.remove(comment.getId());
  }
}
