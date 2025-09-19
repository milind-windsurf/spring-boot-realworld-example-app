package io.spring.infrastructure.repository;

import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.service.SeedingService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("hermetic")
public class HermeticUserRepository implements UserRepository {
  private final SeedingService seedingService;

  @Autowired
  public HermeticUserRepository(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public void save(User user) {
    seedingService.putSeed("user:entity:" + user.getId(), user);
    seedingService.putSeed("user:username:" + user.getUsername(), user);
    seedingService.putSeed("user:email:" + user.getEmail(), user);
  }

  @Override
  public Optional<User> findById(String id) {
    return Optional.ofNullable((User) seedingService.getSeed("user:entity:" + id));
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return Optional.ofNullable((User) seedingService.getSeed("user:username:" + username));
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return Optional.ofNullable((User) seedingService.getSeed("user:email:" + email));
  }

  @Override
  public void saveRelation(FollowRelation followRelation) {
    String key = "follow:" + followRelation.getUserId() + ":" + followRelation.getTargetId();
    if (seedingService.getSeed(key) == null) {
      seedingService.putSeed(key, followRelation);
    }
  }

  @Override
  public Optional<FollowRelation> findRelation(String userId, String targetId) {
    String key = "follow:" + userId + ":" + targetId;
    return Optional.ofNullable((FollowRelation) seedingService.getSeed(key));
  }

  @Override
  public void removeRelation(FollowRelation followRelation) {
    String key = "follow:" + followRelation.getUserId() + ":" + followRelation.getTargetId();
    seedingService.putSeed(key, null);
  }
}
