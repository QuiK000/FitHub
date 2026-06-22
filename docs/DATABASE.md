# Database

FitHub uses **PostgreSQL** as its primary data store with **Flyway** for schema migration management. Hibernate runs in `validate` mode — the schema is owned entirely by Flyway.

## Migration Strategy

All schema changes live in `src/main/resources/db/migration/` following Flyway's naming convention: `V{version}__{description}.sql`.

| Migration | Purpose |
|---|---|
| V1 | Auth tables (users, roles, user_roles, verification_tokens) |
| V2 | Profile tables (client_profiles, trainer_profiles, specializations, trainer_specialization) |
| V3 | Membership & payment tables |
| V4 | Training tables (training_sessions, training_client, attendances) |
| V5 | Exercise & workout tables (exercises, workout_plans, workout_plan_exercises, client_workout_plans, workout_logs) |
| V6 | Health/body tables (body_measurements, body_measurement_details, progress_photos, goals, personal_records) |
| V7 | Nutrition tables (foods, meal_plans, meals, meal_foods, water_intake) |
| V8 | Utility tables (email_failure_log) |
| V9 | Default data seeding (roles, specializations, exercises) |
| V10–V29 | Extended seed data, crypto payment fields, notifications, reviews, waitlist, Telegram chat ID, test data |

**Key properties:**

- `flyway.baseline-on-migrate: false` — no baseline for existing databases.
- `flyway.validate-on-migrate: true` — validates checksums before applying.
- `hibernate.ddl-auto: validate` — Hibernate verifies the entity model matches the schema but never modifies it.
- `hibernate.jdbc.batch_size: 20` with `order_updates: true` for write performance.

## Entity Model

All entities extend `BaseEntity`, which provides:

```java
@Id @GeneratedValue(strategy = GenerationType.UUID)
private String id;

@CreatedDate   private LocalDateTime createdDate;
@LastModifiedDate private LocalDateTime lastModifiedDate;
@CreatedBy     private String createdBy;
@LastModifiedBy  private String lastModifiedBy;
```

Audit fields are populated by Spring Data JPA auditing (`@EnableJpaAuditing` + `AuditingEntityListener`).

## Domain Areas

### Authentication & Users

```mermaid
erDiagram
    USERS {
        uuid id PK
        string user_email UK
        string user_password
        boolean user_enabled
        string telegram_chat_id UK
    }
    ROLES {
        uuid id PK
        string name UK
    }
    USER_ROLES {
        uuid user_id FK
        uuid role_id FK
    }
    VERIFICATION_TOKENS {
        uuid id PK
        string token UK
        string token_type
        timestamp expires_at
        boolean used
        uuid user_id FK
    }
    USERS ||--o{ USER_ROLES : "has roles"
    ROLES ||--o{ USER_ROLES : "granted to"
    USERS ||--o{ VERIFICATION_TOKENS : "owns"
```

- **User** is the central identity entity. One user can have one `CLIENT_PROFILES` and/or one `TRAINER_PROFILES` (both optional, via `@OneToOne`).
- **Roles**: CLIENT, TRAINER, ADMIN. Assigned at registration (default: CLIENT).
- **VerificationToken**: Supports `EMAIL_VERIFICATION` and `PASSWORD_RESET` token types. TTL-based expiration with `used` flag.

### User Profiles

```mermaid
erDiagram
    CLIENT_PROFILES {
        uuid id PK
        uuid user_id FK
        string first_name
        string last_name
        string phone
        date birthdate
        decimal height
        decimal weight
        integer daily_water_target
        string gender
        boolean active
    }
    TRAINER_PROFILES {
        uuid id PK
        uuid user_id FK
        string first_name
        string last_name
        integer experience_years
        string description
        boolean active
    }
    SPECIALIZATIONS {
        uuid id PK
        string name UK
        string description
        boolean active
    }
    TRAINER_SPECIALIZATION {
        uuid trainer_id FK
        uuid specialization_id FK
    }
    USERS ||--o| CLIENT_PROFILES : "has client profile"
    USERS ||--o| TRAINER_PROFILES : "has trainer profile"
    TRAINER_PROFILES ||--o{ TRAINER_SPECIALIZATION : "has"
    SPECIALIZATIONS ||--o{ TRAINER_SPECIALIZATION : "assigned to"
```

- Client and trainer profiles are separate entities linked 1:1 to `User`. This allows a single user to theoretically hold both roles.
- **Specialization** is a many-to-many relationship with `TrainerProfile`.

### Training & Workouts

```mermaid
erDiagram
    TRAINING_SESSIONS {
        uuid id PK
        uuid trainer_id FK
        string training_type
        string training_status
        timestamp start_time
        timestamp end_time
        integer max_participants
    }
    TRAINING_CLIENT {
        uuid training_id FK
        uuid client_id FK
    }
    SESSION_WAITLIST {
        uuid id PK
        uuid session_id FK
        uuid client_id FK
        string status
        integer position
    }
    ATTENDANCES {
        uuid id PK
        uuid client_id FK
        uuid session_id FK
        timestamp check_in_time
    }
    EXERCISES {
        uuid id PK
        string name
        string category
        string primary_muscle_group
        boolean active
    }
    EXERCISE_SECONDARY_MUSCLES {
        uuid exercise_id FK
        string muscle_group
    }
    WORKOUT_PLANS {
        uuid id PK
        uuid trainer_id FK
        string name
        string difficulty_level
        boolean active
    }
    WORKOUT_PLAN_EXERCISES {
        uuid id PK
        uuid workout_plan_id FK
        uuid exercise_id FK
        integer day_number
        integer exercise_order
    }
    CLIENT_WORKOUT_PLANS {
        uuid id PK
        uuid client_id FK
        uuid workout_plan_id FK
        string status
    }
    WORKOUT_LOGS {
        uuid id PK
        uuid client_workout_plan_id FK
        uuid exercise_id FK
        date workout_date
    }
    TRAINING_SESSIONS ||--o{ TRAINING_CLIENT : "includes clients"
    CLIENT_PROFILES ||--o{ TRAINING_CLIENT : "joins sessions"
    TRAINING_SESSIONS ||--o{ SESSION_WAITLIST : "queues"
    TRAINING_SESSIONS ||--o{ ATTENDANCES : "records"
    WORKOUT_PLANS ||--o{ WORKOUT_PLAN_EXERCISES : "contains"
    EXERCISES ||--o{ WORKOUT_PLAN_EXERCISES : "used by"
    CLIENT_PROFILES ||--o{ CLIENT_WORKOUT_PLANS : "assigned"
    CLIENT_WORKOUT_PLANS ||--o{ WORKOUT_LOGS : "logs"
```

**Training sessions** are the core scheduling unit. A session has a trainer, time range, capacity, and status (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED). Clients join sessions via the `training_client` join table, with overflow going to `session_waitlist`.

**Exercises** are a catalog with primary and secondary muscle groups. **Workout plans** are templates created by trainers, containing ordered exercises per day. Plans are assigned to clients via `client_workout Plans`, and execution is tracked in `workout_logs`.

### Memberships & Payments

```mermaid
erDiagram
    MEMBERSHIPS {
        uuid id PK
        uuid client_id FK
        string membership_type
        string membership_status
        timestamp start_date
        timestamp end_date
        integer visits_left
        timestamp freeze_date
        integer duration_months
    }
    PAYMENTS {
        uuid id PK
        uuid client_id FK
        uuid membership_id FK
        decimal amount
        string payment_status
        string payment_currency
        string transaction_hash
        timestamp transaction_date
    }
    CLIENT_PROFILES ||--o{ MEMBERSHIPS : "owns"
    CLIENT_PROFILES ||--o{ PAYMENTS : "makes"
    MEMBERSHIPS ||--o{ PAYMENTS : "paid by"
```

Membership types include `MONTHLY`, `QUARTERLY`, `YEARLY`, and `VISIT_BASED`. Statuses track lifecycle: `ACTIVE`, `EXPIRED`, `FROZEN`, `CANCELLED`. Freeze functionality pauses the membership with a `freeze_date` reference.

Payments support fiat and crypto (TRON) via `transaction_hash` and `payment_currency` fields.

### Nutrition

```mermaid
erDiagram
    FOODS {
        uuid id PK
        string name
        string barcode
        boolean active
    }
    MEAL_PLANS {
        uuid id PK
        uuid client_id FK
        date plan_date
        integer target_calories
    }
    MEALS {
        uuid id PK
        uuid meal_plan_id FK
        string meal_type
        boolean completed
    }
    MEAL_FOODS {
        uuid id PK
        uuid meal_id FK
        uuid food_id FK
        decimal quantity
    }
    WATER_INTAKE {
        uuid id PK
        uuid client_id FK
        date intake_date
        integer amount_ml
    }
    CLIENT_PROFILES ||--o{ MEAL_PLANS : "owns"
    MEAL_PLANS ||--o{ MEALS : "contains"
    MEALS ||--o{ MEAL_FOODS : "includes"
    FOODS ||--o{ MEAL_FOODS : "used by"
    CLIENT_PROFILES ||--o{ WATER_INTAKE : "logs"
```

Meal plans are daily, containing multiple meals (BREAKFAST, LUNCH, DINNER, SNACK). Each meal links to foods with a quantity. Water intake is tracked separately per day.

### Progress Tracking

```mermaid
erDiagram
    BODY_MEASUREMENTS {
        uuid id PK
        uuid client_id FK
        date measurement_date
        decimal weight
    }
    BODY_MEASUREMENT_DETAILS {
        uuid id PK
        uuid measurement_id FK
        string measurement_type
        decimal value
    }
    PROGRESS_PHOTOS {
        uuid id PK
        uuid client_id FK
        uuid measurement_id FK
        string photo_url
        date photo_date
    }
    GOALS {
        uuid id PK
        uuid client_id FK
        string goal_type
        string status
        decimal target_value
        decimal current_value
    }
    PERSONAL_RECORDS {
        uuid id PK
        uuid client_id FK
        uuid exercise_id FK
        string record_type
        decimal value
    }
    CLIENT_PROFILES ||--o{ BODY_MEASUREMENTS : "tracks"
    BODY_MEASUREMENTS ||--o{ BODY_MEASUREMENT_DETAILS : "details"
    CLIENT_PROFILES ||--o{ PROGRESS_PHOTOS : "uploads"
    BODY_MEASUREMENTS ||--o{ PROGRESS_PHOTOS : "links"
    CLIENT_PROFILES ||--o{ GOALS : "sets"
    CLIENT_PROFILES ||--o{ PERSONAL_RECORDS : "records"
    EXERCISES ||--o{ PERSONAL_RECORDS : "measured for"
```

Body measurements are snapshots with a weight and detailed breakdown (chest, waist, arms, etc.) via `body_measurement_details`. Progress photos can link to a specific measurement. Goals track target vs. current values. Personal records are exercise-specific PRs.

### Notifications

```mermaid
erDiagram
    NOTIFICATIONS {
        uuid id PK
        uuid recipient_id FK
        string notification_type
        string priority
        boolean is_read
        boolean sent
        string title
        string message
    }
    EMAIL_FAILURE_LOG {
        uuid id PK
        string recipient_email
        string email_type
        integer retry_count
        timestamp next_retry_at
    }
    USERS ||--o{ NOTIFICATIONS : "receives"
```

Notifications support multiple types (SESSION_REMINDER, MEMBERSHIP_EXPIRING, PAYMENT_RECEIVED, etc.) and priorities (LOW, MEDIUM, HIGH, URGENT). The `sent` flag tracks delivery status. Failed emails are logged to `email_failure_log` for retry.

### Reviews

```mermaid
erDiagram
    TRAINER_REVIEWS {
        uuid id PK
        uuid client_id FK
        uuid trainer_id FK
        integer rating
        string comment
        boolean is_visible
        string moderation_status
    }
    CLIENT_PROFILES ||--o{ TRAINER_REVIEWS : "writes"
    TRAINER_PROFILES ||--o{ TRAINER_REVIEWS : "receives"
```

Reviews include moderation support: `is_visible` controls public visibility, and `moderation_status` tracks PENDING/APPROVED/REJECTED states.

## Query Optimization

Entities declare composite indexes aligned with common query patterns:

- `idx_membership_client_status` — client's active memberships lookup
- `idx_session_trainer_start_status` — trainer's upcoming sessions
- `idx_session_status_end` — expiration job queries
- `idx_user_email` (unique) — authentication lookups
- `idx_training_client_training` / `idx_training_client_client` — session participant queries
