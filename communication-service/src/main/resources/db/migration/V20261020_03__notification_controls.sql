ALTER TABLE collaboration_notifications ADD COLUMN IF NOT EXISTS deleted_at timestamp;
ALTER TABLE notification_preferences ADD COLUMN IF NOT EXISTS mentions boolean NOT NULL DEFAULT true;
ALTER TABLE notification_preferences ADD COLUMN IF NOT EXISTS invitations boolean NOT NULL DEFAULT true;
CREATE INDEX IF NOT EXISTS idx_collaboration_notifications_visible ON collaboration_notifications(user_id,created_at DESC) WHERE deleted_at IS NULL;
ALTER TABLE collaboration_notifications ADD COLUMN IF NOT EXISTS version bigint NOT NULL DEFAULT 0;
