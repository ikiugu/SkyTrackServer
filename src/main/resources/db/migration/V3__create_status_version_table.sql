CREATE TABLE status_version (
    flight_number VARCHAR(20) PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_status_version_updated ON status_version(updated_at);

