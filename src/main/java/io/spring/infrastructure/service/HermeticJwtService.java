package io.spring.infrastructure.service;

import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;

@Profile("hermetic")
public class HermeticJwtService implements JwtService {
  private final ConcurrentHashMap<String, String> tokenToUserMap = new ConcurrentHashMap<>();
  private final SeedingService seedingService;

  public HermeticJwtService(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public String toToken(User user) {
    String token =
        seedingService.getSeededToken(user.getId()).orElse("hermetic-token-" + user.getId());
    tokenToUserMap.put(token, user.getId());
    return token;
  }

  @Override
  public Optional<String> getSubFromToken(String token) {
    return Optional.ofNullable(tokenToUserMap.get(token));
  }
}
