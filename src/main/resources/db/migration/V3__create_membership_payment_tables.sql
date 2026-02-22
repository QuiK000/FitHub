CREATE TABLE memberships
(
    id                 VARCHAR(36)  NOT NULL,
    membership_type    VARCHAR(50)  NOT NULL,
    membership_status  VARCHAR(50)  NOT NULL,
    start_date         TIMESTAMP,
    end_date           TIMESTAMP,
    visits_left        INTEGER,
    freeze_date        TIMESTAMP,
    duration_months    INTEGER,
    client_id          VARCHAR(36)  NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_memberships PRIMARY KEY (id),
    CONSTRAINT fk_memberships_client FOREIGN KEY (client_id) REFERENCES client_profiles (id) ON DELETE RESTRICT
);

CREATE INDEX idx_membership_client_status ON memberships (client_id, membership_status);
CREATE INDEX idx_membership_end_date ON memberships (end_date);
CREATE INDEX idx_membership_status_freeze ON memberships (membership_status, freeze_date);
CREATE INDEX idx_membership_type ON memberships (membership_type);
CREATE INDEX idx_membership_client_created ON memberships (client_id, created_date DESC);

CREATE TABLE payments
(
    id                 VARCHAR(36)    NOT NULL,
    amount             DECIMAL(19, 4) NOT NULL,
    currency           VARCHAR(10)    NOT NULL,
    payment_status     VARCHAR(50)    NOT NULL,
    payment_date       TIMESTAMP      NOT NULL,
    client_id          VARCHAR(36)    NOT NULL,
    membership_id      VARCHAR(36),
    created_date       TIMESTAMP      NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255)   NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_payments PRIMARY KEY (id),
    CONSTRAINT fk_payments_client FOREIGN KEY (client_id) REFERENCES client_profiles (id) ON DELETE RESTRICT,
    CONSTRAINT fk_payments_membership FOREIGN KEY (membership_id) REFERENCES memberships (id) ON DELETE SET NULL
);