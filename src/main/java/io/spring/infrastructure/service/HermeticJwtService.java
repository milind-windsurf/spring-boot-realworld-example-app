package io.spring.infrastructure.service;

import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hermetic")
public class HermeticJwtService implements JwtService {
  private final SeedingService seedingService;

  @Autowired
  public HermeticJwtService(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public String toToken(User user) {
    String token = seedingService.getSeededToken(user.getId())
        .orElse("hermetic-token-" + user.getId());
    seedingService.seedToken(token, user.getId());
    return token;
  }

  @Override
  public Optional<String> getSubFromToken(String token) {
    return seedingService.getUserFromToken(token);
  }
}
