CREATE TABLE session_waitlist(
    id VARCHAR (36) NOT NULL,
    session_id VARCHAR(36) NOT NULL,
    client_id VARCHAR(36) NOT NULL,
    position INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    joined_at TIMESTAMP NOT NULL,

    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    last_modified_by VARCHAR(255),

    CONSTRAINT pk_session_waitlist PRIMARY KEY (id),

    CONSTRAINT fk_waitlist_session
                             FOREIGN KEY (session_id)
                             REFERENCES training_sessions (id)
                             ON DELETE CASCADE,

    CONSTRAINT fk_waitlist_client
                             FOREIGN KEY (client_id)
                             REFERENCES client_profiles (id)
                             ON DELETE CASCADE,

    CONSTRAINT uk_waitlist_session_client UNIQUE (session_id, client_id),
    CONSTRAINT uk_waitlist_session_position UNIQUE (session_id, position)
);

CREATE INDEX idx_waitlist_session_status_pos ON session_waitlist (session_id, status, position)