-- 用户表添加有效期字段
ALTER TABLE SY_USER
    ADD COLUMN valid_start_date_time DATETIME NULL;

ALTER TABLE SY_USER
    ADD COLUMN valid_end_date_time DATETIME NULL;

-- 租户表添加有效期字段
ALTER TABLE SY_TENANT
    ADD COLUMN valid_start_date_time DATETIME NULL;

ALTER TABLE SY_TENANT
    ADD COLUMN valid_end_date_time DATETIME NULL;