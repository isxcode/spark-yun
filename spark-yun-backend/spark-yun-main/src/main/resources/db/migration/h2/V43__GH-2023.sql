-- 添加query作业配置
alter table SY_WORK_CONFIG
    add QUERY_CONFIG longtext;

-- 实例表中添加QUERY_CONFIG字段
alter table SY_WORK_VERSION
    add QUERY_CONFIG longtext;