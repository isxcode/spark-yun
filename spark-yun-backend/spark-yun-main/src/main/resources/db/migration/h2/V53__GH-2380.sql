-- 扩大数据模型字段
alter table SY_DATA_MODEL
    alter column BUILD_LOG text;

alter table SY_DATA_MODEL
    alter column TABLE_CONFIG text;