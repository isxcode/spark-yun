-- 添加数据大屏分享的链接表
create table SY_VIEW_LINK
(
    id                      varchar(200)  not null comment '数据大屏分享链接id'
    primary key,
    view_id                 varchar(200)  not null comment '大屏id',
    view_version            varchar(200)  not null comment '大屏版本',
    view_token              varchar(500)  not null comment '分享大屏的匿名token',
    invalid_date_time       datetime      not null comment '到期时间',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);