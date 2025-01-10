-- 表元数据添加自定义备注
alter table SY_META_TABLE_INFO
    add CUSTOM_COMMENT varchar(500);

-- 元数据字段信息
create table SY_META_COLUMN_INFO
(
    datasource_id           varchar(200)  not null comment '元数据表id',
    table_name              varchar(200)  not null comment '表名',
    column_name             varchar(200)  not null comment '字段名',
    custom_comment          varchar(500) comment '自定义备注',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id',
    PRIMARY KEY (datasource_id, table_name, column_name)
);