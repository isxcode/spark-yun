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

-- 新增字段 createMode 创建模式
alter table SY_FORM
  add create_mode varchar(100) null comment '表单创建模式' after status;

-- 表单新增表单备注字段
alter table SY_FORM
  add remark varchar(500) null comment '表单备注' after create_mode;

alter table SY_FORM
    modify insert_sql varchar(2000) null comment '增sql语句';

alter table SY_FORM
    modify delete_sql varchar(2000) null comment '删sql语句';

alter table SY_FORM
    modify update_sql varchar(2000) null comment '改sql语句';

alter table SY_FORM
    modify select_sql varchar(2000) null comment '查sql语句';

alter table SY_FORM
    drop column create_mode;

alter table SY_FORM
    add form_web_config longtext null comment '前端所有的配置' after select_sql;

alter table SY_FORM_COMPONENT
    add uuid varchar(50) not null comment '前端的uuid' after form_id;;

alter table SY_FORM_COMPONENT
    add component_config text null comment '组件的配置' after value_sql;

alter table SY_FORM_COMPONENT
    drop column name;

alter table SY_FORM_COMPONENT
    drop column component_type;

alter table SY_FORM_COMPONENT
    drop column component_key;

alter table SY_FORM_COMPONENT
    drop column is_display;

alter table SY_FORM_COMPONENT
    drop column is_primary_key;

alter table SY_FORM_COMPONENT
    drop column show_value;

alter table SY_FORM_COMPONENT
    drop column value_sql;

alter table SY_FORM
    add form_version varchar(50) null comment '表单版本号' after form_web_config;

alter table SY_API
    add token_type varchar(200) null comment '认证方式' after status;

alter table SY_API
    add page_type varchar(50) null comment '分页状态' after token_type;

alter table SY_FORM
    add create_mode varchar(50) not null comment '该表单主表的创建模式' after form_version;

alter table SY_API
    modify page_type boolean null comment '分页状态';

alter table SY_API
    modify api_sql varchar(2000) null comment '执行的sql';

-- 添加分享表单的链接表
create table SY_FORM_LINK
(
  id                      varchar(200)  not null comment '分享表单链接id'
    primary key,
  form_id                 varchar(200)  not null comment '表单id',
  form_version            varchar(200)  not null comment '表单版本',
  form_token              varchar(500)  not null comment '分享表单的匿名token',
  create_by               varchar(200)  not null comment '创建人',
  invalid_date_time       datetime      not null comment '到期时间',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);
