# Hermetic Server Setup

The hermetic server is a self-contained version of the Spring Boot RealWorld application that makes no external network calls. It uses in-memory storage instead of database connections and provides a seeding service for test data management.

## Running the Hermetic Server

### Using Gradle Task
```bash
./gradlew runHermetic
```

### Using Java Command
```bash
java -Dspring.profiles.active=hermetic -jar build/libs/spring-boot-realworld-example-app-1.0-SNAPSHOT.jar
```

### Using Spring Boot Run
```bash
./gradlew bootRun -Dspring.profiles.active=hermetic
```

## Features

- **No External Dependencies**: All database calls are replaced with in-memory storage
- **JWT Authentication**: Uses hermetic JWT service with predictable tokens
- **Seeded Data**: Pre-populated with sample users and articles
- **Full API Compatibility**: All production endpoints work identically
- **Thread-Safe**: Uses ConcurrentHashMap for concurrent access

## Seeding Data

The hermetic server comes with pre-seeded data:

### Default Users
- **testuser**: test@example.com (password: password123)
- **author**: author@example.com (password: password123)

### Default Articles
- "How to build webapps that scale"
- "The song you won't ever stop singing. No matter how hard you try."

### Custom Seeding

Use the SeedingService to configure additional responses:

```java
@Autowired
private SeedingService seedingService;

// Seed user data
User customUser = new User("custom@example.com", "customuser", "password", "Custom bio", "image.jpg");
seedingService.seedUserData(customUser.getId(), customUser);

// Seed article data
Article customArticle = new Article("Custom Title", "Description", "Body", Arrays.asList("tag1"), "userId");
seedingService.seedArticleData(customArticle.getSlug(), customArticle);

// Seed JWT tokens
seedingService.seedToken("user123", "custom-token-value");
```

## Testing

The hermetic server supports all production endpoints with seeded data:

### Basic Endpoints
```bash
# Get tags
curl http://localhost:8080/tags

# Get articles
curl http://localhost:8080/articles

# Get specific article
curl http://localhost:8080/articles/how-to-build-webapps-that-scale
```

### Authentication
```bash
# Register new user
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"user":{"username":"newuser","email":"new@example.com","password":"password123"}}'

# Login
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"user":{"email":"test@example.com","password":"password123"}}'
```

### Authenticated Endpoints
```bash
# Get current user (requires Authorization header)
curl http://localhost:8080/user \
  -H "Authorization: Token hermetic-token-{user-id}"

# Create article (requires Authorization header)
curl -X POST http://localhost:8080/articles \
  -H "Content-Type: application/json" \
  -H "Authorization: Token hermetic-token-{user-id}" \
  -d '{"article":{"title":"New Article","description":"Description","body":"Body","tagList":["test"]}}'
```

## Configuration

The hermetic server uses `application-hermetic.yml` configuration:

- **Database**: H2 in-memory database
- **JWT Secret**: hermetic-secret-key-for-testing-purposes-only
- **Session Time**: 86400 seconds (24 hours)
- **Logging**: DEBUG level for io.spring packages

## Architecture

The hermetic implementation follows the same DDD architecture as production:

- **HermeticConfiguration**: Spring configuration with @Profile("hermetic")
- **SeedingService**: Centralized test data management
- **HermeticJwtService**: In-memory JWT token handling
- **Hermetic Repositories**: In-memory implementations of all repository interfaces
  - HermeticUserRepository
  - HermeticArticleRepository
  - HermeticCommentRepository
  - HermeticArticleFavoriteRepository

## Troubleshooting

### Server Won't Start
- Ensure no other application is running on port 8080
- Check that the hermetic profile is active: `-Dspring.profiles.active=hermetic`
- Verify all hermetic classes are in the classpath

### Authentication Issues
- Use the correct token format: `hermetic-token-{user-id}`
- Ensure the user exists in seeded data or has been created via API
- Check that JWT secret matches configuration

### Missing Data
- Verify seeded data is loaded on application startup
- Use SeedingService to add custom test data
- Check application logs for seeding errors

## Development

When modifying the hermetic server:

1. **Add New Repositories**: Implement the repository interface with in-memory storage
2. **Update Configuration**: Add new @Primary beans to HermeticConfiguration
3. **Extend Seeding**: Add new seeding methods to SeedingService
4. **Test Changes**: Run `./gradlew runHermetic` to verify functionality

The hermetic server is designed to be a drop-in replacement for the production server during testing and development scenarios where external dependencies should be avoided.
