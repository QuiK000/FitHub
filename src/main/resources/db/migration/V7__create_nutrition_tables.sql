CREATE TABLE foods
(
    id                   VARCHAR(36)  NOT NULL,
    name                 VARCHAR(200) NOT NULL,
    brand                VARCHAR(200),
    serving_size         DOUBLE PRECISION,
    serving_unit         VARCHAR(50),
    calories_per_serving INTEGER,
    protein_per_serving  DOUBLE PRECISION,
    carbs_per_serving    DOUBLE PRECISION,
    fats_per_serving     DOUBLE PRECISION,
    fiber_per_serving    DOUBLE PRECISION,
    sugar_per_serving    DOUBLE PRECISION,
    barcode              VARCHAR(50),
    active               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_date         TIMESTAMP    NOT NULL,
    last_modified_date   TIMESTAMP,
    created_by           VARCHAR(255) NOT NULL,
    last_modified_by     VARCHAR(255),
    CONSTRAINT pk_foods PRIMARY KEY (id)
);

CREATE INDEX idx_food_name ON foods (name);

CREATE TABLE meal_plans
(
    id                 VARCHAR(36)  NOT NULL,
    client_id          VARCHAR(36)  NOT NULL,
    plan_date          DATE         NOT NULL,
    total_calories     INTEGER,
    target_calories    INTEGER,
    protein            DOUBLE PRECISION,
    carbs              DOUBLE PRECISION,
    fats               DOUBLE PRECISION,
    fiber              DOUBLE PRECISION,
    sugar              DOUBLE PRECISION,
    target_protein     DOUBLE PRECISION,
    target_carbs       DOUBLE PRECISION,
    target_fats        DOUBLE PRECISION,
    target_fiber       DOUBLE PRECISION,
    target_sugar       DOUBLE PRECISION,
    notes              TEXT,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_meal_plans PRIMARY KEY (id),
    CONSTRAINT fk_meal_plans_client FOREIGN KEY (client_id) REFERENCES client_profiles (id) ON DELETE RESTRICT
);

CREATE INDEX idx_meal_plan_client ON meal_plans (client_id);
CREATE INDEX idx_meal_plan_date ON meal_plans (plan_date DESC);

CREATE TABLE meals
(
    id                 VARCHAR(36)  NOT NULL,
    meal_plan_id       VARCHAR(36)  NOT NULL,
    meal_type          VARCHAR(50)  NOT NULL,
    meal_time          TIMESTAMP,
    name               VARCHAR(200),
    description        TEXT,
    calories           INTEGER,
    protein            DOUBLE PRECISION,
    carbs              DOUBLE PRECISION,
    fats               DOUBLE PRECISION,
    fiber              DOUBLE PRECISION,
    sugar              DOUBLE PRECISION,
    completed          BOOLEAN      NOT NULL DEFAULT FALSE,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_meals PRIMARY KEY (id),
    CONSTRAINT fk_meals_plan FOREIGN KEY (meal_plan_id) REFERENCES meal_plans (id) ON DELETE CASCADE
);

CREATE TABLE meal_foods
(
    id                 VARCHAR(36)      NOT NULL,
    meal_id            VARCHAR(36)      NOT NULL,
    food_id            VARCHAR(36)      NOT NULL,
    servings           DOUBLE PRECISION NOT NULL,
    total_calories     INTEGER,
    total_protein      DOUBLE PRECISION,
    total_carbs        DOUBLE PRECISION,
    total_fats         DOUBLE PRECISION,
    total_fiber        DOUBLE PRECISION,
    total_sugar        DOUBLE PRECISION,
    created_date       TIMESTAMP        NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255)     NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_meal_foods PRIMARY KEY (id),
    CONSTRAINT fk_mf_meal FOREIGN KEY (meal_id) REFERENCES meals (id) ON DELETE CASCADE,
    CONSTRAINT fk_mf_food FOREIGN KEY (food_id) REFERENCES foods (id) ON DELETE RESTRICT
);


CREATE TABLE water_intake
(
    id                 VARCHAR(36)  NOT NULL,
    client_id          VARCHAR(36)  NOT NULL,
    intake_date        DATE         NOT NULL,
    amount_ml          INTEGER      NOT NULL,
    target_ml          INTEGER,
    intake_time        TIMESTAMP,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_water_intake PRIMARY KEY (id),
    CONSTRAINT fk_wi_client FOREIGN KEY (client_id) REFERENCES client_profiles (id) ON DELETE RESTRICT
);

CREATE INDEX idx_water_client_date ON water_intake (client_id, intake_date DESC);