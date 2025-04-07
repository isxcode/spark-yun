-- 插入OpenGauss驱动
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time,
                                last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark,
                                is_default_driver)
VALUES ('open_gauss(postgresql_42.6.0)', 'open_gauss(postgresql_42.6.0)', 'OPEN_GAUSS', 'postgresql-42.6.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun',
        '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);
-- 插入Gauss驱动
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time,
                                last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark,
                                is_default_driver)
VALUES ('gauss(postgresql_42.6.0)', 'gauss(postgresql_42.6.0)', 'GAUSS', 'postgresql-42.6.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun',
        '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);