alter table SY_FORM
    add create_mode varchar(50) not null comment '该表单主表的创建模式' after form_version;

alter table SY_API
    alter page_type boolean null comment '分页状态';