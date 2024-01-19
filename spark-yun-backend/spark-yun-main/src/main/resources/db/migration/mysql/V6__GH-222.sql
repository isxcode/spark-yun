-- 作业添加udf函数开关
ALTER TABLE SY_WORK_CONFIG ADD udf_status BOOL DEFAULT false NULL;
ALTER TABLE SY_WORK_CONFIG CHANGE udf_status udf_status BOOL DEFAULT false NULL AFTER cron_config;

-- 新增udf定义表
create table SY_WORK_UDF
(
  id                      varchar(200)  not null comment 'udf唯一id'
    primary key,
  type                    varchar(20)   not null comment 'udf类型',
  file_id                 varchar(200)  not null comment 'jar文件id',
  func_name               varchar(200)  not null comment 'udf调用方法名',
  class_name              varchar(200)  not null comment 'udf方法class名称',
  result_type             varchar(200)  not null comment '返回值类型',
  status                  boolean       not null comment '是否启用此udf',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

ALTER TABLE SY_WORK_CONFIG ADD jar_conf text NULL;
ALTER TABLE SY_WORK_CONFIG CHANGE jar_conf jar_conf text NULL AFTER sync_rule;

ALTER TABLE SY_WORKFLOW_CONFIG ADD external_call CHAR NULL;
ALTER TABLE SY_WORKFLOW_CONFIG CHANGE external_call external_call CHAR NULL AFTER dag_end_list;
ALTER TABLE SY_WORKFLOW_CONFIG ADD access_key varchar(100) NULL;
ALTER TABLE SY_WORKFLOW_CONFIG CHANGE access_key access_key varchar(100) NULL AFTER external_call;
