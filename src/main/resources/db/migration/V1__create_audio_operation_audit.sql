CREATE TABLE IF NOT EXISTS transaction_entity (
    id BINARY(16) NOT NULL PRIMARY KEY,
    description VARCHAR(255),
    amount BIGINT NOT NULL,
    category VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS audio_operation_audit (
    id BINARY(16) NOT NULL PRIMARY KEY,
    occurred_at TIMESTAMP(6) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    source_filename VARCHAR(255),
    storage_key VARCHAR(255) NOT NULL,
    sha256 CHAR(64) NOT NULL,
    channel VARCHAR(64) NOT NULL,
    content_type VARCHAR(255),
    file_size BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    failure_code VARCHAR(255)
);

CREATE INDEX idx_audio_audit_user_time ON audio_operation_audit (user_id, occurred_at);
CREATE INDEX idx_audio_audit_hash ON audio_operation_audit (sha256);

CREATE TABLE IF NOT EXISTS audio_transaction_link (
    id BINARY(16) NOT NULL PRIMARY KEY,
    audio_audit_id BINARY(16) NOT NULL,
    transaction_id BINARY(16) NOT NULL,
    linked_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_audio_link_audit FOREIGN KEY (audio_audit_id) REFERENCES audio_operation_audit (id),
    CONSTRAINT fk_audio_link_transaction FOREIGN KEY (transaction_id) REFERENCES transaction_entity (id)
);

CREATE INDEX idx_audio_link_transaction ON audio_transaction_link (transaction_id);