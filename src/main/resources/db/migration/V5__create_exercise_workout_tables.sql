CREATE TABLE exercises
(
    id                   VARCHAR(36)  NOT NULL,
    name                 VARCHAR(200) NOT NULL,
    description          TEXT,
    category             VARCHAR(100) NOT NULL,
    primary_muscle_group VARCHAR(100) NOT NULL,
    video_url            VARCHAR(512),
    image_url            VARCHAR(512),
    instructions         TEXT,
    active               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_date         TIMESTAMP    NOT NULL,
    last_modified_date   TIMESTAMP,
    created_by           VARCHAR(255) NOT NULL,
    last_modified_by     VARCHAR(255),
    CONSTRAINT pk_exercises PRIMARY KEY (id)
);

CREATE INDEX idx_exercise_category ON exercises (category);
CREATE INDEX idx_exercise_muscle ON exercises (primary_muscle_group);
CREATE INDEX idx_exercise_active ON exercises (active);

CREATE TABLE exercise_secondary_muscles
(
    exercise_id  VARCHAR(36)  NOT NULL,
    muscle_group VARCHAR(100) NOT NULL,
    CONSTRAINT fk_esm_exercise FOREIGN KEY (exercise_id) REFERENCES exercises (id) ON DELETE CASCADE
);

CREATE TABLE workout_plans
(
    id                 VARCHAR(36)  NOT NULL,
    name               VARCHAR(200) NOT NULL,
    description        TEXT,
    difficulty_level   VARCHAR(50)  NOT NULL,
    duration_weeks     INTEGER,
    sessions_per_week  INTEGER,
    active             BOOLEAN      NOT NULL DEFAULT TRUE,
    trainer_id         VARCHAR(36)  NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_workout_plans PRIMARY KEY (id),
    CONSTRAINT fk_wp_trainer FOREIGN KEY (trainer_id) REFERENCES trainer_profiles (id) ON DELETE RESTRICT
);

CREATE INDEX idx_workout_trainer ON workout_plans (trainer_id);
CREATE INDEX idx_workout_difficulty ON workout_plans (difficulty_level);
CREATE INDEX idx_workout_active ON workout_plans (active);

CREATE TABLE workout_plan_exercises
(
    id                 VARCHAR(36)  NOT NULL,
    workout_plan_id    VARCHAR(36)  NOT NULL,
    exercise_id        VARCHAR(36)  NOT NULL,
    day_number         INTEGER      NOT NULL,
    order_index        INTEGER      NOT NULL,
    sets               INTEGER,
    reps               INTEGER,
    duration_seconds   INTEGER,
    rest_seconds       INTEGER,
    notes              TEXT,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_workout_plan_exercises PRIMARY KEY (id),
    CONSTRAINT fk_wpe_plan FOREIGN KEY (workout_plan_id) REFERENCES workout_plans (id) ON DELETE CASCADE,
    CONSTRAINT fk_wpe_exercise FOREIGN KEY (exercise_id) REFERENCES exercises (id) ON DELETE RESTRICT
);

CREATE INDEX idx_wpe_plan ON workout_plan_exercises (workout_plan_id);
CREATE INDEX idx_wpe_exercise ON workout_plan_exercises (exercise_id);
CREATE INDEX idx_wpe_day ON workout_plan_exercises (day_number);

CREATE TABLE client_workout_plans
(
    id                    VARCHAR(36)  NOT NULL,
    client_id             VARCHAR(36)  NOT NULL,
    workout_plan_id       VARCHAR(36)  NOT NULL,
    assigned_date         TIMESTAMP    NOT NULL,
    start_date            TIMESTAMP,
    end_date              TIMESTAMP,
    status                VARCHAR(50)  NOT NULL,
    completion_percentage DOUBLE PRECISION,
    created_date          TIMESTAMP    NOT NULL,
    last_modified_date    TIMESTAMP,
    created_by            VARCHAR(255) NOT NULL,
    last_modified_by      VARCHAR(255),
    CONSTRAINT pk_client_workout_plans PRIMARY KEY (id),
    CONSTRAINT fk_cwp_client FOREIGN KEY (client_id) REFERENCES client_profiles (id) ON DELETE RESTRICT,
    CONSTRAINT fk_cwp_plan FOREIGN KEY (workout_plan_id) REFERENCES workout_plans (id) ON DELETE RESTRICT
);

CREATE INDEX idx_cwp_client ON client_workout_plans (client_id);
CREATE INDEX idx_cwp_workout_plan ON client_workout_plans (workout_plan_id);
CREATE INDEX idx_cwp_status ON client_workout_plans (status);

CREATE TABLE workout_logs
(
    id                     VARCHAR(36)  NOT NULL,
    client_workout_plan_id VARCHAR(36)  NOT NULL,
    exercise_id            VARCHAR(36)  NOT NULL,
    workout_date           TIMESTAMP    NOT NULL,
    sets_completed         INTEGER,
    reps_completed         INTEGER,
    weight_used            DOUBLE PRECISION,
    duration_seconds       INTEGER,
    notes                  TEXT,
    difficulty_rating      INTEGER,
    created_date           TIMESTAMP    NOT NULL,
    last_modified_date     TIMESTAMP,
    created_by             VARCHAR(255) NOT NULL,
    last_modified_by       VARCHAR(255),
    CONSTRAINT pk_workout_logs PRIMARY KEY (id),
    CONSTRAINT fk_wlog_cwp FOREIGN KEY (client_workout_plan_id) REFERENCES client_workout_plans (id) ON DELETE RESTRICT,
    CONSTRAINT fk_wlog_exercise FOREIGN KEY (exercise_id) REFERENCES exercises (id) ON DELETE RESTRICT
);

CREATE INDEX idx_wlog_client_plan ON workout_logs (client_workout_plan_id);
CREATE INDEX idx_wlog_exercise ON workout_logs (exercise_id);
CREATE INDEX idx_wlog_date ON workout_logs (workout_date DESC);
