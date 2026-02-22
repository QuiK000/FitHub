CREATE TABLE training_sessions (
                                   id                  VARCHAR(36)  NOT NULL,
                                   training_type       VARCHAR(50)  NOT NULL,
                                   start_time          TIMESTAMP    NOT NULL,
                                   end_time            TIMESTAMP    NOT NULL,
                                   max_participants    INTEGER      NOT NULL,
                                   training_status     VARCHAR(50)  NOT NULL,
                                   trainer_id          VARCHAR(36)  NOT NULL,
                                   created_date        TIMESTAMP    NOT NULL,
                                   last_modified_date  TIMESTAMP,
                                   created_by          VARCHAR(255) NOT NULL,
                                   last_modified_by    VARCHAR(255),
                                   CONSTRAINT pk_training_sessions PRIMARY KEY (id),
                                   CONSTRAINT fk_ts_trainer FOREIGN KEY (trainer_id) REFERENCES trainer_profiles (id) ON DELETE RESTRICT
);

CREATE INDEX idx_session_start_time           ON training_sessions (start_time);
CREATE INDEX idx_session_trainer_status       ON training_sessions (trainer_id, training_status);
CREATE INDEX idx_session_status_end           ON training_sessions (training_status, end_time);
CREATE INDEX idx_session_type_start           ON training_sessions (training_type, start_time);
CREATE INDEX idx_session_trainer_start_status ON training_sessions (trainer_id, start_time, training_status);


CREATE TABLE training_client (
                                 training_id VARCHAR(36) NOT NULL,
                                 client_id   VARCHAR(36) NOT NULL,
                                 CONSTRAINT pk_training_client PRIMARY KEY (training_id, client_id),
                                 CONSTRAINT fk_tc_session FOREIGN KEY (training_id) REFERENCES training_sessions (id) ON DELETE CASCADE,
                                 CONSTRAINT fk_tc_client  FOREIGN KEY (client_id)   REFERENCES client_profiles   (id) ON DELETE CASCADE
);

CREATE INDEX idx_training_client_training ON training_client (training_id);
CREATE INDEX idx_training_client_client   ON training_client (client_id);

CREATE TABLE attendances (
                             id                  VARCHAR(36)  NOT NULL,
                             check_in_time       TIMESTAMP,
                             client_id           VARCHAR(36)  NOT NULL,
                             session_id          VARCHAR(36)  NOT NULL,
                             created_date        TIMESTAMP    NOT NULL,
                             last_modified_date  TIMESTAMP,
                             created_by          VARCHAR(255) NOT NULL,
                             last_modified_by    VARCHAR(255),
                             CONSTRAINT pk_attendances PRIMARY KEY (id),
                             CONSTRAINT fk_attendance_client  FOREIGN KEY (client_id)  REFERENCES client_profiles   (id) ON DELETE RESTRICT,
                             CONSTRAINT fk_attendance_session FOREIGN KEY (session_id) REFERENCES training_sessions  (id) ON DELETE RESTRICT
);

CREATE INDEX idx_attendance_client         ON attendances (client_id);
CREATE INDEX idx_attendance_session        ON attendances (session_id);
CREATE INDEX idx_attendance_checkin_time   ON attendances (check_in_time DESC);
CREATE INDEX idx_attendance_client_session ON attendances (client_id, session_id);