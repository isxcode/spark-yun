-- 数据分层表
create table SY_TABLE_LAYER
(
    id                      varchar(200)  not null comment '数据分层id' primary key,
    name                    varchar(200)  not null comment '分层名称',
    table_rule              varchar(500) comment '表名分层规范',
    parent_layer_id         varchar(200) comment '父层级分层id',
    parent_id_list          varchar(500) comment '父级链条',
    parent_name_list        varchar(500) comment '父级链条名称',
    remark                  varchar(500) comment '分层描述',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);

-- 字段标准表
create table SY_COLUMN_FORMAT
(
    id                      varchar(200)  not null comment '字段标准id' primary key,
    name                    varchar(200)  not null comment '标准名称',
    column_type_code        varchar(200)  not null comment '字段类型编码，TEXT、LONGTEXT、CUSTOM',
    column_type             varchar(200) comment '字段长度，字段配置',
    column_rule             varchar(500) comment '字段规范',
    status                  varchar(200)  not null comment '是否启用',
    remark                  varchar(200) comment '字段标准备注',
    is_null                 varchar(10)   not null comment '可以为null',
    is_duplicate            varchar(10)   not null comment '可以重复',
    is_partition            varchar(10)   not null comment '是分区键',
    is_primary              varchar(10)   not null comment '是主键',
    default_value           varchar(200) comment '默认值',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);

-- 数据模型表
create table SY_DATA_MODEL
(
    id                      varchar(200)  not null comment '数据模型id' primary key,
    name                    varchar(200)  not null comment '模型名称',
    layer_id                varchar(200)  not null comment '数据分层id',
    db_type                 varchar(200)  not null comment '数据源类型',
    datasource_id           varchar(200)  not null comment '数据源id',
    table_name              varchar(200)  not null comment '表名',
    model_type              varchar(200)  not null comment '模型类型，LINK_TABLE 关联模型,MODEL_TABLE 原始模型',
    table_config            varchar(500)  null comment '表高级配置',
    status                  varchar(200)  not null comment '数据模型状态，INIT 新建、SUCCESS 成功、ERROR 失败',
    build_log               varchar(500)  null comment '创建日志',
    remark                  varchar(200) comment '数据模型备注',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);

-- 模型字段表
create table SY_DATA_MODEL_COLUMN
(
    id                      varchar(200)  not null comment '模型字段id' primary key,
    name                    varchar(200)  not null comment '字段名称',
    column_name             varchar(200)  not null comment '表字段名',
    model_id                varchar(200)  not null comment '数据模型id',
    column_format_id        varchar(200)  not null comment '字段标准id',
    column_index            int           not null comment '字段顺序',
    remark                  varchar(200) comment '模型字段备注',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除',
    tenant_id               varchar(200)  not null comment '租户id'
);