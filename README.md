# Couple API

Couple API is a Spring Boot backend project for creating and managing a "couple" relationship between two users.

## Features

- User registration (`POST /users`)
- User login (`POST /users/login`)
- JWT access + refresh token generation
- Create a couple (`POST /api/v1/couples`)
- Get active couple details (`GET /api/v1/couples/me`)
- Delete a couple (current controller path is noted below)
- Database migration management with Flyway
- Observability stack with Actuator + Prometheus + Grafana

## Tech Stack

- Java 21
- Spring Boot 3.5.13
- Spring Web
- Spring Security (JWT + method security)
- Spring Data JPA
- PostgreSQL
- Flyway
- Docker / Docker Compose
- Spring Boot Actuator + Micrometer Prometheus

## Project Structure

```text
src/main/java/com/example/couple
|- config
|- controller
|- dto
|- entity
|- exception
|- mapper
|- repository
|- security
`- service
```

## Requirements

- Java 21
- Docker + Docker Compose (optional but recommended)
- A Base64 JWT secret for `JWT_SECRET`

## Environment Variables

The application uses the following environment variables:

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | PostgreSQL JDBC connection string | `jdbc:postgresql://localhost:5432/memories_dbbb` |
| `DB_USER` | Database username | `springuser` |
| `DB_PASS` | Database password | `springpassword` |
| `RABBITMQ_HOST` | RabbitMQ host | `localhost` |
| `RABBITMQ_PORT` | RabbitMQ port | `5672` |
| `RABBITMQ_USER` | RabbitMQ username | `guest` |
| `RABBITMQ_PASS` | RabbitMQ password | `guest` |
| `JWT_SECRET` | JWT signing key (Base64) | none (required) |
| `JWT_EXPIRATION` | Access token expiration (ms) | `86400000` |
| `JWT_REFRESH_EXPIRATION` | Refresh token expiration (ms) | `604800000` |

> Note: The project uses a `.env` file. Docker Compose loads this file automatically.

## Run Locally

1. Start dependency services:

```bash
docker compose up -d postgres rabbitmq
```

2. Set `JWT_SECRET` (must be Base64).

3. Start the application:

```bash
./gradlew bootRun
```

The app runs at `http://localhost:8080` by default.

## Full Stack with Docker

Run app + PostgreSQL + RabbitMQ + Prometheus + Grafana with one command:

```bash
docker compose up --build
```

Services:

- API: `http://localhost:8080`
- RabbitMQ UI: `http://localhost:15672`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

## API Endpoints

### 1) Register User

`POST /users`

Example request:

```json
{
  "username": "alice123",
  "firstName": "Alice",
  "lastName": "Doe",
  "email": "alice@example.com",
  "password": "supersecure1"
}
```

Example response (`201 Created`):

```json
{
  "id": 1,
  "username": "alice123",
  "email": "alice@example.com",
  "friendCode": "123456",
  "accessToken": "...",
  "refreshToken": "..."
}
```

### 2) Login User

`POST /users/login`

Example request:

```json
{
  "username": "alice123",
  "password": "supersecure1"
}
```

Example response (`200 OK`):

```json
{
  "id": 1,
  "username": "alice123",
  "email": "alice@example.com",
  "friendCode": "123456",
  "accessToken": "...",
  "refreshToken": "..."
}
```

### 3) Create Couple (Auth required)

`POST /api/v1/couples`

Header:

```text
Authorization: Bearer <access_token>
```

Example request:

```json
{
  "secondUserID": 2
}
```

Example response (`200 OK`):

```json
{
  "id": 10,
  "secondUser": {
    "id": 2,
    "username": "bob456",
    "email": "bob@example.com"
  }
}
```

### 4) Get Active Couple Details (Auth required)

`GET /api/v1/couples/me`

Example response (`200 OK`):

```json
{
  "id": 10,
  "user": {
    "id": 1,
    "username": "alice123",
    "email": "alice@example.com"
  },
  "createdAt": "2026-04-02T12:34:56"
}
```

### 5) Delete Couple (Auth required)

Based on the current controller mapping, the endpoint is currently:

`DELETE /api/v1/couples/api/v1/couples/{id}`

The expected REST shape is usually `DELETE /api/v1/couples/{id}`. In code, the method-level path is written as an absolute path, so it is duplicated.

## Error Format

Validation and runtime errors return `400 Bad Request`:

```json
{
  "error": "Error message"
}
```

## Security Notes

- JWT is parsed from `Authorization: Bearer <token>`.
- Endpoints marked with `@PreAuthorize("isAuthenticated()")` require authentication.
- `SecurityConfig` currently has `anyRequest().permitAll()`, so access control mainly relies on method security.

## Monitoring

Actuator endpoints:

- `/actuator/health`
- `/actuator/info`
- `/actuator/prometheus`

Prometheus config file: `prometheus.yml`

## Tests

```bash
./gradlew test
```
