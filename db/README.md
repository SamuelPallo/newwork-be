# HRApp Database Setup

This guide helps you start the Postgres database (and Adminer UI) for local development using Docker Compose.

## Prerequisites
- Docker installed
- Docker Compose installed

## Quick Start

1. **Start the database and Adminer:**
   ```powershell
   docker-compose up -d
   ```
   This will start Postgres on port 5432 and Adminer on port 8080.

2. **Access Adminer UI:**
   - Open [http://localhost:8080](http://localhost:8080) in your browser.
   - Use these credentials:
     - System: PostgreSQL
     - Server: postgres
     - Username: postgres
     - Password: postgres
     - Database: hrapp

3. **Stop the services:**
   ```powershell
   docker-compose down
   ```

## Database Details
- **Postgres version:** 15
- **Database name:** hrapp
- **Username:** postgres
- **Password:** postgres

## Liquibase Migrations
- Migration files are in `db/changelog/`.
- You can apply migrations using a Spring Boot app with Liquibase integration (recommended for most teams).
- Alternatively, you can run Liquibase CLI manually if needed.

### Why use Spring Boot for Liquibase?
- **Best practice:** Spring Boot automatically applies Liquibase changes on startup, keeps DB schema in sync with your app, and supports rollback/versioning.
- **How:** Add Liquibase dependency to your backend's `pom.xml` or `build.gradle`, configure the changelog path, and start the app. Migrations will run automatically.

---

For more info, see the backend README for Spring Boot + Liquibase setup instructions.
