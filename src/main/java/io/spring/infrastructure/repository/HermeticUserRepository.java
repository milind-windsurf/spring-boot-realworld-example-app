package io.spring.infrastructure.repository;

import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.service.SeedingService;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;

@Profile("hermetic")
public class HermeticUserRepository implements UserRepository {
  private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, User> usersByEmail = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, User> usersByUsername = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, FollowRelation> followRelations =
      new ConcurrentHashMap<>();
  private final SeedingService seedingService;

  public HermeticUserRepository(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public void save(User user) {
    users.put(user.getId(), user);
    usersByEmail.put(user.getEmail(), user);
    usersByUsername.put(user.getUsername(), user);
    seedingService.seedUser(user);
  }

  @Override
  public Optional<User> findById(String id) {
    return seedingService.getSeededUser(id).or(() -> Optional.ofNullable(users.get(id)));
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return Optional.ofNullable(usersByUsername.get(username))
        .or(
            () ->
                users.values().stream()
                    .filter(user -> username.equals(user.getUsername()))
                    .findFirst());
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return Optional.ofNullable(usersByEmail.get(email))
        .or(
            () ->
                users.values().stream().filter(user -> email.equals(user.getEmail())).findFirst());
  }

  @Override
  public void saveRelation(FollowRelation followRelation) {
    String key = followRelation.getUserId() + ":" + followRelation.getTargetId();
    followRelations.put(key, followRelation);
    seedingService.seedFollowRelation(followRelation);
  }

  @Override
  public Optional<FollowRelation> findRelation(String userId, String targetId) {
    return seedingService
        .getSeededFollowRelation(userId, targetId)
        .or(() -> Optional.ofNullable(followRelations.get(userId + ":" + targetId)));
  }

  @Override
  public void removeRelation(FollowRelation followRelation) {
    String key = followRelation.getUserId() + ":" + followRelation.getTargetId();
    followRelations.remove(key);
  }
}
