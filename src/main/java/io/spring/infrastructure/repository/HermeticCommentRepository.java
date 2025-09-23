package io.spring.infrastructure.repository;

import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.infrastructure.service.SeedingService;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hermetic")
public class HermeticCommentRepository implements CommentRepository {
    private final Map<String, Comment> comments = new ConcurrentHashMap<>();
    private final SeedingService seedingService;

    public HermeticCommentRepository(SeedingService seedingService) {
        this.seedingService = seedingService;
    }

    @Override
    public void save(Comment comment) {
        comments.put(comment.getId(), comment);
        seedingService.seedCommentData(comment.getId(), comment);
    }

    @Override
    public Optional<Comment> findById(String articleId, String id) {
        return seedingService.getSeededComment(id)
            .or(() -> Optional.ofNullable(comments.get(id)))
            .filter(comment -> comment.getArticleId().equals(articleId));
    }

    @Override
    public void remove(Comment comment) {
        comments.remove(comment.getId());
    }
}
