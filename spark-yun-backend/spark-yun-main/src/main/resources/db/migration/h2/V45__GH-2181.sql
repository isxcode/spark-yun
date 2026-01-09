-- 新增Spark ETL配置字段
alter table SY_WORK_CONFIG
    add SPARK_ETL_CONFIG longtext;

-- 版本表中添加Spark ETL配置字段
alter table SY_WORK_VERSION
    add SPARK_ETL_CONFIG longtext;