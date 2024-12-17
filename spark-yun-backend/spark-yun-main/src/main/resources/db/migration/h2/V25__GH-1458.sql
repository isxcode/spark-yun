-- 修复分享表单新建问题
alter table SY_FORM
    alter insert_sql varchar(2000) null comment '增sql语句';

alter table SY_FORM
    alter delete_sql varchar(2000) null comment '删sql语句';

alter table SY_FORM
    alter update_sql varchar(2000) null comment '改sql语句';

alter table SY_FORM
    alter select_sql varchar(2000) null comment '查sql语句';