CREATE TABLE IF NOT EXISTS collaboration_notifications(id uuid PRIMARY KEY,external_id uuid UNIQUE NOT NULL,user_id uuid NOT NULL,type varchar(40) NOT NULL,title varchar(150) NOT NULL,body varchar(500) NOT NULL,link varchar(500) NOT NULL,is_read boolean NOT NULL DEFAULT false,created_at timestamp NOT NULL);
CREATE INDEX IF NOT EXISTS idx_collaboration_notifications_user_time ON collaboration_notifications(user_id,created_at DESC);
CREATE INDEX IF NOT EXISTS idx_collaboration_notifications_unread ON collaboration_notifications(user_id,is_read);
CREATE TABLE IF NOT EXISTS notification_preferences(user_id uuid PRIMARY KEY,messages boolean NOT NULL DEFAULT true,tasks boolean NOT NULL DEFAULT true,deadlines boolean NOT NULL DEFAULT true);
