-- 同步作业配置表
create table SY_SYNC_WORK_CONFIG
(
  id                      varchar(200)  not null comment '同步作业配置唯一id'
    primary key,
  work_id                 varchar(200)  not null comment '作业唯一id',
  source_db_type          varchar(200)  not null comment '来源数据源类型',
  source_db_id            varchar(200)  not null comment '来源数据源唯一id',
  source_table            varchar(200)  not null comment '来源表名',
  query_condition         varchar(200)  null comment '查询条件',
  target_db_type          varchar(200)  not null comment '目标数据源类型',
  target_db_id            varchar(200)  not null comment '目标数据源唯一id',
  target_table            varchar(200)  not null comment '目标表名',
  over_mode               varchar(200)  not null comment '写入模式',
  colum_mapping           text          not null comment '字段映射关系',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

CREATE UNIQUE INDEX SY_SYNC_WORK_CONFIG_work_id_IDX USING BTREE ON zhiqingyun.SY_SYNC_WORK_CONFIG (work_id);

-- 创建资源文件表
create table SY_FILE
(
  id                      varchar(200)  not null comment '文件配置唯一id'
    primary key,
  file_name               varchar(200)  null comment '文件名称',
  file_size               varchar(200)  null comment '文件大小',
  file_path               varchar(200)  null comment '文件存储路径',
  file_type               varchar(200)  null comment '文件类型',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id',
  constraint id
    unique (id)
);

ALTER TABLE SY_WORK_CONFIG ADD sync_conf text NULL;
ALTER TABLE SY_WORK_CONFIG CHANGE sync_conf sync_conf text NULL AFTER sql_script;

ALTER TABLE SY_SYNC_WORK_CONFIG ADD source_table_data text NULL;
ALTER TABLE SY_SYNC_WORK_CONFIG CHANGE source_table_data sourceTableData text not NULL AFTER over_mode;
ALTER TABLE SY_SYNC_WORK_CONFIG ADD target_table_data text NULL;
ALTER TABLE SY_SYNC_WORK_CONFIG CHANGE target_table_data targetTableData text not NULL AFTER sourceTableData;
