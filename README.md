# Expense Tracker

![CI](https://github.com/thiagodacoregio/expense-tracker/actions/workflows/ci.yml/badge.svg)

**Live API:** https://expense-tracker-0qeb.onrender.com/api/expenses

> Hosted on Render's free tier: the first request after 15 minutes of inactivity may take up to a minute while the service wakes up.

REST API for tracking personal expenses, built with Java 25 and Spring Boot 4.

## Tech stack

- Java 25, Spring Boot 4 (Web MVC, Data JPA, Validation)
- PostgreSQL 17 with Flyway migrations
- JUnit, Mockito, MockMvc and Testcontainers
- Docker and Docker Compose
- GitHub Actions for CI
- Deployed on Render (Docker) with a managed PostgreSQL database on Neon

## Running locally

Requires Docker.

```bash
docker compose up -d --build
```

The API will be available at `http://localhost:8080/api/expenses`.

## Running tests

```bash
./mvnw test
```

## API endpoints

| Method | Path | Description |
|---|---|---|
| POST | `/api/expenses` | Create an expense |
| GET | `/api/expenses?month=2026-10` | List expenses (optional month filter) |
| GET | `/api/expenses/{id}` | Get one expense |
| PUT | `/api/expenses/{id}` | Update an expense |
| DELETE | `/api/expenses/{id}` | Delete an expense |