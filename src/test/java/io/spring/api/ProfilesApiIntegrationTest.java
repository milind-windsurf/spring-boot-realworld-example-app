package io.spring.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class ProfilesApiIntegrationTest extends BaseIntegrationTest {

  private User anotherUser;

  @BeforeEach
  public void setUp() {
    anotherUser = createUser("another@example.com", "anotheruser", "password123");
  }

  /**
   * Tests retrieving a user profile without authentication.
   * Verifies that public profile information is returned with following status as false.
   */
  @Test
  public void should_get_user_profile_success() throws Exception {
    mockMvc.perform(get("/profiles/" + anotherUser.getUsername()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.profile.username").value(anotherUser.getUsername()))
        .andExpect(jsonPath("$.profile.bio").value(anotherUser.getBio()))
        .andExpect(jsonPath("$.profile.image").value(anotherUser.getImage()))
        .andExpect(jsonPath("$.profile.following").value(false));
  }

  /**
   * Tests retrieving a user profile with authentication showing follow status.
   * Verifies that following status is correctly displayed when user is authenticated.
   */
  @Test
  public void should_get_user_profile_with_following_status_when_authenticated() throws Exception {
    FollowRelation followRelation = new FollowRelation(testUser.getId(), anotherUser.getId());
    userRepository.saveRelation(followRelation);

    mockMvc.perform(get("/profiles/" + anotherUser.getUsername())
        .header("Authorization", "Token " + testUserToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.profile.username").value(anotherUser.getUsername()))
        .andExpect(jsonPath("$.profile.following").value(true));
  }

  /**
   * Tests successful user following functionality.
   * Verifies that follow relationship is created in database and profile shows following status.
   */
  @Test
  public void should_follow_user_success() throws Exception {
    mockMvc.perform(post("/profiles/" + anotherUser.getUsername() + "/follow")
        .header("Authorization", "Token " + testUserToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.profile.username").value(anotherUser.getUsername()))
        .andExpect(jsonPath("$.profile.following").value(true));

    Optional<FollowRelation> followRelation = userRepository.findRelation(testUser.getId(), anotherUser.getId());
    assertThat(followRelation).isPresent();
  }

  /**
   * Tests successful user unfollowing functionality.
   * Verifies that follow relationship is removed from database and profile shows not following.
   */
  @Test
  public void should_unfollow_user_success() throws Exception {
    FollowRelation followRelation = new FollowRelation(testUser.getId(), anotherUser.getId());
    userRepository.saveRelation(followRelation);

    mockMvc.perform(delete("/profiles/" + anotherUser.getUsername() + "/follow")
        .header("Authorization", "Token " + testUserToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.profile.username").value(anotherUser.getUsername()))
        .andExpect(jsonPath("$.profile.following").value(false));

    Optional<FollowRelation> removedRelation = userRepository.findRelation(testUser.getId(), anotherUser.getId());
    assertThat(removedRelation).isEmpty();
  }

  /**
   * Tests error handling for non-existent user profiles.
   * Verifies that 404 status is returned when requesting profile of non-existent user.
   */
  @Test
  public void should_return_404_for_nonexistent_user() throws Exception {
    mockMvc.perform(get("/profiles/nonexistentuser"))
        .andExpect(status().isNotFound());
  }

  /**
   * Tests that following a user requires authentication.
   * Verifies that unauthenticated requests to follow endpoint return 401 status.
   */
  @Test
  public void should_require_authentication_for_follow() throws Exception {
    mockMvc.perform(post("/profiles/" + anotherUser.getUsername() + "/follow"))
        .andExpect(status().isUnauthorized());
  }

  /**
   * Tests that unfollowing a user requires authentication.
   * Verifies that unauthenticated requests to unfollow endpoint return 401 status.
   */
  @Test
  public void should_require_authentication_for_unfollow() throws Exception {
    mockMvc.perform(delete("/profiles/" + anotherUser.getUsername() + "/follow"))
        .andExpect(status().isUnauthorized());
  }

  /**
   * Tests idempotent behavior of follow operation.
   * Verifies that following an already followed user doesn't cause errors.
   */
  @Test
  public void should_handle_follow_idempotency() throws Exception {
    FollowRelation existingRelation = new FollowRelation(testUser.getId(), anotherUser.getId());
    userRepository.saveRelation(existingRelation);

    mockMvc.perform(post("/profiles/" + anotherUser.getUsername() + "/follow")
        .header("Authorization", "Token " + testUserToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.profile.following").value(true));
  }

  /**
   * Tests unfollow operation when not currently following the user.
   * Verifies that unfollowing a non-followed user returns 404 as expected by the API.
   */
  @Test
  public void should_handle_unfollow_when_not_following() throws Exception {
    mockMvc.perform(delete("/profiles/" + anotherUser.getUsername() + "/follow")
        .header("Authorization", "Token " + testUserToken))
        .andExpect(status().isNotFound());
  }
}
