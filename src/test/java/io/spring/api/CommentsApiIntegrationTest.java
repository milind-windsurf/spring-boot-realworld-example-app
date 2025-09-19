package io.spring.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.application.data.CommentData;
import io.spring.core.article.Article;
import io.spring.core.comment.Comment;
import io.spring.core.user.User;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class CommentsApiIntegrationTest extends BaseIntegrationTest {

  private Article testArticle;

  @BeforeEach
  public void setUp() {
    testArticle = new Article(
        "Test Article", 
        "Test Description", 
        "Test Body", 
        Arrays.asList("test"), 
        testUser.getId()
    );
    articleRepository.save(testArticle);
  }

  /**
   * Tests successful comment creation on an article.
   * Verifies that comment is saved to database and returns proper response with author information.
   */
  @Test
  public void should_create_comment_success() throws Exception {
    String commentBody = "This is a test comment";
    Map<String, Object> requestBody = createCommentRequest(commentBody);

    mockMvc.perform(post("/articles/" + testArticle.getSlug() + "/comments")
        .header("Authorization", "Token " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.comment.body").value(commentBody))
        .andExpect(jsonPath("$.comment.author.username").value(testUser.getUsername()));

    List<io.spring.application.data.CommentData> savedComments = commentQueryService.findByArticleId(testArticle.getId(), testUser);
    assertThat(savedComments).hasSize(1);
    assertThat(savedComments.get(0).getBody()).isEqualTo(commentBody);
  }

  /**
   * Tests validation error handling for empty comment body.
   * Verifies that appropriate error message is returned when comment body is empty.
   */
  @Test
  public void should_get_422_with_empty_body() throws Exception {
    String commentBody = "";
    Map<String, Object> requestBody = createCommentRequest(commentBody);

    mockMvc.perform(post("/articles/" + testArticle.getSlug() + "/comments")
        .header("Authorization", "Token " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors.body[0]").value("can't be empty"));
  }

  /**
   * Tests retrieving all comments for a specific article.
   * Verifies that all comments associated with an article are returned in the response.
   */
  @Test
  public void should_get_comments_of_article_success() throws Exception {
    Comment comment1 = new Comment("First comment", testUser.getId(), testArticle.getId());
    Comment comment2 = new Comment("Second comment", testUser.getId(), testArticle.getId());
    commentRepository.save(comment1);
    commentRepository.save(comment2);

    mockMvc.perform(get("/articles/" + testArticle.getSlug() + "/comments"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.comments").isArray())
        .andExpect(jsonPath("$.comments.length()").value(2));
  }

  /**
   * Tests successful comment deletion by the comment author.
   * Verifies that comment is removed from database when deleted by the original author.
   */
  @Test
  public void should_delete_comment_success() throws Exception {
    Comment comment = new Comment("Comment to delete", testUser.getId(), testArticle.getId());
    commentRepository.save(comment);

    mockMvc.perform(delete("/articles/" + testArticle.getSlug() + "/comments/" + comment.getId())
        .header("Authorization", "Token " + testUserToken))
        .andExpect(status().isNoContent());

    Optional<Comment> deletedComment = commentRepository.findById(testArticle.getId(), comment.getId());
    assertThat(deletedComment).isEmpty();
  }

  /**
   * Tests authorization for comment deletion by non-author.
   * Verifies that users cannot delete comments they did not create.
   */
  @Test
  public void should_get_403_if_not_author_when_delete_comment() throws Exception {
    User anotherUser = createUser("another@example.com", "anotheruser", "password123");
    String anotherUserToken = jwtService.toToken(anotherUser);
    
    Comment comment = new Comment("Comment by test user", testUser.getId(), testArticle.getId());
    commentRepository.save(comment);

    mockMvc.perform(delete("/articles/" + testArticle.getSlug() + "/comments/" + comment.getId())
        .header("Authorization", "Token " + anotherUserToken))
        .andExpect(status().isForbidden());

    Optional<Comment> stillExistingComment = commentRepository.findById(testArticle.getId(), comment.getId());
    assertThat(stillExistingComment).isPresent();
  }

  /**
   * Tests that article authors can delete any comment on their articles.
   * Verifies that article authors have permission to moderate comments on their content.
   */
  @Test
  public void should_allow_article_author_to_delete_any_comment() throws Exception {
    User commentAuthor = createUser("commenter@example.com", "commenter", "password123");
    Comment comment = new Comment("Comment by another user", commentAuthor.getId(), testArticle.getId());
    commentRepository.save(comment);

    mockMvc.perform(delete("/articles/" + testArticle.getSlug() + "/comments/" + comment.getId())
        .header("Authorization", "Token " + testUserToken))
        .andExpect(status().isNoContent());

    Optional<Comment> deletedComment = commentRepository.findById(testArticle.getId(), comment.getId());
    assertThat(deletedComment).isEmpty();
  }

  private Map<String, Object> createCommentRequest(String body) {
    Map<String, Object> comment = new HashMap<>();
    comment.put("body", body);

    Map<String, Object> request = new HashMap<>();
    request.put("comment", comment);
    return request;
  }
}
