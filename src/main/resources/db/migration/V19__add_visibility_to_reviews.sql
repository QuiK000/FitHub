ALTER TABLE trainer_reviews
    ADD COLUMN is_visible BOOLEAN DEFAULT TRUE NOT NULL,
    ADD COLUMN moderated_by_admin_id VARCHAR(255),
    ADD COLUMN moderated_at TIMESTAMP,
    ADD COLUMN hidden_reason VARCHAR(500);

CREATE INDEX idx_trainer_reviews_visible ON trainer_reviews(trainer_id, is_visible);