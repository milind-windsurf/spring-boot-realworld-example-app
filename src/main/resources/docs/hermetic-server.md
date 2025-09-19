# Hermetic Server Setup

## Overview

The hermetic server is a special configuration of the Spring Boot RealWorld application that behaves identically to the production service but makes no external network calls. This is essential for reliable testing scenarios where you need deterministic, isolated behavior.

## Running the Hermetic Server

### Using Gradle Task
```bash
./gradlew runHermetic
```

### Using Java Command
```bash
java -Dspring.profiles.active=hermetic -jar build/libs/spring-boot-realworld-example-app-0.0.1-SNAPSHOT.jar
```

### Using Spring Boot Plugin
```bash
./gradlew bootRun -Pargs="--spring.profiles.active=hermetic"
```

## Configuration

The hermetic server uses the `hermetic` Spring profile which:
- Replaces SQLite database with H2 in-memory database
- Uses hermetic implementations of all repositories (in-memory storage)
- Uses hermetic JWT service (no cryptographic operations)
- Automatically seeds default test data on startup

## Seeding Data

The `SeedingService` allows you to configure responses and test data:

### Seeding Users
```java
@Autowired
private SeedingService seedingService;

public void seedTestUser() {
    User user = new User("test@example.com", "testuser", "password", "Test Bio", "");
    seedingService.seedUser(user);
    seedingService.seedToken(user.getId(), "custom-token-123");
}
```

### Seeding Articles
```java
public void seedTestArticle() {
    Article article = new Article(
        "Test Article", 
        "Description",
        "Body content",
        Arrays.asList("test", "hermetic"),
        userId
    );
    seedingService.seedArticle(article);
}
```

### Seeding Comments
```java
public void seedTestComment() {
    Comment comment = new Comment("Test comment content", userId, articleId);
    seedingService.seedComment(comment);
}
```

### Seeding Favorites and Follows
```java
public void seedRelationships() {
    ArticleFavorite favorite = new ArticleFavorite(articleId, userId);
    seedingService.seedArticleFavorite(favorite);
    
    FollowRelation follow = new FollowRelation(followerId, followeeId);
    seedingService.seedFollowRelation(follow);
}
```

## Default Seeded Data

The hermetic server automatically seeds the following data on startup:

### Users
- **user1**: email=user1@example.com, username=user1
- **user2**: email=user2@example.com, username=user2
- **Follow relationship**: user1 follows user2

### Articles
- **"How to Build Hermetic Servers"**: Tagged with testing, hermetic, servers
- **"Spring Boot Best Practices"**: Tagged with spring-boot, java, best-practices

### JWT Tokens
- Deterministic tokens in format: `hermetic-token-{userId}`

## API Testing

Test the hermetic server endpoints:

```bash
# Get all tags
curl http://localhost:8080/tags

# Get articles
curl http://localhost:8080/articles

# Login with seeded user
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"user":{"email":"user1@example.com","password":"password123"}}'
```

## Key Features

### Hermetic Repositories
- **HermeticUserRepository**: In-memory user storage with ConcurrentHashMap
- **HermeticArticleRepository**: In-memory article storage with slug indexing
- **HermeticCommentRepository**: In-memory comment storage
- **HermeticArticleFavoriteRepository**: In-memory favorite relationships

### Hermetic JWT Service
- No cryptographic operations
- Deterministic token generation
- In-memory token-to-user mapping
- Compatible with existing JWT filter chain

### Database Configuration
- H2 in-memory database (`jdbc:h2:mem:hermetic`)
- Automatic schema creation on startup
- No persistent storage between runs

## Benefits

1. **Deterministic**: Same input always produces same output
2. **Fast**: No network calls or database I/O
3. **Isolated**: No external dependencies
4. **Reliable**: Perfect for automated testing
5. **Debuggable**: Full control over all data and responses

## Use Cases

- **Integration Testing**: Test complete user flows without external dependencies
- **Performance Testing**: Measure application performance without network latency
- **Development**: Develop against consistent, controlled data
- **CI/CD**: Reliable testing in build pipelines
- **Debugging**: Reproduce issues with exact data scenarios

## Troubleshooting

### Server Won't Start
- Check that H2 dependency is available
- Verify `spring.profiles.active=hermetic` is set
- Check logs for configuration errors

### Missing Data
- Verify seeding methods are called in `HermeticRealWorldApplication`
- Check `SeedingService` for proper data storage
- Ensure hermetic repositories are using seeded data

### Authentication Issues
- Verify JWT tokens are properly seeded
- Check `HermeticJwtService` token mapping
- Ensure user data is seeded before token generation
