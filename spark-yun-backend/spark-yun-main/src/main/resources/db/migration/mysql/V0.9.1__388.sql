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