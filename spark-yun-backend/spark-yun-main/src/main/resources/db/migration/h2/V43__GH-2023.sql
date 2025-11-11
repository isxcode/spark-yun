-- 添加query作业配置
alter table SY_WORK_CONFIG
    add QUERY_CONFIG longtext;

-- 实例表中添加QUERY_CONFIG字段
alter table SY_WORK_VERSION
    add QUERY_CONFIG longtext;

-- 添加oracle 11c驱动
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time,
                                last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark,
                                is_default_driver)
VALUES ('oracle_11g', 'oracle_11g', 'ORACLE', 'ojdbc6.jar', 'SYSTEM_DRIVER',
        'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动',
        0);