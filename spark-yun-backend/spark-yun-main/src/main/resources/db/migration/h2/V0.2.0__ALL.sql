-- 创建资源文件表
create table SY_FILE
(
  id                      varchar(200)  not null comment '文件配置唯一id'
    primary key,
  file_name               varchar(200) null comment '文件名称',
  file_size               varchar(200) null comment '文件大小',
  file_path               varchar(200) null comment '文件存储路径',
  file_type               varchar(200) null comment '文件类型',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 添加同步作业的配置
alter table SY_WORK_CONFIG
  add sync_work_config text null comment '同步作业的配置' after corn;

-- 将cluster_id和spark_config合并成cluster_config
alter table SY_WORK_CONFIG
    drop column cluster_id;

alter table SY_WORK_CONFIG
    drop column spark_config;

alter table SY_WORK_CONFIG
    add cluster_config text null comment '计算集群配置' after sync_work_config;

-- 添加数据同步规则
alter table SY_WORK_CONFIG
    add sync_rule text null comment '数据同步规则' after cluster_config;

-- 将cron扩展成cron_config
alter table SY_WORK_CONFIG ALTER COLUMN corn RENAME TO cron_config;
alter table SY_WORK_CONFIG
    alter cron_config text null comment '定时表达式';

-- 统一脚本内容
alter table SY_WORK_CONFIG ALTER COLUMN sql_script RENAME TO script;
alter table SY_WORK_CONFIG
    alter script text null comment '统一脚本内容，包括sql、bash、python脚本';

-- 添加spark_home_path
alter table SY_CLUSTER_NODE
    add spark_home_path varchar(200) null comment 'standalone模式spark的安装目录' after hadoop_home_path;;

-- 添加默认集群
alter table SY_CLUSTER
    add default_cluster boolean null default false comment '默认计算集群' after used_storage_num;

-- 作业运行实例中，添加作业运行的pid
alter table SY_WORK_INSTANCE
    add work_pid varchar(100) null comment '作业运行的进程pid' after result_data;

-- hive数据源，添加hive.metastore.uris配置项
alter table SY_DATASOURCE
    add metastore_uris varchar(200) null comment 'hive数据源 hive.metastore.uris 配置' after db_type;

-- 数据源支持驱动添加
alter table SY_DATASOURCE
    add driver_id varchar(100) not null comment '数据库驱动id' after metastore_uris;

-- 新增数据源驱动表
create table SY_DATABASE_DRIVER
(
    id                      varchar(200)  not null comment '数据源驱动唯一id'
        primary key,
    name                    varchar(200)  not null comment '数据源驱动名称',
    db_type                 varchar(200)  not null comment '数据源类型',
    file_name               varchar(500)  not null comment '驱动名称',
    driver_type             varchar(200)  not null comment '驱动类型',
    is_default_driver       boolean       not null comment '是否为默认驱动',
    remark                  varchar(500)  null comment '描述',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);

-- 初始化系统驱动
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('clickhouse_0.5.0', 'clickhouse_0.5.0', 'CLICKHOUSE', 'clickhouse-jdbc-0.5.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('db2_11.5.8.0', 'db2_11.5.8.0', 'DB2', 'jcc-11.5.8.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('dm_8.1.1.49', 'dm_8.1.1.49', 'DM', 'Dm8JdbcDriver18-8.1.1.49.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('doris_mysql_5.1.49', 'doris_mysql_5.1.49', 'DORIS', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('hana_2.18.13', 'hana_2.18.13', 'HANA_SAP', 'ngdbc-2.18.13.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('hive_uber_2.6.3.jar', 'hive_uber_2.6.3.jar', 'HIVE', 'hive-jdbc-uber-2.6.3.0-235.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('hive_3.1.3', 'hive_3.1.3', 'HIVE', 'hive-jdbc-3.1.3-standalone.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 0);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('mysql_5.1.49', 'mysql_5.1.49', 'MYSQL', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 0);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('mysql_8.1.0', 'mysql_8.1.0', 'MYSQL', 'mysql-connector-j-8.1.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('oceanbase_2.4.6', 'oceanbase_2.4.6', 'OCEANBASE', 'oceanbase-client-2.4.6.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('oracle_19.20.0.0', 'oracle_19.20.0.0', 'ORACLE', 'ojdbc10-19.20.0.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('postgre_42.6.0', 'postgre_42.6.0', 'POSTGRE_SQL', 'postgresql-42.6.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('sqlServer_12.4.2.jre8', 'sqlServer_12.4.2.jre8', 'SQL_SERVER', 'mssql-jdbc-12.4.2.jre8.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('star_rocks(mysql_5.1.49)', 'star_rocks(mysql_5.1.49)', 'STAR_ROCKS', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);
INSERT INTO SY_DATABASE_DRIVER (id, name, db_type, file_name, driver_type, create_by, create_date_time, last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver) VALUES ('tidb(mysql_5.1.49)', 'tidb(mysql_5.1.49)', 'TIDB', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', 1);

-- 工作流添加默认计算引擎配置
alter table SY_WORKFLOW
    add default_cluster_id varchar(200) null comment '默认计算引擎' after status;

-- 数据源日志长度扩大
alter table SY_DATASOURCE
    alter connect_log text null comment '测试连接日志';

-- SY_WORKFLOW_CONFIG将cron扩展成cron_config
alter table SY_WORKFLOW_CONFIG ALTER COLUMN corn RENAME TO cron_config;
alter table SY_WORKFLOW_CONFIG
    alter cron_config text null comment '定时表达式';

-- SY_WORK_VERSION将cron扩展成cron_config
alter table SY_WORK_VERSION ALTER COLUMN corn RENAME TO cron_config;
alter table SY_WORK_VERSION
    alter cron_config text null comment '定时表达式';

-- SY_WORK_VERSION将cluster_id改成cluster_config
alter table SY_WORK_VERSION ALTER COLUMN cluster_id RENAME TO cluster_config;
alter table SY_WORK_VERSION
    alter cluster_config text null comment '集群配置';

-- SY_WORK_VERSION将sql_script扩展成script
alter table SY_WORK_VERSION ALTER COLUMN sql_script RENAME TO script;
alter table SY_WORK_VERSION
    alter script text null comment '脚本内容';

-- 删除SY_WORK_VERSION的spark_config
alter table SY_WORK_VERSION
    drop column spark_config;

-- 添加同步作业的配置
alter table SY_WORK_VERSION
    add sync_work_config text null comment '同步作业的配置' after cron_config;

-- 添加数据同步规则
alter table SY_WORK_VERSION
    add sync_rule text null comment '数据同步规则' after sync_work_config;

-- 修改错别字
alter table SY_WORKFLOW_VERSION ALTER COLUMN corn RENAME TO cron;

-- 作业流版本里加dag图
alter table SY_WORKFLOW_VERSION
    add web_config text null comment '作业流的dag图' after cron;

-- 作业流实例添加耗时
alter table SY_WORKFLOW_INSTANCE
    add duration int null comment '耗时时间（秒）' after exec_end_date_time;

-- 作业流实例添加耗时
alter table SY_WORK_INSTANCE
    add duration int null comment '耗时时间（秒）' after exec_end_date_time;

-- SY_WORK_VERSION将cron扩展成cron_config
alter table SY_WORKFLOW_VERSION ALTER COLUMN cron RENAME TO cron_config;
alter table SY_WORKFLOW_VERSION
    alter cron_config text null comment '调度配置';