# Hermetic Server Setup

## Overview

The hermetic server is a special configuration of the Spring Boot RealWorld application that behaves identically to the production service but makes no external network calls. This is essential for reliable testing and development.

## Running the Hermetic Server

### Using Gradle Task
```bash
./gradlew runHermetic
```

### Using Java directly
```bash
java -Dspring.profiles.active=hermetic -jar build/libs/spring-boot-realworld-example-app-0.0.1-SNAPSHOT.jar
```

### Using Spring Boot Gradle Plugin
```bash
./gradlew bootRun -Dspring.profiles.active=hermetic
```

## Features

### In-Memory Storage
- Uses H2 in-memory database instead of SQLite
- All data is stored in ConcurrentHashMap structures for thread safety
- No external database connections required

### Seeded Test Data
The hermetic server automatically seeds the following test data on startup:

**Users:**
- john@example.com / username: john / password: password
- jane@example.com / username: jane / password: password

**Articles:**
- "How to train your dragon" by john
- "How to train your cat" by jane

**Tags:**
- dragons, training, cats

**Relationships:**
- john follows jane
- jane favorited john's dragon article

### JWT Authentication
- Uses fake JWT tokens with format: `hermetic-token-{userId}`
- No actual JWT signing/verification
- Tokens are mapped to users in memory

## API Testing

### Basic Endpoints
```bash
# Get all tags
curl http://localhost:8080/tags

# Get articles
curl http://localhost:8080/articles

# Login as john
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"user":{"email":"john@example.com","password":"password"}}'

# Get current user (requires authentication)
curl http://localhost:8080/user \
  -H "Authorization: Token hermetic-token-{john-user-id}"
```

### Seeding Custom Data

You can programmatically seed additional data using the SeedingService:

```java
@Autowired
private SeedingService seedingService;

// Seed a user
UserData userData = new UserData("user-id", "test@example.com", "testuser", "Test bio", "image-url");
seedingService.seedUserData("user-id", userData);

// Seed an article
ArticleData articleData = new ArticleData(/* article parameters */);
seedingService.seedArticleData("article-slug", articleData);

// Seed a JWT token
seedingService.seedToken("custom-token", "user-id");
```

## Configuration

The hermetic server uses the `application-hermetic.yml` configuration file which:
- Configures H2 in-memory database
- Sets hermetic-specific JWT secret
- Enables debug logging
- Disables MyBatis caching for testing

## Differences from Production

1. **Database**: H2 in-memory instead of SQLite file
2. **JWT**: Fake tokens instead of real JWT signing
3. **Data**: Seeded test data instead of empty database
4. **Network**: No external calls whatsoever
5. **Persistence**: Data is lost on restart (by design)

## Use Cases

- **Integration Testing**: Test full application flows without external dependencies
- **Development**: Develop frontend applications against consistent backend data
- **CI/CD**: Run tests in isolated environments
- **Demos**: Showcase application functionality with predictable data

## Troubleshooting

### Server Won't Start
- Ensure H2 dependency is available
- Check that hermetic profile is active
- Verify no port conflicts on 8080

### Authentication Issues
- Use the seeded user credentials (john@example.com / password)
- Check JWT token format: `hermetic-token-{userId}`
- Verify SeedingService has populated user data

### Missing Data
- Check that ApplicationReadyEvent fired and seeded default data
- Verify SeedingService is properly configured
- Look for errors in startup logs

## Code Coverage

The hermetic server is designed to exercise as much of the production codebase as possible while avoiding external dependencies. This helps achieve high code coverage in tests by using the same business logic and service layers as production.
