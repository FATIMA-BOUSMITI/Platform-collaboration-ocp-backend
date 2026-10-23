CREATE TABLE IF NOT EXISTS board_columns(id uuid PRIMARY KEY,project_id uuid NOT NULL REFERENCES project(id),name varchar(100) NOT NULL,status varchar(32) NOT NULL,position integer NOT NULL);
CREATE INDEX IF NOT EXISTS idx_board_columns_project ON board_columns(project_id,position);
ALTER TABLE task ADD COLUMN IF NOT EXISTS column_id uuid REFERENCES board_columns(id);
ALTER TABLE task ADD COLUMN IF NOT EXISTS sort_index bigint NOT NULL DEFAULT 0;
ALTER TABLE task ADD COLUMN IF NOT EXISTS version bigint NOT NULL DEFAULT 0;
UPDATE task SET version=0 WHERE version IS NULL;
ALTER TABLE task ALTER COLUMN assignee_id DROP NOT NULL;
ALTER TABLE task ALTER COLUMN due_date DROP NOT NULL;
CREATE INDEX IF NOT EXISTS idx_task_column_order ON task(project_id,column_id,sort_index);
