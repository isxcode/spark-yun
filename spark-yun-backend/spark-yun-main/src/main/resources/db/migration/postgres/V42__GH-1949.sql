-- 添加impala驱动
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time,
                                last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark,
                                is_default_driver)
VALUES ('impala_hive_uber_2.6.3', 'impala_hive_uber_2.6.3', 'IMPALA', 'hive-jdbc-uber-2.6.3.0-235.jar',
        'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun',
        '系统自带驱动', true);

-- 添加presto驱动
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time,
                                last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark,
                                is_default_driver)
VALUES ('presto-jdbc-0.295', 'presto-jdbc-0.295', 'PRESTO', 'presto-jdbc-0.295.jar',
        'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun',
        '系统自带驱动', true);

-- 添加trino驱动
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time,
                                last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark,
                                is_default_driver)
VALUES ('trino-jdbc-418', 'trino-jdbc-418', 'TRINO', 'trino-jdbc-418.jar',
        'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun',
        '系统自带驱动', true);
