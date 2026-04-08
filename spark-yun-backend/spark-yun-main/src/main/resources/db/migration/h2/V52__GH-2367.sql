-- 新增Flink数据同步配置字段
alter table SY_WORK_CONFIG
    add API_SYNC_CONFIG longtext;

-- 版本表中添加新增Flink数据同步配置字段
alter table SY_WORK_VERSION
    add API_SYNC_CONFIG longtext;
