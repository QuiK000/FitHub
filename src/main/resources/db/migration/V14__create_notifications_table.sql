-- ============================================================
-- V14: Create notifications table
-- ============================================================

CREATE TABLE notifications
(
    id                 VARCHAR(36)  NOT NULL,
    recipient_id       VARCHAR(36)  NOT NULL,
    notification_type  VARCHAR(100) NOT NULL,
    priority           VARCHAR(50)  NOT NULL,
    title              VARCHAR(200) NOT NULL,
    message            TEXT         NOT NULL,
    is_read            BOOLEAN      NOT NULL DEFAULT FALSE,
    read_at            TIMESTAMP,
    action_url         VARCHAR(512),
    reference_id       VARCHAR(36),
    reference_type     VARCHAR(100),
    scheduled_for      TIMESTAMP,
    sent               BOOLEAN      NOT NULL DEFAULT FALSE,
    sent_at            TIMESTAMP,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT fk_notification_recipient FOREIGN KEY (recipient_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_notification_recipient ON notifications (recipient_id);
CREATE INDEX idx_notification_read ON notifications (is_read);
CREATE INDEX idx_notification_created ON notifications (created_date DESC);
CREATE INDEX idx_notification_type ON notifications (notification_type);
CREATE INDEX idx_notification_sent ON notifications (sent);
CREATE INDEX idx_notification_scheduled ON notifications (scheduled_for);