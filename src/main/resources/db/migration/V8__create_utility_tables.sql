CREATE TABLE email_failure_log
(
    id                 VARCHAR(36)  NOT NULL,
    recipient_email    VARCHAR(255) NOT NULL,
    email_type         VARCHAR(50)  NOT NULL,
    email_content      TEXT,
    failure_reason     VARCHAR(255) NOT NULL,
    error_message      TEXT,
    attempt_count      INTEGER      NOT NULL DEFAULT 0,
    retry_scheduled    BOOLEAN      NOT NULL DEFAULT FALSE,
    next_retry_at      TIMESTAMP,
    last_attempt_at    TIMESTAMP,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_email_failure_log PRIMARY KEY (id)
);