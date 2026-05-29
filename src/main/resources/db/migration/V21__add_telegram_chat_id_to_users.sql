ALTER TABLE users ADD COLUMN telegram_chat_id VARCHAR (255) UNIQUE;
CREATE INDEX idx_user_telegram_chat_id ON users(telegram_chat_id);