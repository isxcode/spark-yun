-- 插入h2驱动
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time,
                                last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark,
                                is_default_driver)
VALUES ('h2-2.2.224', 'h2-2.2.224', 'H2', 'h2-2.2.224.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34',
        'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 0);
