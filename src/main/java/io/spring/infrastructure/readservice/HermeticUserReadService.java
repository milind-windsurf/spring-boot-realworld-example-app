package io.spring.infrastructure.readservice;

import io.spring.application.data.UserData;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import io.spring.infrastructure.service.SeedingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hermetic")
public class HermeticUserReadService implements UserReadService {
  private final SeedingService seedingService;

  @Autowired
  public HermeticUserReadService(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public UserData findByUsername(String username) {
    return (UserData) seedingService.getSeed("user:" + getUserIdByUsername(username));
  }

  @Override
  public UserData findById(String id) {
    return (UserData) seedingService.getSeed("user:" + id);
  }

  private String getUserIdByUsername(String username) {
    if ("john".equals(username)) {
      return getJohnUserId();
    } else if ("jane".equals(username)) {
      return getJaneUserId();
    }
    return null;
  }

  private String getJohnUserId() {
    return seedingService.getSeed("user:username:john") != null ? 
        ((io.spring.core.user.User) seedingService.getSeed("user:username:john")).getId() : null;
  }

  private String getJaneUserId() {
    return seedingService.getSeed("user:username:jane") != null ? 
        ((io.spring.core.user.User) seedingService.getSeed("user:username:jane")).getId() : null;
  }
}
