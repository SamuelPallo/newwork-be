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

## Quick Start: Running the App (Locally or with Docker)

> **Important:**
> - All Docker Compose commands (e.g., `docker-compose up -d`) must be run from the `db` directory.
> - All backend build and run commands (e.g., `./gradlew build`, `docker build`, `./gradlew bootRun`) must be run from the project root (where `build.gradle` and `Dockerfile` are located).
> - If you are using Windows, use `gradlew.bat` instead of `./gradlew`.
> - For inspecting containers or troubleshooting, you can run commands from any directory, but ensure you have the correct context (e.g., `docker ps`, `docker network inspect newwork`).

### 1. Start the Database (PostgreSQL)

- **Recommended: Using Docker Compose**
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
    - Accessible at: [http://localhost:8080](http://localhost:8080)

- **Alternative: Using Docker directly (custom network)**
  ```sh
  docker network create newwork
  # Note: If you see 'network with name newwork already exists', you can ignore this message and proceed. The network is already available for your containers.
  docker run --name hrapp_postgres --network newwork \
    -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=hrapp \
    -p 5432:5432 -d postgres:15
  docker run --name adminer --network newwork -p 8080:8080 -d adminer

  # On Windows CMD (use caret ^ for line continuation or write all on one line):
  docker run --name hrapp_postgres --network newwork ^
    -e POSTGRES_USER=postgres ^
    -e POSTGRES_PASSWORD=postgres ^
    -e POSTGRES_DB=hrapp ^
    -p 5432:5432 -d postgres:15
  docker run --name adminer --network newwork -p 8080:8080 -d adminer

  # Or, all on one line (works everywhere):
  docker run --name hrapp_postgres --network newwork -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=hrapp -p 5432:5432 -d postgres:15
  docker run --name adminer --network newwork -p 8080:8080 -d adminer
  ```
  - **Database connection details:**
    - **Database:** hrapp
    - **User:** postgres
    - **Password:** postgres
    - **Host:** hrapp_postgres (container name)
    - **Port:** 5432
  - **Adminer UI:** [http://localhost:8080](http://localhost:8080)

- You can change these credentials in `src/main/resources/application.yml` or `application-local.yml`.

- **Note:** When building and running the backend with Docker, the application will use the `docker` Spring profile by default. Make sure your `application-docker.yml` is up to date with the correct database credentials and host.
### 2. Start the Backend

- **Option A: Run with Docker (recommended for production-like environment)**
  1. **Build the backend jar:**
     ```sh
     ./gradlew build
     ```
  2. **Build the Docker image:**
     ```sh
     docker build -t newwork-backend .
     ```
  3. **Run the backend container on the same network as the database:**
     ```sh
     docker run --name newwork-backend --network newwork -p 8081:8081 newwork-backend
     ```
  - The backend will be available at: [http://localhost:8081](http://localhost:8081)
  - **Swagger UI:** [http://localhost:8081/api/v1/swagger-ui.html](http://localhost:8081/api/v1/swagger-ui.html)
  - **OpenAPI docs:** [http://localhost:8081/api/v1/api-docs](http://localhost:8081/api/v1/api-docs)

- **Option B: Run locally with Gradle (for development)**
  1. **Configure environment variables (if needed):**
     - Edit `src/main/resources/application.yml` or `application-local.yml` for DB credentials or other settings.
  2. **Build the project:**
     ```sh
     ./gradlew build
     ```
  3. **Run the application with the local profile:**
     ```sh
     ./gradlew bootRun --args='--spring.profiles.active=local'
     ```
  - The backend will be available at: [http://localhost:8081](http://localhost:8081)
  - **Swagger UI:** [http://localhost:8081/api/v1/swagger-ui.html](http://localhost:8081/api/v1/swagger-ui.html)
  - **OpenAPI docs:** [http://localhost:8081/api/v1/api-docs](http://localhost:8081/api/v1/api-docs)

### 3. Verify Network Connectivity (if using Docker)

- To check which network a container is on:
  ```sh
  docker inspect <container_name> --format '{{json .NetworkSettings.Networks}}'
  ```
- To see all containers on the network:
  ```sh
  docker network inspect newwork
  ```
- Both backend and database containers must appear in the `newwork` network's container list.

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
  - No password reset or account recovery flows; would benefit from a secure password reset feature (e.g., email with token)
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
- **Microservices & Event-Driven Architecture:**
  - The current monolith could be split into microservices (e.g., notification, feedback, absence, user management)
  - A dedicated Notification Service could be introduced, using Kafka or another pub/sub system for event-driven communication
  - When feedback is added to a user, or an absence is requested, an event could be published to Kafka; the Notification Service would consume these events and send emails, push notifications, or in-app alerts
  - This would decouple business logic from notification delivery, improve scalability, and allow for more flexible notification channels
- **User Experience Enhancements:**
  - Add password reset functionality (e.g., via email with secure token)
  - Add email or in-app notifications for key events (feedback received, absence requested/approved/denied, etc.)
- **Polish/Content Enhancement Service:**
  - Currently, the Polish feature is not functional due to lack of an API key
  - In a production system, a dedicated microservice could be created to handle content polishing, integrating with multiple LLMs or external APIs using design patterns (e.g., Strategy, Adapter) for extensibility
  - This would allow switching between different content enhancement providers or models as needed
- **Other Potential Improvements:**
  - Add audit logging for sensitive actions (user updates, feedback changes, etc.)
  - Implement rate limiting and brute-force protection on authentication endpoints
  - Add support for multi-tenancy or organization-based data segregation
  - Enhance role and permission management for more granular access control
  - Add support for file uploads (e.g., attachments to absences or feedback)
  - Improve accessibility and internationalization (i18n) support
  - Add API versioning and deprecation strategy

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

---

## Sample Data

- The database is seeded with several sample users for testing/demo purposes.
- **All sample users have the same password:** `password12345`
- **User emails can be found in the database** (see the `users` table via Adminer or a database client).
- You can find the seed logic in the Liquibase changelogs under `src/main/resources/db/changelog/` (e.g., `002-add-default-users.xml`, `005-seed-managers-hierarchy.xml`).
