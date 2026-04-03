-- 新增Flink数据同步配置字段
alter table SY_WORK_CONFIG
    add SYNC_FLINK_CONFIG longtext;

-- 版本表中添加新增Flink数据同步配置字段
alter table SY_WORK_VERSION
    add SYNC_FLINK_CONFIG longtext;

-- doris数据源特有字段feNodes
alter table SY_DATASOURCE
    add FE_NODES varchar(200);