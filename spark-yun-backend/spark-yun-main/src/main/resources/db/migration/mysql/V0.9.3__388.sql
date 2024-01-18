alter table SY_API
    add token_type varchar(200) null comment '认证方式' after status;

alter table SY_API
    add page_type varchar(50) null comment '分页状态' after token_type;