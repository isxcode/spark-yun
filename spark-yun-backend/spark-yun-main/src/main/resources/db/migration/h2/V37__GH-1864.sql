-- 新增整库迁移配置字段
alter table SY_WORK_CONFIG
    add DB_MIGRATE_CONFIG longtext;

-- 版本表中添加DB_MIGRATE_CONFIG字段
alter table SY_WORK_VERSION
    add DB_MIGRATE_CONFIG longtext;