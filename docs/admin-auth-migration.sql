-- Execute against auth_db if automatic schema updates are disabled.
CREATE TABLE IF NOT EXISTS admin_preferences (id uuid PRIMARY KEY, language varchar(255), timezone varchar(255));
CREATE TABLE IF NOT EXISTS admin_notices (id uuid PRIMARY KEY, owner uuid NOT NULL, message varchar(255) NOT NULL, created_at timestamp, is_read boolean NOT NULL DEFAULT false);
CREATE INDEX IF NOT EXISTS idx_admin_notices_owner ON admin_notices(owner, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_active ON refresh_tokens(revoked, expiry_date);
