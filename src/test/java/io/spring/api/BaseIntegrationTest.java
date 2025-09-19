package io.spring.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.application.CommentQueryService;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.CommentRepository;
import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureWebMvc
public abstract class BaseIntegrationTest {

  @Autowired protected WebApplicationContext webApplicationContext;
  @Autowired protected UserRepository userRepository;
  @Autowired protected ArticleRepository articleRepository;
  @Autowired protected CommentRepository commentRepository;
  @Autowired protected CommentQueryService commentQueryService;
  @Autowired protected JwtService jwtService;
  @Autowired protected JdbcTemplate jdbcTemplate;
  @Autowired protected ObjectMapper objectMapper;
  @Autowired protected PasswordEncoder passwordEncoder;
  
  protected MockMvc mockMvc;

  protected User testUser;
  protected String testUserToken;
  protected String defaultAvatar;

  @BeforeEach
  public void baseSetUp() {
    cleanupDatabase();
    defaultAvatar = "https://static.productionready.io/images/smiley-cyrus.jpg";
    setupTestUser();
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(SecurityMockMvcConfigurers.springSecurity())
        .build();
  }

  protected void cleanupDatabase() {
    try {
      jdbcTemplate.execute("DELETE FROM comments");
      jdbcTemplate.execute("DELETE FROM article_tags");
      jdbcTemplate.execute("DELETE FROM articles");
      jdbcTemplate.execute("DELETE FROM follows");
      jdbcTemplate.execute("DELETE FROM tags");
      jdbcTemplate.execute("DELETE FROM users");
    } catch (Exception e) {
    }
  }

  protected void setupTestUser() {
    testUser = new User("test@example.com", "testuser", passwordEncoder.encode("password123"), "Test Bio", defaultAvatar);
    userRepository.save(testUser);
    testUserToken = jwtService.toToken(testUser);
  }

  protected User createUser(String email, String username, String password) {
    User user = new User(email, username, passwordEncoder.encode(password), "", defaultAvatar);
    userRepository.save(user);
    return user;
  }

  protected HttpEntity<Object> createAuthenticatedRequest(Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Token " + testUserToken);
    headers.set("Content-Type", "application/json");
    return new HttpEntity<>(body, headers);
  }

  protected HttpEntity<Object> createAuthenticatedRequest(Object body, String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Token " + token);
    headers.set("Content-Type", "application/json");
    return new HttpEntity<>(body, headers);
  }

  protected HttpEntity<Void> createAuthenticatedGetRequest() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Token " + testUserToken);
    return new HttpEntity<>(headers);
  }

  protected HttpEntity<Void> createAuthenticatedGetRequest(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Token " + token);
    return new HttpEntity<>(headers);
  }
}
