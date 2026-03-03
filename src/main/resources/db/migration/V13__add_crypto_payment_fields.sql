ALTER TABLE payments ADD COLUMN transaction_hash VARCHAR(255);
CREATE INDEX idx_payment_tx_hash ON payments(transaction_hash);