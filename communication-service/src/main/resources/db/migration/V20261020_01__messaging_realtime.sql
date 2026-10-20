ALTER TABLE conversation_members ADD COLUMN IF NOT EXISTS last_read_at timestamp;
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS project_id uuid;
CREATE TABLE IF NOT EXISTS message_attachments(id uuid PRIMARY KEY,message_id uuid NOT NULL REFERENCES messages(id),conversation_id uuid NOT NULL REFERENCES conversations(id),original_name varchar(255) NOT NULL,stored_name varchar(255) NOT NULL,content_type varchar(255) NOT NULL,size bigint NOT NULL);
CREATE INDEX IF NOT EXISTS idx_message_attachments_message ON message_attachments(message_id);
CREATE INDEX IF NOT EXISTS idx_messages_conversation_time ON messages(conversation_id,sent_at);
CREATE INDEX IF NOT EXISTS idx_conversation_members_user ON conversation_members(user_id,conversation_id);
