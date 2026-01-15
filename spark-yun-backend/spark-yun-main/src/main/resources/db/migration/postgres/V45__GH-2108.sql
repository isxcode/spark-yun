-- 用户表添加有效期字段
ALTER TABLE SY_USER
    ADD COLUMN valid_start_date_time TIMESTAMP;

ALTER TABLE SY_USER
    ADD COLUMN valid_end_date_time TIMESTAMP;

-- 租户表添加有效期字段
ALTER TABLE SY_TENANT
    ADD COLUMN valid_start_date_time TIMESTAMP;

ALTER TABLE SY_TENANT
    ADD COLUMN valid_end_date_time TIMESTAMP;