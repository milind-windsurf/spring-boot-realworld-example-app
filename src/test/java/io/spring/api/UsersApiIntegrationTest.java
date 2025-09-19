package io.spring.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class UsersApiIntegrationTest extends BaseIntegrationTest {

  /**
   * Tests successful user registration with valid data.
   * Verifies that a new user is created in the database and returns proper response with JWT token.
   */
  @Test
  public void should_create_user_success() throws Exception {
    String email = "newuser@example.com";
    String username = "newuser";
    String password = "password123";

    Map<String, Object> requestBody = createUserRegistrationRequest(email, username, password);

    mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.user.email").value(email))
        .andExpect(jsonPath("$.user.username").value(username))
        .andExpect(jsonPath("$.user.bio").value(""))
        .andExpect(jsonPath("$.user.image").value(defaultAvatar))
        .andExpect(jsonPath("$.user.token").exists());

    Optional<io.spring.core.user.User> savedUser = userRepository.findByEmail(email);
    assertThat(savedUser).isPresent();
    assertThat(savedUser.get().getUsername()).isEqualTo(username);
  }

  /**
   * Tests validation error handling for empty username during registration.
   * Verifies that appropriate error message is returned for blank username.
   */
  @Test
  public void should_show_error_message_for_blank_username() throws Exception {
    String email = "test@example.com";
    String username = "";
    String password = "password123";

    Map<String, Object> requestBody = createUserRegistrationRequest(email, username, password);

    mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors.username[0]").value("can't be empty"));
  }

  /**
   * Tests validation error handling for invalid email format during registration.
   * Verifies that appropriate error message is returned for malformed email.
   */
  @Test
  public void should_show_error_message_for_invalid_email() throws Exception {
    String email = "invalid-email";
    String username = "testuser";
    String password = "password123";

    Map<String, Object> requestBody = createUserRegistrationRequest(email, username, password);

    mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors.email[0]").value("should be an email"));
  }

  /**
   * Tests duplicate username validation during registration.
   * Verifies that registration fails when attempting to use an existing username.
   */
  @Test
  public void should_show_error_for_duplicated_username() throws Exception {
    String email = "duplicate@example.com";
    String username = testUser.getUsername();
    String password = "password123";

    Map<String, Object> requestBody = createUserRegistrationRequest(email, username, password);

    mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors.username[0]").value("duplicated username"));
  }

  /**
   * Tests duplicate email validation during registration.
   * Verifies that registration fails when attempting to use an existing email address.
   */
  @Test
  public void should_show_error_for_duplicated_email() throws Exception {
    String email = testUser.getEmail();
    String username = "uniqueusername";
    String password = "password123";

    Map<String, Object> requestBody = createUserRegistrationRequest(email, username, password);

    mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors.email[0]").value("duplicated email"));
  }

  /**
   * Tests successful user login with valid credentials.
   * Verifies that login returns user data and JWT token for valid email/password combination.
   */
  @Test
  public void should_login_success() throws Exception {
    String email = "login@example.com";
    String username = "loginuser";
    String password = "password123";
    
    createUser(email, username, password);

    Map<String, Object> loginRequest = createUserLoginRequest(email, password);

    mockMvc.perform(post("/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.email").value(email))
        .andExpect(jsonPath("$.user.username").value(username))
        .andExpect(jsonPath("$.user.token").exists());
  }

  /**
   * Tests login failure with incorrect password.
   * Verifies that login fails and returns appropriate error message for wrong password.
   */
  @Test
  public void should_fail_login_with_wrong_password() throws Exception {
    String email = "wrongpass@example.com";
    String username = "wrongpassuser";
    String password = "password123";
    
    createUser(email, username, password);

    Map<String, Object> loginRequest = createUserLoginRequest(email, "wrongpassword");

    mockMvc.perform(post("/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("invalid email or password"));
  }

  /**
   * Tests login failure with non-existent email address.
   * Verifies that login fails and returns appropriate error message for unknown email.
   */
  @Test
  public void should_fail_login_with_nonexistent_email() throws Exception {
    String email = "nonexistent@example.com";
    String password = "password123";

    Map<String, Object> loginRequest = createUserLoginRequest(email, password);

    mockMvc.perform(post("/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.message").value("invalid email or password"));
  }

  private Map<String, Object> createUserRegistrationRequest(String email, String username, String password) {
    Map<String, Object> user = new HashMap<>();
    user.put("email", email);
    user.put("username", username);
    user.put("password", password);

    Map<String, Object> request = new HashMap<>();
    request.put("user", user);
    return request;
  }

  private Map<String, Object> createUserLoginRequest(String email, String password) {
    Map<String, Object> user = new HashMap<>();
    user.put("email", email);
    user.put("password", password);

    Map<String, Object> request = new HashMap<>();
    request.put("user", user);
    return request;
  }
}
