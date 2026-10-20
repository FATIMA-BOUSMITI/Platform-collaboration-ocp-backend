-- Execute against organization_db if automatic schema updates are disabled.
ALTER TABLE teams ADD COLUMN IF NOT EXISTS objective varchar(255);
ALTER TABLE teams ADD COLUMN IF NOT EXISTS active boolean DEFAULT true;
UPDATE teams SET active = true WHERE active IS NULL;
CREATE INDEX IF NOT EXISTS idx_team_members_team_id ON team_members(team_id);
