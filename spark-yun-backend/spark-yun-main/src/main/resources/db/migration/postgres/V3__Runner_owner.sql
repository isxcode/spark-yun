ALTER TABLE sy_work_instance ADD COLUMN runner_owner VARCHAR(200);
ALTER TABLE sy_work_instance ADD COLUMN heartbeat_date_time TIMESTAMP;
ALTER TABLE sy_meta_instance ADD COLUMN runner_owner VARCHAR(200);
ALTER TABLE sy_meta_instance ADD COLUMN heartbeat_date_time TIMESTAMP;
