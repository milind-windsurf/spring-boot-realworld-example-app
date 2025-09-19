package io.spring.infrastructure.readservice;

import io.spring.application.CursorPageParameter;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.comment.Comment;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.CommentReadService;
import io.spring.infrastructure.service.SeedingService;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hermetic")
public class HermeticCommentReadService implements CommentReadService {
  private final SeedingService seedingService;

  @Autowired
  public HermeticCommentReadService(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public CommentData findById(String id) {
    Comment comment = (Comment) seedingService.getSeed("comment:entity:" + id);
    if (comment == null) {
      return null;
    }
    User author = (User) seedingService.getSeed("user:entity:" + comment.getUserId());
    ProfileData profileData = author != null ? 
        new ProfileData(author.getId(), author.getUsername(), author.getBio(), author.getImage(), false) : null;
    
    return new CommentData(comment.getId(), comment.getBody(), comment.getArticleId(), comment.getCreatedAt(), comment.getCreatedAt(), profileData);
  }

  @Override
  public List<CommentData> findByArticleId(String articleId) {
    List<CommentData> comments = new ArrayList<>();
    @SuppressWarnings("unchecked")
    List<String> commentIds = (List<String>) seedingService.getSeed("comment:article:" + articleId);
    if (commentIds != null) {
      for (String commentId : commentIds) {
        CommentData commentData = findById(commentId);
        if (commentData != null) {
          comments.add(commentData);
        }
      }
    }
    return comments;
  }

  @Override
  public List<CommentData> findByArticleIdWithCursor(String articleId, CursorPageParameter<DateTime> page) {
    return findByArticleId(articleId);
  }
}
