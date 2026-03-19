CREATE TABLE trainer_reviews
(
    id                     VARCHAR(36)  NOT NULL,

    client_id              VARCHAR(36)  NOT NULL,
    trainer_id             VARCHAR(36)  NOT NULL,

    rating                 INTEGER      NOT NULL,
    comment                TEXT,

    professionalism_rating INTEGER,
    knowledge_rating       INTEGER,
    communication_rating   INTEGER,
    motivation_rating      INTEGER,

    visible                BOOLEAN      NOT NULL DEFAULT TRUE,
    edited                 BOOLEAN      NOT NULL DEFAULT FALSE,
    edited_at              TIMESTAMP,

    created_date           TIMESTAMP    NOT NULL,
    last_modified_date     TIMESTAMP,
    created_by             VARCHAR(255) NOT NULL,
    last_modified_by       VARCHAR(255),

    CONSTRAINT pk_trainer_reviews PRIMARY KEY (id),

    CONSTRAINT fk_review_client
        FOREIGN KEY (client_id)
            REFERENCES client_profiles (id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_review_trainer
        FOREIGN KEY (trainer_id)
            REFERENCES trainer_profiles (id)
            ON DELETE RESTRICT,

    CONSTRAINT uk_review_client_trainer
        UNIQUE (client_id, trainer_id)
);

-- indexes
CREATE INDEX idx_review_trainer ON trainer_reviews (trainer_id);
CREATE INDEX idx_review_client ON trainer_reviews (client_id);
CREATE INDEX idx_review_rating ON trainer_reviews (rating);
CREATE INDEX idx_review_created ON trainer_reviews (created_date DESC);