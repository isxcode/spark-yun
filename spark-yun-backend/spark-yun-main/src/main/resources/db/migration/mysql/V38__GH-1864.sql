-- 新增整库迁移配置字段
alter table SY_WORK_CONFIG
    add DB_MIGRATE_CONFIG text comment '整库同步配置';

-- 版本表中添加DB_MIGRATE_CONFIG字段
alter table SY_WORKFLOW_CONFIG
    add DB_MIGRATE_CONFIG text comment '整库同步配置';