package io.spring.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.core.article.Article;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class ArticlesApiIntegrationTest extends BaseIntegrationTest {

  /**
   * Tests successful article creation with valid data.
   * Verifies that article is saved to database and returns proper response with author information.
   */
  @Test
  public void should_create_article_success() throws Exception {
    String title = "How to train your dragon";
    String description = "Ever wonder how?";
    String body = "You have to believe";
    List<String> tagList = Arrays.asList("reactjs", "angularjs", "dragons");

    Map<String, Object> requestBody = createArticleRequest(title, description, body, tagList);

    mockMvc.perform(post("/articles")
        .header("Authorization", "Token " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.article.title").value(title))
        .andExpect(jsonPath("$.article.description").value(description))
        .andExpect(jsonPath("$.article.body").value(body))
        .andExpect(jsonPath("$.article.favorited").value(false))
        .andExpect(jsonPath("$.article.favoritesCount").value(0))
        .andExpect(jsonPath("$.article.author.username").value(testUser.getUsername()));

    String slug = Article.toSlug(title);
    Optional<Article> savedArticle = articleRepository.findBySlug(slug);
    assertThat(savedArticle).isPresent();
    assertThat(savedArticle.get().getTitle()).isEqualTo(title);
  }

  /**
   * Tests validation error handling for empty article body.
   * Verifies that appropriate error message is returned when article body is empty.
   */
  @Test
  public void should_get_error_message_with_empty_body() throws Exception {
    String title = "Test Article";
    String description = "Test Description";
    String body = "";
    List<String> tagList = Arrays.asList("test");

    Map<String, Object> requestBody = createArticleRequest(title, description, body, tagList);

    mockMvc.perform(post("/articles")
        .header("Authorization", "Token " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors.body[0]").value("can't be empty"));
  }

  /**
   * Tests validation error handling for empty article title.
   * Verifies that appropriate error message is returned when article title is empty.
   */
  @Test
  public void should_get_error_message_with_empty_title() throws Exception {
    String title = "";
    String description = "Test Description";
    String body = "Test Body";
    List<String> tagList = Arrays.asList("test");

    Map<String, Object> requestBody = createArticleRequest(title, description, body, tagList);

    mockMvc.perform(post("/articles")
        .header("Authorization", "Token " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors.title[0]").value("can't be empty"));
  }

  /**
   * Tests duplicate title validation during article creation.
   * Verifies that article creation fails when attempting to use an existing title.
   */
  @Test
  public void should_get_error_message_with_duplicated_title() throws Exception {
    String title = "Duplicate Title Article";
    String description = "First Description";
    String body = "First Body";
    List<String> tagList = Arrays.asList("test");

    Article existingArticle = new Article(title, description, body, tagList, testUser.getId());
    articleRepository.save(existingArticle);

    Map<String, Object> requestBody = createArticleRequest(title, "Second Description", "Second Body", tagList);

    mockMvc.perform(post("/articles")
        .header("Authorization", "Token " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isUnprocessableEntity());
  }

  /**
   * Tests retrieving an article by its slug.
   * Verifies that article can be fetched using its generated slug identifier.
   */
  @Test
  public void should_get_article_by_slug() throws Exception {
    String title = "Get Article Test";
    String description = "Test Description";
    String body = "Test Body";
    List<String> tagList = Arrays.asList("test", "get");

    Article article = new Article(title, description, body, tagList, testUser.getId());
    articleRepository.save(article);

    mockMvc.perform(get("/articles/" + article.getSlug())
        .header("Authorization", "Token " + testUserToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.article.title").value(title))
        .andExpect(jsonPath("$.article.slug").value(article.getSlug()));
  }

  /**
   * Tests successful article update by the author.
   * Verifies that article title and content can be updated by the original author.
   */
  @Test
  public void should_update_article_success() throws Exception {
    String originalTitle = "Original Title";
    String updatedTitle = "Updated Title";
    String description = "Test Description";
    String body = "Test Body";
    List<String> tagList = Arrays.asList("test");

    Article article = new Article(originalTitle, description, body, tagList, testUser.getId());
    articleRepository.save(article);

    Map<String, Object> updateRequest = createArticleRequest(updatedTitle, description, body, tagList);

    mockMvc.perform(put("/articles/" + article.getSlug())
        .header("Authorization", "Token " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.article.title").value(updatedTitle));
  }

  /**
   * Tests successful article deletion by the author.
   * Verifies that article is removed from database when deleted by the original author.
   */
  @Test
  public void should_delete_article_success() throws Exception {
    String title = "Article to Delete";
    String description = "Test Description";
    String body = "Test Body";
    List<String> tagList = Arrays.asList("test");

    Article article = new Article(title, description, body, tagList, testUser.getId());
    articleRepository.save(article);

    mockMvc.perform(delete("/articles/" + article.getSlug())
        .header("Authorization", "Token " + testUserToken))
        .andExpect(status().isNoContent());

    Optional<Article> deletedArticle = articleRepository.findBySlug(article.getSlug());
    assertThat(deletedArticle).isEmpty();
  }

  private Map<String, Object> createArticleRequest(String title, String description, String body, List<String> tagList) {
    Map<String, Object> article = new HashMap<>();
    article.put("title", title);
    article.put("description", description);
    article.put("body", body);
    article.put("tagList", tagList);

    Map<String, Object> request = new HashMap<>();
    request.put("article", article);
    return request;
  }
}
