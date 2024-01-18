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