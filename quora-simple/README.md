# Quora Backend Project

A production-ready Spring Boot 3 + PostgreSQL REST API modelled after Quora's core features.

## Architecture

Multi-module Maven project:

```
quora-backend/
├── quora-api/        ← REST controllers, Security config, Exception handler, Swagger UI
├── quora-service/    ← Business logic, JPA entities, DAOs, JWT provider
└── quora-db/         ← DB config, Flyway migrations (SQL scripts)
```

## Tech Stack

| Layer        | Technology                     |
|--------------|-------------------------------|
| Framework    | Spring Boot 3.2                |
| Security     | Spring Security + JWT (JJWT)   |
| Persistence  | Spring Data JPA + Hibernate    |
| Database     | PostgreSQL 15+                 |
| Migrations   | Flyway                         |
| API Docs     | SpringDoc OpenAPI 3 (Swagger)  |
| Build Tool   | Maven (multi-module)           |
| Java Version | 17                             |

## API Endpoints

### User
| Method | Endpoint                         | Auth | Description          |
|--------|----------------------------------|------|----------------------|
| POST   | `/user/signup`                   | No   | Register new user    |
| POST   | `/user/signin`                   | No   | Sign in, get JWT     |
| POST   | `/user/signout`                  | Yes  | Sign out             |
| GET    | `/user/{userId}`                 | Yes  | View user profile    |
| DELETE | `/user/admin/userDelete/{userId}`| Yes (admin) | Delete user |

### Question
| Method | Endpoint                   | Auth | Description                    |
|--------|----------------------------|------|--------------------------------|
| POST   | `/question/create`         | Yes  | Create a question              |
| GET    | `/question/all`            | Yes  | Get all questions              |
| PUT    | `/question/edit/{id}`      | Yes (owner) | Edit question           |
| DELETE | `/question/delete/{id}`    | Yes (owner/admin) | Delete question  |
| GET    | `/question/all/{userId}`   | Yes  | Questions by a specific user   |

### Answer
| Method | Endpoint                                | Auth | Description                 |
|--------|-----------------------------------------|------|-----------------------------|
| POST   | `/question/{questionId}/answer/create`  | Yes  | Post an answer              |
| PUT    | `/question/answer/edit/{answerId}`      | Yes (owner) | Edit answer          |
| DELETE | `/question/answer/delete/{answerId}`    | Yes (owner/admin) | Delete answer  |
| GET    | `/question/{questionId}/answers/all`    | Yes  | All answers for a question  |

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL 15+ running locally

### Database Setup
```sql
CREATE DATABASE quora;
CREATE USER postgres WITH PASSWORD 'changeme';
GRANT ALL PRIVILEGES ON DATABASE quora TO postgres;
```

### Configuration
Edit `quora-db/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/quora
spring.datasource.username=postgres
spring.datasource.password=changeme
app.jwt.secret=<your-256-bit-secret>
```

### Build & Run
```bash
mvn clean install
mvn -pl quora-api spring-boot:run
```

The API will start at `http://localhost:8080`.  
Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

## Authentication

All protected endpoints require a `Bearer` token in the Authorization header:

```
Authorization: Bearer <token-from-signin>
```

## Project Improvements Over Original

| Area               | Before               | After                                         |
|--------------------|----------------------|-----------------------------------------------|
| Entities           | Stub classes, no JPA | Full JPA entities with constraints & relations|
| DAOs               | None                 | Spring Data JPA repositories                  |
| Business services  | Empty stubs          | Full implementations with auth checks         |
| Controllers        | Empty stubs          | REST endpoints with request/response models   |
| Security           | None                 | JWT-based stateless auth + Spring Security    |
| Error handling     | None                 | Global @RestControllerAdvice with error codes |
| Database schema    | 1-column table       | Normalized schema with FK, indexes, UUIDs     |
| DB migrations      | Raw SQL              | Flyway versioned migrations                   |
| API documentation  | None                 | Swagger / OpenAPI 3                           |
| Maven config       | Incomplete POM       | Full multi-module build with dependencies     |
