ALTER TABLE sy_locker ADD COLUMN lock_owner VARCHAR(200);
ALTER TABLE sy_locker ADD COLUMN expire_time TIMESTAMP;
ALTER TABLE sy_locker ADD COLUMN create_date_time TIMESTAMP;
