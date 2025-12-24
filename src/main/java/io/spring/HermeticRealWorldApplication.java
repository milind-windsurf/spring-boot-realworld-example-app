package io.spring;

import io.spring.infrastructure.service.SeedingService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@Profile("hermetic")
public class HermeticRealWorldApplication {

  public static void main(String[] args) {
    System.setProperty("spring.profiles.active", "hermetic");
    SpringApplication app = new SpringApplication(RealWorldApplication.class);
    app.setAdditionalProfiles("hermetic");
    app.run(args);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void seedDefaultData(ApplicationReadyEvent event) {
    SeedingService seedingService = event.getApplicationContext().getBean(SeedingService.class);
    seedingService.seedDefaultData();
  }
}
