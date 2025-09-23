package io.spring.infrastructure.repository;

import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.service.SeedingService;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hermetic")
public class HermeticUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();
    private final Map<String, FollowRelation> followRelations = new ConcurrentHashMap<>();
    private final SeedingService seedingService;

    public HermeticUserRepository(SeedingService seedingService) {
        this.seedingService = seedingService;
    }

    @Override
    public void save(User user) {
        users.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);
        usersByUsername.put(user.getUsername(), user);
        seedingService.seedUserData(user.getId(), user);
    }

    @Override
    public Optional<User> findById(String id) {
        return seedingService.getSeededUser(id)
            .or(() -> Optional.ofNullable(users.get(id)));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return seedingService.getSeededUser(username)
            .or(() -> Optional.ofNullable(usersByUsername.get(username)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    @Override
    public void saveRelation(FollowRelation followRelation) {
        String key = followRelation.getUserId() + ":" + followRelation.getTargetId();
        followRelations.put(key, followRelation);
        seedingService.seedFollowRelationData(key, followRelation);
    }

    @Override
    public Optional<FollowRelation> findRelation(String userId, String targetId) {
        String key = userId + ":" + targetId;
        return seedingService.getSeededFollowRelation(key)
            .or(() -> Optional.ofNullable(followRelations.get(key)));
    }

    @Override
    public void removeRelation(FollowRelation followRelation) {
        String key = followRelation.getUserId() + ":" + followRelation.getTargetId();
        followRelations.remove(key);
    }
}
