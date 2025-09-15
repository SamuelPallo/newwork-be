# Newwork Backend

## Overview

This is the backend for the Newwork application, built with Java and Spring Boot. It provides RESTful APIs for managing absences, users, feedback, authentication, and more. The project follows a layered architecture (Controller, Service, Repository, Mapper) and includes advanced logging with AOP, including sensitive data sanitization.

---

## Features

- RESTful API endpoints for core business domains
- Layered architecture: Controller, Service, Repository, Mapper
- AOP-based logging for request/response and intermediate steps
- Sensitive data masking in logs (passwords, credentials, tokens, etc.)
- Exception handling and resilience
- Entity-DTO mapping
- PostgreSQL database
- Database migrations with Liquibase
- Security with Spring Security and JWT

---

## Prerequisites

- Java 21 or newer
- Gradle 7 or newer
- Docker (for local database)
- Git

---

## Running the Project Locally or in Docker

You can run the backend in two ways: directly with Gradle, or as a Docker container. In both cases, you need a running PostgreSQL database (see below).

### 1. Start the Database in Docker

The backend uses PostgreSQL. You must have the database running before starting the backend.

- Using Docker Compose (recommended):
  ```sh
  cd db
  docker-compose up -d
  ```
- Or, using Docker directly:
  ```sh
  docker run --name newwork-postgres -e POSTGRES_USER=appuser -e POSTGRES_PASSWORD=apppassword -e POSTGRES_DB=appdb -p 5432:5432 -d postgres:15
  ```
- **Default DB:** appdb
- **User:** appuser
- **Password:** apppassword
- **Port:** 5432
- You can change these in `src/main/resources/application.yml` or `application-local.yml`.

### 2. Run the Backend

#### Option A: Run with Gradle

1. **Configure environment variables (if needed):**
   - Edit `src/main/resources/application.yml` or `application-local.yml` for DB credentials or other settings.
2. **Build the project:**
   ```sh
   ./gradlew build
   ```
3. **Run the application:**
   ```sh
   ./gradlew bootRun
   ```
   The API will be available at `http://localhost:8081`.

#### Option B: Run as a Docker Container

1. **Build the backend jar:**
   ```sh
   ./gradlew build
   ```
2. **Build the Docker image (Java 21):**
   ```sh
   docker build -t newwork-backend .
   ```
3. **Run the backend container:**
   ```sh
   docker run --name newwork-backend --network host -p 8081:8081 newwork-backend
   ```
   - The backend will be available at `http://localhost:8081`.
   - The container uses Java 21 and sets the Spring profile to `local` by default.
   - You can pass environment variables (e.g., DB credentials) using `-e` flags or a custom config file.
   - Make sure the database container is running and accessible.

---

## Project Structure

- `src/main/java/com/hr/newwork/controllers/` — REST controllers
- `src/main/java/com/hr/newwork/services/` — Business logic
- `src/main/java/com/hr/newwork/repositories/` — Data access
- `src/main/java/com/hr/newwork/data/entity/` — JPA entities
- `src/main/java/com/hr/newwork/data/dto/` — Data Transfer Objects
- `src/main/java/com/hr/newwork/config/` — Configuration (AOP, security, etc.)
- `src/main/java/com/hr/newwork/util/` — Utilities (e.g., log sanitization)

---

## Database Migrations with Liquibase

- Liquibase is used for managing database schema changes and migrations.
- Changelogs are located in `src/main/resources/db/changelog/`.
- On application startup, Liquibase automatically applies any pending migrations to the database.
- To add a new migration, create a new changelog file and include it in `db.changelog-master.yaml`.

---

## Logging & AOP

- All requests and responses are logged at a general level, with sensitive data masked (e.g., passwords, tokens, hashes).
- Intermediate logs are present for service, repository, mapper, and REST template calls.
- Log sanitization is handled via a utility class and reused across the project.

---

## Security & Authentication

- **Spring Security** is used to secure endpoints and manage authentication/authorization.
- **JWT (JSON Web Token)** is used for stateless authentication. After login, clients receive a JWT, which must be included in the `Authorization` header for protected endpoints.
- Security configuration is located in `src/main/java/com/hr/newwork/config/SecurityConfig.java`.
- JWT utilities and filters are implemented to validate tokens and extract user details.
- Passwords and sensitive data are always masked in logs.

---

## Testing

Run all tests with:
```sh
./gradlew test
```

---

## Troubleshooting

- Ensure Docker is running for the database.
- Check `application.yml` for correct DB settings.
- Use `./gradlew clean` if you encounter build issues.
- Logs are available in the console; check for errors or stack traces.

---

## Contribution

- Fork the repo and create a feature branch.
- Follow code style and add tests for new features.
- Open a pull request with a clear description.

---

## License

This project is licensed under the MIT License.

---

## Contact

For questions or support, contact the maintainers or open an issue in the repository.

---

Replace `<repo-url>` with your actual repository URL. Adjust DB details and structure as needed for your project.
