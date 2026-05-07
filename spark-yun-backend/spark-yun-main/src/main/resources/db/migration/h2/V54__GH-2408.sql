-- 扩大数据源配置字段长度
alter table SY_DATASOURCE
    alter column METASTORE_URIS varchar(500) null comment 'hive数据源 hive.metastore.uris 配置';

alter table SY_DATASOURCE
    alter column FE_NODES varchar(500) null;
