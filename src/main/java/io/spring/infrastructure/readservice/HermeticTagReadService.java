package io.spring.infrastructure.readservice;

import io.spring.infrastructure.mybatis.readservice.TagReadService;
import io.spring.infrastructure.service.SeedingService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hermetic")
public class HermeticTagReadService implements TagReadService {
  private final SeedingService seedingService;

  @Autowired
  public HermeticTagReadService(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String> all() {
    return (List<String>) seedingService.getSeed("tags:all");
  }
}
