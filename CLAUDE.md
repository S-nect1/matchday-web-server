# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.5 application named "matchday" built with Java 21 and Gradle 8. The project uses MySQL as the database with JPA/Hibernate for data access, Spring Security for authentication/authorization, and includes validation capabilities.

## Development Requirements

- **Java Version**: Java 17 or newer required (configured for Java 21)
- **Build Tool**: Gradle with wrapper (use `./gradlew` on Unix/Mac, `gradlew.bat` on Windows)
- **Database**: MySQL (connector included in runtime dependencies)

## Common Development Commands

### Building and Running
```bash
# Build the project
./gradlew build

# Run the application (requires Java 17+)
./gradlew bootRun

# Run in development mode with automatic restart
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Testing
```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.matchday.MatchdayApplicationTests"

# Run tests with continuous build (re-run on file changes)
./gradlew test --continuous
```

### Code Quality and Build Tasks
```bash
# Clean build directory
./gradlew clean

# Generate build scan for insights
./gradlew build --scan

# List all available tasks
./gradlew tasks
```

## Project Structure

```
src/
├── main/
│   ├── java/com/matchday/           # Main application code
│   │   └── MatchdayApplication.java # Spring Boot main class
│   └── resources/
│       ├── application.properties   # Configuration
│       ├── static/                  # Static web resources
│       └── templates/               # View templates
└── test/
    └── java/com/matchday/           # Test code
        └── MatchdayApplicationTests.java
```

## Technology Stack

- **Framework**: Spring Boot 3.5.5 with Spring MVC
- **Security**: Spring Security (authentication and authorization)
- **Data Access**: Spring Data JPA with MySQL
- **Validation**: Spring Boot Validation starter
- **Testing**: JUnit 5 Platform with Spring Boot Test
- **Development**: Lombok for reducing boilerplate code

## Architecture Notes

This is a standard Spring Boot application following the typical layered architecture:

1. **Application Layer**: `MatchdayApplication.java` - Main entry point with `@SpringBootApplication`
2. **Web Layer**: Controllers for handling HTTP requests (to be developed under `com.matchday`)
3. **Service Layer**: Business logic components (to be developed)
4. **Repository Layer**: Data access using Spring Data JPA (to be developed)
5. **Entity Layer**: JPA entities representing database tables (to be developed)

## Development Setup Notes

- The project requires Java 17+ to build and run (current build.gradle specifies Java 21)
- MySQL database connection needs to be configured in `application.properties`
- Lombok is included for reducing boilerplate code in entities and DTOs
- Spring Security is configured but needs customization based on requirements
- Static resources go in `src/main/resources/static/`
- Templates (if using server-side rendering) go in `src/main/resources/templates/`

## Key Dependencies

- Spring Boot Starters: Web, Data JPA, Security, Validation
- MySQL Connector for database connectivity
- Lombok for code generation
- JUnit 5 and Spring Security Test for testing

## The Golden Rule
When unsure about implementation details, ALWAYS ask the developer.
@PRD.md의 제품을 만들고 있습니다.
각 작업은 @tasklist.md의 내용을 참고하여 수행합니다.
작업을 할 때, 그에 상응하는 테스트 코드도 함께 만듭니다.
JPA에서 엔티티간 관계를 설정할 때 양방향 매핑은 정말 필요한 경우가 아니라면 자제해.
생성자가 아닌 정적 팩토리 메서드를 사용하여 개발을 진행할꺼야.
도메인 주도 개발(Domain Driven Development) 원칙에 따라 개발을 진행해줘.