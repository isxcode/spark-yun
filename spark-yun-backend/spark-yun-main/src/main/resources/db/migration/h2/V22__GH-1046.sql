-- 新增excel同步配置字段
alter table SY_WORK_CONFIG
    add EXCEL_SYNC_CONFIG longtext;

-- 实例表中添加EXCEL_SYNC_CONFIG字段
alter table SY_WORK_VERSION
    add EXCEL_SYNC_CONFIG longtext;
