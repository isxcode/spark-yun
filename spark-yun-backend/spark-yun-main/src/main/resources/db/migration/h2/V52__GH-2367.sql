-- 新增接口采集作业同步配置字段
alter table SY_WORK_CONFIG
    add API_SYNC_CONFIG longtext;

-- 新增接口采集作业同步配置字段
alter table SY_WORK_VERSION
    add API_SYNC_CONFIG longtext;
