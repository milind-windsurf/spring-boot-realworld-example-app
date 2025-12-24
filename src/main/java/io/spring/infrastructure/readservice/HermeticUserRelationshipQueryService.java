package io.spring.infrastructure.readservice;

import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import io.spring.infrastructure.service.SeedingService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hermetic")
public class HermeticUserRelationshipQueryService implements UserRelationshipQueryService {
  private final SeedingService seedingService;

  @Autowired
  public HermeticUserRelationshipQueryService(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public boolean isUserFollowing(String userId, String anotherUserId) {
    String key = "follow:" + userId + ":" + anotherUserId;
    return seedingService.getSeed(key) != null;
  }

  @Override
  public Set<String> followingAuthors(String userId, List<String> ids) {
    return ids.stream()
        .filter(id -> isUserFollowing(userId, id))
        .collect(Collectors.toSet());
  }

  @Override
  public List<String> followedUsers(String userId) {
    if ("john".equals(getUsernameById(userId))) {
      String janeId = getJaneUserId();
      return janeId != null ? List.of(janeId) : List.of();
    }
    return List.of();
  }

  private String getUsernameById(String userId) {
    Object user = seedingService.getSeed("user:entity:" + userId);
    return user != null ? ((io.spring.core.user.User) user).getUsername() : null;
  }

  private String getJaneUserId() {
    Object user = seedingService.getSeed("user:username:jane");
    return user != null ? ((io.spring.core.user.User) user).getId() : null;
  }
}
