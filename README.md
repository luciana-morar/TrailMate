# TrailMate

**An offline-first hiking and trail-tracking app: record routes with GPS, attach photos, and share your position live with friends.**

> Status: **work in progress.** The backend foundation (Clean Architecture, PostgreSQL, Routes REST API with pagination and filtering) is working. Authentication, GPS tracking, offline sync, real-time sharing and the React frontend are planned, see the [Roadmap](#roadmap).

---

## Table of contents
1. [Why this project](#why-this-project)
2. [What TrailMate does](#what-trailmate-does)
3. [Tech stack](#tech-stack)
4. [Architecture](#architecture)
5. [Getting started](#getting-started)
6. [API](#api)
7. [Testing](#testing)
8. [Roadmap](#roadmap)
9. [Design decisions](#design-decisions)
10. [Project structure](#project-structure)

---

## Why this project

Most outdoor apps stop working exactly where they are needed most: in the mountains, with no signal. TrailMate is designed **offline-first**. Everything you record is stored on the device and synchronised automatically once a connection is back.

It is also a learning project built to practise the concepts used in real production applications:

| Concept | Where it appears in TrailMate |
|---|---|
| REST services | CRUD for routes, photos, friends; documented with OpenAPI |
| Authentication | JWT access and refresh tokens, per-user data |
| Pagination and filtering | Paginated route feed with difficulty and text filters |
| WebSockets | Live location sharing between friends |
| Data synchronisation | Offline queue, idempotent sync endpoint, conflict handling |
| Location and maps | GPS recording, route drawing, distance and elevation stats |
| Camera and file system | Photos taken on the trail, GPX import and export |
| Animations | Screen transitions, animated route replay, elevation chart |
| Clean Architecture | Domain / Application / Infrastructure / Server layering |

## What TrailMate does

**Planned features (MVP):**

1. Sign up, log in, log out.
2. Record a route with GPS and see distance, duration, average speed and elevation gain.
3. Take photos during a hike; each photo is linked to the place where it was taken.
4. View a route and its photos on a map.
5. Browse route history with pagination, sorting and filters.
6. Use the app fully offline; changes sync when the connection returns.
7. Share a live route with friends who follow it on the map in real time.
8. Export and import routes as GPX files.

**Non-functional goals:** responsive and accessible UI, validated input, passwords stored as hashes, users can only access their own data, automated tests and CI, English documentation and a public demo.

## Tech stack

| Layer | Current | Planned |
|---|---|---|
| Language / runtime | Java 21 | |
| Backend | Spring Boot 3.3, Spring Web, Spring Data JPA, Bean Validation | Spring Security + JWT, Spring WebSocket |
| Database | PostgreSQL (PostGIS image) in Docker | PostGIS spatial queries, Flyway migrations |
| Build | Maven multi-module | GitHub Actions CI |
| Testing | JUnit 5, Mockito | Testcontainers, Playwright (end-to-end) |
| API docs | | springdoc-openapi (Swagger UI) |
| Frontend | | React + TypeScript + Vite, TanStack Query, Leaflet, Dexie (IndexedDB), Framer Motion |
| Mobile packaging | | Capacitor (camera, GPS, file system on Android) |

## Architecture

The backend follows **Clean Architecture**. Source-code dependencies point inwards only.

```
┌────────────────────────────────────────────┐
│ trailmate-server        (Presentation)     │  REST controllers, DTOs, error handling,
│                                            │  Spring configuration (composition root)
└──────────────────────┬─────────────────────┘
                       │ depends on
┌──────────────────────▼─────────────────────┐
│ trailmate-infrastructure                   │  JPA entities, Spring Data repositories,
│                                            │  adapters implementing application ports
└──────────────────────┬─────────────────────┘
                       │ depends on
┌──────────────────────▼─────────────────────┐
│ trailmate-application                      │  Use cases (services), ports (interfaces),
│                                            │  commands, pagination types
└──────────────────────┬─────────────────────┘
                       │ depends on
┌──────────────────────▼─────────────────────┐
│ trailmate-domain                           │  Entities and business rules,
│                                            │  plain Java, no framework
└────────────────────────────────────────────┘
```

Request flow for `POST /api/routes`:

```
Client → RouteController → RouteService → RouteRepository (port)
                                              ↑ implemented by
                              JpaRouteRepository → PostgreSQL
```

- The **domain** knows nothing about Spring, HTTP or databases. Business rules (for example "a route needs a name") live in the domain objects.
- The **application** layer defines *what* it needs through interfaces (ports), such as `RouteRepository`.
- The **infrastructure** layer provides the implementation (adapters) using JPA.
- The **server** layer exposes the application over HTTP and wires everything together.

## Getting started

### Prerequisites
- JDK 21
- Docker Desktop
- Git
- IntelliJ IDEA (recommended)

### 1. Clone
```bash
git clone https://github.com/luciana-morar/TrailMate.git
cd TrailMate
```

### 2. Start the database
```bash
docker compose up -d
```
This starts PostgreSQL (with PostGIS) in a container named `trailmate-postgres`, exposed on **localhost:5433**. Port 5433 is used so it does not clash with a PostgreSQL instance installed locally.

> The database credentials in `docker-compose.yml` and `application.yml` are **for local development only**. Never reuse them in a real deployment.

### 3. Build
From the `backend` folder:
```bash
./mvnw clean install        # Windows: .\mvnw.cmd clean install
```

### 4. Run
Run `TrailMateApplication` from IntelliJ, or:
```bash
./mvnw -pl trailmate-server spring-boot:run
```
The API is available at `http://localhost:8080`.

> `-pl trailmate-server` uses the other modules from your local Maven repository, so run `install` again after changing code in `domain`, `application` or `infrastructure`.

### Configuration
`backend/trailmate-server/src/main/resources/application.yml`:

| Property | Default | Meaning |
|---|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5433/trailmate` | Database connection |
| `spring.datasource.username` / `password` | `trailmate` / `trailmate` | Local development credentials |
| `spring.jpa.hibernate.ddl-auto` | `update` | Creates and updates tables automatically. Will be replaced by Flyway migrations. |

## API

Base URL: `http://localhost:8080/api`

### Routes
| Method | Endpoint | Description | Success |
|---|---|---|---|
| `GET` | `/routes?page=0&size=20&difficulty=EASY` | Paginated list, optional difficulty filter, newest first | `200` |
| `GET` | `/routes/{id}` | Get one route | `200` |
| `POST` | `/routes` | Create a route | `201` |
| `PUT` | `/routes/{id}` | Update a route | `200` |
| `DELETE` | `/routes/{id}` | Delete a route | `204` |

**Request body (`POST` / `PUT`):**
```json
{
  "name": "Cetățuia loop",
  "description": "Short city trail",
  "difficulty": "EASY",
  "distanceMeters": 3500
}
```
`difficulty` is one of `EASY`, `MEDIUM`, `HARD`.

**Paginated response (`GET /routes`):**
```json
{
  "items": [ { "id": "…", "name": "Cetățuia loop", "difficulty": "EASY", "distanceMeters": 3500.0, "createdAt": "…", "updatedAt": "…" } ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

**Errors:**
| Status | When | Body |
|---|---|---|
| `400` | Validation fails (blank name, negative distance, missing difficulty) | field → message map, or `{"error": "..."}` |
| `404` | Route does not exist | `{"error": "Route <id> not found"}` |

Interactive API documentation (Swagger UI) is on the roadmap.

## Testing
```bash
./mvnw test
```
Unit tests for the application layer use Mockito to replace the repository port, so they run without a database. Integration tests with Testcontainers are planned.

## Roadmap

Tasks are ordered. Finish each stage, with tests and documentation, before starting the next. A task is **done** when it works, has at least one automated test, passes CI, and is documented here.

### Stage 0: Foundation
- [x] Maven multi-module project (domain, application, infrastructure, server)
- [x] PostgreSQL + PostGIS in Docker Compose
- [x] `Route` domain model with validation rules
- [x] Routes REST API: CRUD, pagination, difficulty filter
- [x] Global error handling (400 / 404)
- [x] Unit tests for `RouteService`
- [ ] Verify every endpoint manually and fix issues found
- [ ] Add OpenAPI / Swagger UI
- [ ] Add GitHub Actions workflow (build + test on every push)
- [ ] Add a `.gitignore` check (no `target/`, `.idea/`, secrets)

### Stage 1: Solid backend
- [ ] Replace `ddl-auto: update` with Flyway migrations
- [ ] Integration tests with Testcontainers (repository and controller)
- [ ] Text search on route name (case-insensitive)
- [ ] Sorting options (date, distance)
- [ ] Move database credentials to environment variables

### Stage 2: Authentication
- [ ] `User` entity, registration with hashed passwords (BCrypt)
- [ ] Login returning access + refresh tokens (JWT)
- [ ] Protect all `/api/**` endpoints except register and login
- [ ] Link routes to their owner; users only see their own data
- [ ] Tests: unauthorised access returns 401, other users' data returns 404

### Stage 3: Tracking and maps (backend + first frontend)
- [ ] `TrackPoint` entity (latitude, longitude, elevation, timestamp)
- [ ] Upload and read track points for a route
- [ ] Compute distance (Haversine), duration and elevation gain
- [ ] PostGIS: store route geometry, query "routes within X km of me"
- [ ] React + TypeScript + Vite frontend: login, route list with infinite scroll, route form
- [ ] Map view of a route (Leaflet / OpenStreetMap)

### Stage 4: Photos and files
- [ ] Photo upload (multipart), size and type limits, storage on disk or S3-compatible
- [ ] Photos linked to coordinates and shown as map markers
- [ ] GPX export and import

### Stage 5: Offline-first synchronisation
- [ ] Client-generated IDs and `updatedAt` on all synced entities
- [ ] Local storage in IndexedDB with a queue of pending operations
- [ ] Online / offline indicator with pending-change counter
- [ ] Idempotent `/sync` endpoint
- [ ] Conflict strategy ("last write wins" first, documented) and tests for offline, reconnect, conflict and retry

### Stage 6: Real-time and social
- [ ] WebSocket endpoint authenticated with JWT
- [ ] Friend requests and friendships
- [ ] Share a live route; friends see the position on the map in real time

### Stage 7: Polish and delivery
- [ ] Animations: screen transitions, route replay, animated elevation chart
- [ ] Accessibility pass (semantic HTML, keyboard navigation, contrast, Lighthouse / axe)
- [ ] End-to-end tests with Playwright
- [ ] Capacitor build for Android (camera, GPS, file system)
- [ ] Deploy backend and frontend, add a live demo link and screenshots to this README

## Design decisions

- **Clean Architecture over a single module.** It keeps business rules independent from frameworks and makes the code easy to test. The cost is some extra mapping code between domain objects and JPA entities.
- **UUIDs as identifiers, generated by the application.** Clients can create records while offline and sync them later without ID collisions.
- **Own pagination types (`PageQuery`, `PageResult`).** The application layer does not depend on Spring Data.
- **Validation in two places.** Request DTOs use Bean Validation for fast, descriptive HTTP errors; the domain re-checks invariants so invalid objects can never be created.
- **PostGIS image from the start.** Spatial queries are planned, so the database is ready for them.

## Project structure

```
TrailMate/
├── docker-compose.yml
└── backend/
    ├── pom.xml                       parent POM
    ├── trailmate-domain/             entities, business rules
    ├── trailmate-application/        use cases, ports, pagination types, unit tests
    ├── trailmate-infrastructure/     JPA entities, adapters
    └── trailmate-server/             REST controllers, configuration, application entry point
```

## License
To be decided.