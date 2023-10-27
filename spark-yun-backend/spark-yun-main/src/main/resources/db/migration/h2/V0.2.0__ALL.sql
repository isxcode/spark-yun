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
    add default_cluster boolean null comment '默认计算集群' after used_storage_num;

-- 添加默认集群节点
alter table SY_CLUSTER_NODE
    add default_cluster_node boolean null comment '默认集群节点' after spark_home_path;