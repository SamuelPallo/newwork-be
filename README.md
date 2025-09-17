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

- **Using Docker Compose:**
  ```sh
  cd db
  docker-compose up -d
  ```
  - **Database connection details:**
    - **Database:** hrapp
    - **User:** postgres
    - **Password:** postgres
    - **Host:** hrapp_postgres (service/container name)
    - **Port:** 5432
  - **Adminer UI for DB management:**
    - Accessible at: `http://localhost:8080`

- **Using Docker directly (custom network):**
  ```sh
  docker network create newwork
  docker run --name newwork-postgres --network newwork \
    -e POSTGRES_USER=appuser -e POSTGRES_PASSWORD=apppassword -e POSTGRES_DB=appdb \
    -p 5432:5432 -d postgres:15
  ```
  - **Database connection details:**
    - **Database:** appdb
    - **User:** appuser
    - **Password:** apppassword
    - **Host:** newwork-postgres (container name)
    - **Port:** 5432
  - If you want a DB UI, you can run Adminer:
    ```sh
    docker run --name adminer --network newwork -p 8080:8080 -d adminer
    ```
    - Accessible at: `http://localhost:8080`

- You can change these credentials in `src/main/resources/application.yml` or `application-local.yml`.

### 2. Run the Backend (Docker only, no Compose)

1. **Build the backend jar:**
   ```sh
   ./gradlew build
   ```
2. **Build the Docker image (Java 21):**
   ```sh
   docker build -t newwork-backend .
   ```
3. **Run the backend container on the same network as the database:**
   ```sh
   docker run --name newwork-backend --network newwork -p 8081:8081 newwork-backend
   ```
   - The backend will be available at: `http://localhost:8081`
   - **Swagger UI:** `http://localhost:8081/api/v1/swagger-ui.html`
   - **OpenAPI docs:** `http://localhost:8081/api/v1/api-docs`
   - The backend will connect to the database using the credentials and host specified above (e.g., `newwork-postgres` or `hrapp_postgres`).
   - Make sure the database container is running and accessible on the same Docker network (`newwork`).

### 3. Run the Backend (Gradle)

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
   - The backend will be available at: `http://localhost:8081`
   - **Swagger UI:** `http://localhost:8081/api/v1/swagger-ui.html`
   - **OpenAPI docs:** `http://localhost:8081/api/v1/api-docs`
   - The backend will connect to the database using the credentials and host specified above (e.g., `newwork-postgres` or `hrapp_postgres`).
   - Make sure the database container is running and accessible on the same Docker network (`newwork`).

### 4. Verify Network Connectivity

- To check which network a container is on:
  ```sh
  docker inspect <container_name> --format '{{json .NetworkSettings.Networks}}'
  ```
- To see all containers on the network:
  ```sh
  docker network inspect newwork
  ```
- Both backend and database containers must appear in the `newwork` network's container list.
- `src/main/java/com/hr/newwork/services/` — Business logic
- `src/main/java/com/hr/newwork/repositories/` — Data access
- `src/main/java/com/hr/newwork/data/entity/` — JPA entities
- `src/main/java/com/hr/newwork/data/dto/` — Data Transfer Objects
- `src/main/java/com/hr/newwork/config/` — Configuration (AOP, security, etc.)
- `src/main/java/com/hr/newwork/util/` — Utilities (e.g., log sanitization)

---

## Quick Start: Running the App

### 1. Start the Database

- **With Docker Compose:**
  ```sh
  cd db
  docker-compose up -d
  ```
  - Database: `hrapp` | User: `postgres` | Password: `postgres` | Host: `hrapp_postgres` | Port: `5432`
  - Adminer UI: [http://localhost:8080](http://localhost:8080)

- **With Docker directly:**
  ```sh
  docker network create newwork
  docker run --name newwork-postgres --network newwork \
    -e POSTGRES_USER=appuser -e POSTGRES_PASSWORD=apppassword -e POSTGRES_DB=appdb \
    -p 5432:5432 -d postgres:15
  ```
  - Database: `appdb` | User: `appuser` | Password: `apppassword` | Host: `newwork-postgres` | Port: `5432`
  - Adminer UI (optional): [http://localhost:8080](http://localhost:8080)

### 2. Build and Run the Backend

- **Build the JAR:**
  ```sh
  ./gradlew build
  ```
- **Build Docker image:**
  ```sh
  docker build -t newwork-backend .
  ```
- **Run the backend container:**
  ```sh
  docker run --name newwork-backend --network newwork -p 8081:8081 newwork-backend
  ```
  - API: [http://localhost:8081](http://localhost:8081)
  - Swagger UI: [http://localhost:8081/api/v1/swagger-ui.html](http://localhost:8081/api/v1/swagger-ui.html)
  - OpenAPI docs: [http://localhost:8081/api/v1/api-docs](http://localhost:8081/api/v1/api-docs)

---

## Architecture Decisions

- **Layered Architecture:**
  - Controllers (REST endpoints)
  - Services (business logic)
  - Repositories (data access)
  - DTOs and Mappers (data transfer and conversion)
  - Exception handling via custom exceptions and advices
  - Security: Spring Security + JWT
  - Logging: AOP-based, with sensitive data masking
  - Database migrations: Liquibase
- **Configuration:**
  - Profiles for local, docker, etc. (`application.yml`, `application-docker.yml`, `application-local.yml`)
  - Externalized configuration for DB, JWT, etc.
- **Resilience:**
  - Basic resilience config (see `ResilienceConfig.java`)
- **Utilities:**
  - Log sanitization, security utilities, etc.

---

## What Could Be Improved

- **Testing:**
  - No unit test coverage for services, controllers, or mappers
  - No integration tests for API endpoints or DB interactions
  - No automated test coverage for security/auth flows
- **Validation:**
  - Input validation is minimal; could use more robust validation annotations and custom validators
  - No centralized validation error handling
- **Error Handling:**
  - Exception handling is present but could be more granular and standardized
- **API Documentation:**
  - Swagger/OpenAPI is present, but endpoint documentation could be more detailed
- **Security:**
  - Security config could be refactored for clarity and extensibility
  - No role-based access tests
- **Resilience:**
  - Resilience features (e.g., retries, circuit breakers) are basic and could be expanded
- **Database:**
  - Liquibase changelogs are present, but DB seeding and migration strategies could be improved
- **Code Quality:**
  - Some duplication in DTOs and mappers
  - Logging could be standardized further
- **DevOps:**
  - No CI/CD pipeline
  - No Docker Compose for backend (only for DB)
  - No health checks or readiness probes
- **Frontend Integration:**
  - No integration tests with frontend
- **Documentation:**
  - README could include more troubleshooting and FAQ

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
