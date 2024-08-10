-- 新增excel同步配置字段
alter table SY_WORK_CONFIG
    add EXCEL_SYNC_CONFIG longtext;

-- 实例表中添加EXCEL_SYNC_CONFIG字段
alter table SY_WORK_VERSION
    add EXCEL_SYNC_CONFIG longtext;

-- 元数据相关联的表
-- 元数据db表
create table SY_META_DATABASE
(
    id                      varchar(200)  not null comment '元数据数据源id'
        primary key,
    datasource_id           varchar(200)  not null comment '数据源关联id',
    datasource_name         varchar(200)  not null comment '数据源名称',
    db_name                 varchar(200)  null comment 'db名称',
    db_type                 varchar(200)  not null comment '数据源类型',
    db_remark               varchar(500) comment '数据源备注',
    status                  varchar(100)  not null comment '元数据状态',
    update_date_time        datetime      not null comment '更新时间',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);

-- 元数据表
create table SY_META_TABLE
(
    id                      varchar(200)  not null comment '元数据表id'
        primary key,
    table_name              varchar(200)  not null comment '表名',
    datasource_id           varchar(200)  not null comment '数据源关联id',
    status                  varchar(100)  not null comment '元数据状态',
    remark                  varchar(500) comment '表备注',
    update_date_time        datetime      not null comment '更新时间',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);