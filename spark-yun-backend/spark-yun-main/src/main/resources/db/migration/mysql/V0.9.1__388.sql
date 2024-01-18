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

