CREATE TABLE IF NOT EXISTS collaboration_outbox(id uuid PRIMARY KEY,dedup_key varchar(255) UNIQUE NOT NULL,payload text NOT NULL,created_at timestamp NOT NULL,next_attempt_at timestamp NOT NULL,delivered_at timestamp,attempts integer NOT NULL DEFAULT 0);
CREATE INDEX IF NOT EXISTS idx_collaboration_outbox_delivery ON collaboration_outbox(delivered_at,next_attempt_at);
