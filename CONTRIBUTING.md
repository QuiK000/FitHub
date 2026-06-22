# Contributing to FitHub

Thank you for your interest in contributing to FitHub! This guide covers the setup process, coding conventions, and architectural rules to follow when working on this codebase.

## Prerequisites

- **Java 21** (Eclipse Temurin recommended)
- **Maven 3.9+** or the included Maven wrapper (`mvnw`)
- **Node.js 20+** and npm
- **Docker and Docker Compose**
- **IDE**: IntelliJ IDEA recommended (project includes `.idea/` config)

## Getting Started

### 1. Fork & Clone

```bash
git clone https://github.com/<your-username>/FitHub.git
cd FitHub
```

### 2. Start Infrastructure

```bash
cd docker
docker compose -f docker-compose-dev.yml up -d
```

This starts PostgreSQL, Redis, MailDev, Prometheus, and Grafana.

### 3. Configure Environment

```bash
cp env.example env.properties
```

Edit `env.properties` with your local values. The defaults work with the development compose file.

### 4. Run the Backend

```bash
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`. Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### 5. Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

The SPA starts at `http://localhost:5173`.

### 6. Run Tests

```bash
# Backend
./mvnw clean verify

# Frontend
cd frontend
npm run lint
npm run build
```

## Project Structure Rules

### Backend Module Organization

Each domain module follows a strict layout:

```
modules.<domain>/
├── controller/      # @RestController — HTTP mapping only
├── service/         # IXxxService interface
│   └── impl/        # XxxServiceImpl — business logic
├── repository/      # Spring Data JPA interface
├── entity/          # JPA @Entity
├── dto/
│   ├── request/     # Inbound DTOs
│   └── response/    # Outbound DTOs
├── mapper/          # Entity ↔ DTO conversion
├── enums/           # Module-specific enumerations
├── validator/       # (optional) Custom validators
├── scheduler/       # (optional) @Scheduled jobs
├── event/           # (optional) ApplicationEvent subclasses
└── listener/        # (optional) @EventListener handlers
```

### Architectural Rules

1. **Services are always interfaces.** Define `IXxxService` in the `service/` package and implement in `service/impl/`. This enables clean mocking in tests.

2. **Entities never leak to the API.** Controllers accept and return DTOs. Use mappers to convert. This prevents accidental exposure of JPA relationships and internal fields.

3. **No cross-module repository access.** If module A needs data from module B, it calls module B's service interface — never its repository directly.

4. **Business logic lives in services, not controllers.** Controllers handle HTTP concerns (parameter binding, status codes, content negotiation). Services handle rules, validation, and data orchestration.

5. **Use `BaseEntity` for all entities.** It provides UUID primary keys, audit fields (createdDate, lastModifiedDate, createdBy, lastModifiedBy), and consistent builder patterns.

6. **Use `@Transactional` at the service level.** Mark read-only methods with `@Transactional(readOnly = true)` and write operations with `@Transactional`.

7. **Error handling via `BusinessException`.** Throw `BusinessException` with an `ErrorCode` enum value. The `GlobalExceptionHandler` maps these to consistent JSON responses. Don't return error responses manually from controllers.

8. **Caching uses Spring's `@Cacheable` annotations.** Cache names must be registered in `RedisConfig` with an explicit TTL.

## Code Style

### Java

- **Lombok**: Use `@Data`, `@Getter`, `@Setter`, `@Builder`, `@SuperBuilder`, `@RequiredArgsConstructor` freely. Avoid manual getters/setters.
- **Constructors**: Use constructor injection via `@RequiredArgsConstructor` with `final` fields.
- **Logging**: Use `@Slf4j` (Lombok). Log at INFO for business events, DEBUG for diagnostics, ERROR for failures.
- **Nullability**: Use `@NonNull` annotations from Lombok where appropriate.
- **Records**: Use Java records for immutable DTOs where Lombok isn't needed.
- **Naming**: Entities use singular nouns (`User`, `TrainingSession`). DTOs use descriptive suffixes (`LoginRequest`, `TrainingSessionResponse`). Services use `I` prefix for interfaces (`IUserService`).

### TypeScript (Frontend)

- **Components**: Functional components with hooks. No class components.
- **Props**: Define explicit types for component props.
- **Services**: Thin wrappers around the Axios instance. No business logic in services.
- **Types**: Centralized in `types/` directory. Re-export via `index.ts`.
- **Naming**: Components use PascalCase filenames (`ProtectedRoute.tsx`). Services use camelCase (`auth.service.ts`). Types use `.types.ts` suffix.

## Testing

### Backend Testing

- **Unit tests**: JUnit 5 + Mockito + AssertJ. Mock repository and service dependencies.
- **Integration tests**: Use Testcontainers for PostgreSQL. The `test` profile configures H2 as a fallback.
- **Controller tests**: Use `@WebMvcTest` with MockMvc.
- **Test naming**: `*Test.java` for unit tests, `*IT.java` for integration tests.

```bash
./mvnw test              # Unit tests only
./mvnw clean verify      # Full verification including integration tests
```

### Frontend Testing

```bash
cd frontend
npm run lint             # ESLint
npx tsc --noEmit         # Type checking
npm run test             # Run tests (if present)
npm run build            # Production build validation
```

## Pull Request Process

1. **Create a feature branch** from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** following the conventions above.

3. **Run the full verification** before pushing:
   ```bash
   # Backend
   ./mvnw clean verify

   # Frontend
   cd frontend
   npm run lint
   npm run build
   ```

4. **Commit with clear messages.** Use present tense ("Add feature" not "Added feature"). Keep commits focused on a single change.

5. **Push and open a PR** against `main`. The CI pipeline will run automatically:
   - Backend: Maven verify + artifact upload
   - Frontend: lint + type check + build

6. **Respond to review feedback.** Update your branch as needed.

## Commit Convention

Follow a consistent commit message style:

```
<type>: <description>

Types:
  feat     — New feature
  fix      — Bug fix
  docs     — Documentation only
  style    — Code style (formatting, no logic change)
  refactor — Code restructuring (no feature/fix)
  test     — Adding or updating tests
  chore    — Build, CI, or tooling changes
```

Examples:
```
feat: add waitlist position tracking for training sessions
fix: prevent duplicate session joins when capacity is full
docs: add architecture overview with Mermaid diagrams
refactor: extract email sending to async event listener
test: add unit tests for membership expiration logic
```

## Architecture Decision Records

When making a significant architectural change (new module, new infrastructure service, changes to the data model), document the decision in the PR description with:

1. **Problem**: What issue does this solve?
2. **Approach**: What was chosen and why?
3. **Alternatives considered**: What else was evaluated?
4. **Impact**: What does this affect (modules, data model, API surface)?

## Getting Help

- Check the existing documentation in `docs/`.
- Review the README for project overview and setup.
- Open a GitHub issue for bugs or feature requests.
