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
    datasource_id           varchar(200)  not null comment '元数据数据源id'
        primary key,
    name                    varchar(200)  not null comment '数据源名称',
    db_name                 varchar(200)  null comment 'db名称',
    db_type                 varchar(200)  not null comment '数据源类型',
    db_comment              varchar(500) comment '数据源备注',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);

-- 元数据表信息
create table SY_META_TABLE
(
    datasource_id           varchar(200)  not null comment '元数据表id',
    table_name              varchar(200)  not null comment '表名',
    table_comment           varchar(500) comment '表备注',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id',
    PRIMARY KEY (datasource_id, table_name)
);

-- 元数据字段信息
create table SY_META_COLUMN
(
    datasource_id           varchar(200)  not null comment '元数据表id',
    table_name              varchar(200)  not null comment '表名',
    column_name             varchar(200)  not null comment '字段名',
    column_type             varchar(200)  not null comment '字段类型',
    column_comment          varchar(500) comment '字段备注',
    is_partition_column     boolean not null comment '是否为分区字段',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id',
    PRIMARY KEY (datasource_id, table_name, column_name)
);

-- 元数据采集任务表
create table SY_META_WORK
(
    id                      varchar(200)  not null comment '元数据采集任务id'
        primary key,
    name                    varchar(200)  not null comment '任务名',
    db_type                 varchar(200)  not null comment '数据源类型',
    datasource_id           varchar(100)  not null comment '数据源id',
    collect_type            varchar(100)  not null comment '采集方式',
    table_pattern           varchar(100)  not null comment '表名表达式',
    cron_config             varchar(500) comment '调度配置',
    status                  varchar(500)  not null comment '任务状态',
    remark                  varchar(500) comment '表备注',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);

-- 元数据采集实例表
create table SY_META_INSTANCE
(
    id                      varchar(200)  not null comment '元数据采集实例id'
        primary key,
    meta_work_id            varchar(200)  not null comment '元数据采集任务id',
    trigger_type            varchar(200)  not null comment '触发类型',
    status                  varchar(500)  not null comment '实例类型',
    start_date_time         datetime      not null comment '开始时间',
    end_date_time           datetime comment '结束时间',
    collect_log             longtext comment '采集日志',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);

-- 元数据表基础信息表
create table SY_META_TABLE_INFO
(
    datasource_id           varchar(200)  not null comment '元数据表id',
    table_name              varchar(200)  not null comment '表名',
    column_count            long comment '字段数',
    total_rows              long comment '总条数',
    total_size              long comment '总大小',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id',
    PRIMARY KEY (datasource_id, table_name)
);
