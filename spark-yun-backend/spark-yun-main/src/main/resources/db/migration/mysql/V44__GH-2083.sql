-- 添加TDengine驱动
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time,
                                last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark,
                                is_default_driver)
VALUES ('taos-jdbcdriver-3.7.7-dist', 'taos-jdbcdriver-3.7.7-dist', 'T_DENGINE', 'taos-jdbcdriver-3.7.7-dist.jar', 'SYSTEM_DRIVER',
        'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动',
        1);

-- 添加duckDB驱动
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time,
                                last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark,
                                is_default_driver)
VALUES ('duckdb_jdbc-1.4.2.0', 'duckdb_jdbc-1.4.2.0', 'DUCK_DB', 'duckdb_jdbc-1.4.2.0.jar', 'SYSTEM_DRIVER',
        'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动',
        1);

-- 添加selectDB驱动
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time,
                                last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark,
                                is_default_driver)
VALUES ('selectDB_mysql_5.1.49', 'selectDB_mysql_5.1.49', 'SELECT_DB', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER',
        'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动',
        1);