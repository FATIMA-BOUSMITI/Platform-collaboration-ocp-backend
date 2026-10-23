CREATE TABLE IF NOT EXISTS project_invitations(id uuid PRIMARY KEY,project_id uuid NOT NULL REFERENCES project(id) ON DELETE CASCADE,user_id uuid NOT NULL,invited_by uuid NOT NULL,status varchar(20) NOT NULL,created_at timestamp NOT NULL,responded_at timestamp,version bigint NOT NULL DEFAULT 0);
CREATE INDEX IF NOT EXISTS idx_project_invitations_user ON project_invitations(user_id,status,created_at DESC);
CREATE INDEX IF NOT EXISTS idx_project_invitations_project ON project_invitations(project_id,user_id,status);
ALTER TABLE task_comment ALTER COLUMN content TYPE text;
