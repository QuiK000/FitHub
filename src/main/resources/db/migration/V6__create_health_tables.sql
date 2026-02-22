CREATE TABLE body_measurements
(
    id                    VARCHAR(36)  NOT NULL,
    client_id             VARCHAR(36)  NOT NULL,
    measurement_date      TIMESTAMP    NOT NULL,
    weight                DOUBLE PRECISION,
    body_fat_percentage   DOUBLE PRECISION,
    muscle_mass           DOUBLE PRECISION,
    bmi                   DOUBLE PRECISION,
    bmr                   INTEGER,
    body_water_percentage DOUBLE PRECISION,
    body_mass             DOUBLE PRECISION,
    visceral_fat_level    INTEGER,
    notes                 TEXT,
    photo_url             VARCHAR(512),
    created_date          TIMESTAMP    NOT NULL,
    last_modified_date    TIMESTAMP,
    created_by            VARCHAR(255) NOT NULL,
    last_modified_by      VARCHAR(255),
    CONSTRAINT pk_body_measurements PRIMARY KEY (id),
    CONSTRAINT fk_bm_client FOREIGN KEY (client_id) REFERENCES client_profiles (id) ON DELETE RESTRICT
);

CREATE TABLE body_measurement_details
(
    measurement_id   VARCHAR(36) NOT NULL,
    measurement_type SMALLINT    NOT NULL,
    value            DOUBLE PRECISION,
    CONSTRAINT fk_bmd_measurement FOREIGN KEY (measurement_id) REFERENCES body_measurements (id) ON DELETE CASCADE
);

CREATE TABLE progress_photos
(
    id                 VARCHAR(36)  NOT NULL,
    client_id          VARCHAR(36)  NOT NULL,
    photo_date         TIMESTAMP    NOT NULL,
    photo_url          VARCHAR(512) NOT NULL,
    angle              VARCHAR(50),
    notes              TEXT,
    measurement_id     VARCHAR(36),
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_progress_photos PRIMARY KEY (id),
    CONSTRAINT fk_pp_client FOREIGN KEY (client_id) REFERENCES client_profiles (id) ON DELETE RESTRICT,
    CONSTRAINT fk_pp_measurement FOREIGN KEY (measurement_id) REFERENCES body_measurements (id) ON DELETE SET NULL
);

CREATE INDEX idx_photo_client_date ON progress_photos (client_id, photo_date DESC);

CREATE TABLE goals
(
    id                  VARCHAR(36)  NOT NULL,
    client_id           VARCHAR(36)  NOT NULL,
    title               VARCHAR(200) NOT NULL,
    description         TEXT,
    goal_type           VARCHAR(50)  NOT NULL,
    start_value         DOUBLE PRECISION,
    target_value        DOUBLE PRECISION,
    current_value       DOUBLE PRECISION,
    unit                VARCHAR(50),
    start_date          TIMESTAMP    NOT NULL,
    target_date         TIMESTAMP,
    completion_date     TIMESTAMP,
    status              VARCHAR(50)  NOT NULL,
    progress_percentage DOUBLE PRECISION,
    notes               TEXT,
    version             BIGINT,
    created_date        TIMESTAMP    NOT NULL,
    last_modified_date  TIMESTAMP,
    created_by          VARCHAR(255) NOT NULL,
    last_modified_by    VARCHAR(255),
    CONSTRAINT pk_goals PRIMARY KEY (id),
    CONSTRAINT fk_goals_client FOREIGN KEY (client_id) REFERENCES client_profiles (id) ON DELETE RESTRICT
);

CREATE INDEX idx_goal_client ON goals (client_id);
CREATE INDEX idx_goal_status ON goals (status);

CREATE TABLE personal_records
(
    id                 VARCHAR(36)      NOT NULL,
    client_id          VARCHAR(36)      NOT NULL,
    exercise_id        VARCHAR(36)      NOT NULL,
    record_type        VARCHAR(50)      NOT NULL,
    value              DOUBLE PRECISION NOT NULL,
    unit               VARCHAR(50),
    record_date        TIMESTAMP        NOT NULL,
    previous_record    DOUBLE PRECISION,
    notes              TEXT,
    video_url          VARCHAR(512),
    is_current_best    BOOLEAN,
    created_date       TIMESTAMP        NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255)     NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_personal_records PRIMARY KEY (id),
    CONSTRAINT uk_active_personal_record UNIQUE (client_id, exercise_id, record_type, is_current_best),
    CONSTRAINT fk_pr_client FOREIGN KEY (client_id) REFERENCES client_profiles (id) ON DELETE RESTRICT,
    CONSTRAINT fk_pr_exercise FOREIGN KEY (exercise_id) REFERENCES exercises (id) ON DELETE RESTRICT
);

CREATE INDEX idx_pr_client_exercise ON personal_records (client_id, exercise_id);
CREATE INDEX idx_pr_date ON personal_records (record_date DESC);