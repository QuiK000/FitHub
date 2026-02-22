CREATE TABLE specializations
(
    id          VARCHAR(36)  NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_specializations PRIMARY KEY (id)
);

CREATE TABLE client_profiles
(
    id                 VARCHAR(36)  NOT NULL,
    first_name         VARCHAR(255),
    last_name          VARCHAR(255),
    phone              VARCHAR(50),
    birth_date         DATE,
    height             DOUBLE PRECISION,
    weight             DOUBLE PRECISION,
    daily_water_target INTEGER,
    client_gender      VARCHAR(20),
    active             BOOLEAN      NOT NULL DEFAULT TRUE,
    user_id            VARCHAR(36)  NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_client_profiles PRIMARY KEY (id),
    CONSTRAINT uq_client_user UNIQUE (user_id),
    CONSTRAINT uq_client_phone UNIQUE (phone),
    CONSTRAINT fk_client_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_client_user ON client_profiles (user_id);
CREATE INDEX idx_client_active ON client_profiles (active);
CREATE INDEX idx_client_names ON client_profiles (last_name, first_name);
CREATE INDEX idx_client_phone ON client_profiles (phone);

CREATE TABLE trainer_profiles
(
    id                 VARCHAR(36)  NOT NULL,
    first_name         VARCHAR(255),
    last_name          VARCHAR(255),
    experience_years   INTEGER,
    description        VARCHAR(1000),
    active             BOOLEAN      NOT NULL DEFAULT TRUE,
    user_id            VARCHAR(36)  NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_trainer_profiles PRIMARY KEY (id),
    CONSTRAINT uq_trainer_user UNIQUE (user_id),
    CONSTRAINT fk_trainer_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_trainer_user ON trainer_profiles (user_id);
CREATE INDEX idx_trainer_active ON trainer_profiles (active);
CREATE INDEX idx_trainer_names ON trainer_profiles (last_name, first_name);
CREATE INDEX idx_trainer_experience ON trainer_profiles (experience_years);

CREATE TABLE trainer_specialization
(
    trainer_id        VARCHAR(36) NOT NULL,
    specialization_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_trainer_specialization PRIMARY KEY (trainer_id, specialization_id),
    CONSTRAINT fk_ts_trainer FOREIGN KEY (trainer_id) REFERENCES trainer_profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_ts_spec FOREIGN KEY (specialization_id) REFERENCES specializations (id) ON DELETE CASCADE
);

CREATE INDEX idx_trainer_spec_trainer ON trainer_specialization (trainer_id);
CREATE INDEX idx_trainer_spec_spec ON trainer_specialization (specialization_id);

