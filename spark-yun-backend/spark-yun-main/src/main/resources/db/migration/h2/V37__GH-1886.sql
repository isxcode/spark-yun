-- 新增ck驱动
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time,
                                last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark,
                                is_default_driver)
VALUES ('clickhouse_0.8.2', 'clickhouse_0.8.2', 'CLICKHOUSE', 'clickhouse-jdbc-0.8.2-shaded-all.jar', 'SYSTEM_DRIVER',
        'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动',
        1);

-- 删除旧的驱动
DELETE
FROM SY_DATABASE_DRIVER
WHERE id = 'clickhouse_0.5.0';

-- 修复数据
UPDATE SY_DATASOURCE
SET DRIVER_ID = 'clickhouse_0.8.2'
WHERE DRIVER_ID = 'clickhouse_0.5.0';
