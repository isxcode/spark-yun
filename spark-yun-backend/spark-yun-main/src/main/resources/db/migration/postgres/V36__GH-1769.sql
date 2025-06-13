CREATE TABLE sy_data_layer
(
    id                      VARCHAR(200) NOT NULL PRIMARY KEY,
    name                    VARCHAR(200) NOT NULL,
    table_rule              VARCHAR(500),
    parent_layer_id         VARCHAR(200),
    parent_id_list          VARCHAR(500),
    parent_name_list        VARCHAR(500),
    remark                  VARCHAR(500),
    create_by               VARCHAR(200) NOT NULL,
    create_date_time        TIMESTAMP NOT NULL,
    last_modified_by        VARCHAR(200) NOT NULL,
    last_modified_date_time TIMESTAMP NOT NULL,
    version_number          INTEGER NOT NULL,
    deleted                 INTEGER DEFAULT 0 NOT NULL,
    tenant_id               VARCHAR(200) NOT NULL
);

COMMENT ON TABLE sy_data_layer IS '数据分层表';
COMMENT ON COLUMN sy_data_layer.id IS '数据分层id';
COMMENT ON COLUMN sy_data_layer.name IS '分层名称';
COMMENT ON COLUMN sy_data_layer.table_rule IS '表名分层规范';
COMMENT ON COLUMN sy_data_layer.parent_layer_id IS '父层级分层id';
COMMENT ON COLUMN sy_data_layer.parent_id_list IS '父级链条';
COMMENT ON COLUMN sy_data_layer.parent_name_list IS '父级链条名称';
COMMENT ON COLUMN sy_data_layer.remark IS '分层描述';
COMMENT ON COLUMN sy_data_layer.create_by IS '创建人';
COMMENT ON COLUMN sy_data_layer.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_data_layer.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_data_layer.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_data_layer.version_number IS '版本号';
COMMENT ON COLUMN sy_data_layer.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_data_layer.tenant_id IS '租户id';

-- 字段标准表
CREATE TABLE sy_column_format
(
    id                      VARCHAR(200) NOT NULL PRIMARY KEY,
    name                    VARCHAR(200) NOT NULL,
    column_type_code        VARCHAR(200) NOT NULL,
    column_type             VARCHAR(200),
    column_rule             VARCHAR(500),
    status                  VARCHAR(200) NOT NULL,
    remark                  VARCHAR(200),
    is_null                 VARCHAR(10) NOT NULL,
    is_duplicate            VARCHAR(10) NOT NULL,
    is_partition            VARCHAR(10) NOT NULL,
    is_primary              VARCHAR(10) NOT NULL,
    default_value           VARCHAR(200),
    create_by               VARCHAR(200) NOT NULL,
    create_date_time        TIMESTAMP NOT NULL,
    last_modified_by        VARCHAR(200) NOT NULL,
    last_modified_date_time TIMESTAMP NOT NULL,
    version_number          INTEGER NOT NULL,
    deleted                 INTEGER DEFAULT 0 NOT NULL,
    tenant_id               VARCHAR(200) NOT NULL
);

COMMENT ON TABLE sy_column_format IS '字段标准表';
COMMENT ON COLUMN sy_column_format.id IS '字段标准id';
COMMENT ON COLUMN sy_column_format.name IS '标准名称';
COMMENT ON COLUMN sy_column_format.column_type_code IS '字段类型编码，TEXT、LONGTEXT、CUSTOM';
COMMENT ON COLUMN sy_column_format.column_type IS '字段长度，字段配置';
COMMENT ON COLUMN sy_column_format.column_rule IS '字段规范';
COMMENT ON COLUMN sy_column_format.status IS '是否启用';
COMMENT ON COLUMN sy_column_format.remark IS '字段标准备注';
COMMENT ON COLUMN sy_column_format.is_null IS '可以为null';
COMMENT ON COLUMN sy_column_format.is_duplicate IS '可以重复';
COMMENT ON COLUMN sy_column_format.is_partition IS '是分区键';
COMMENT ON COLUMN sy_column_format.is_primary IS '是主键';
COMMENT ON COLUMN sy_column_format.default_value IS '默认值';
COMMENT ON COLUMN sy_column_format.create_by IS '创建人';
COMMENT ON COLUMN sy_column_format.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_column_format.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_column_format.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_column_format.version_number IS '版本号';
COMMENT ON COLUMN sy_column_format.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_column_format.tenant_id IS '租户id';

-- 数据模型表
CREATE TABLE sy_data_model
(
    id                      VARCHAR(200) NOT NULL PRIMARY KEY,
    name                    VARCHAR(200) NOT NULL,
    layer_id                VARCHAR(200) NOT NULL,
    db_type                 VARCHAR(200) NOT NULL,
    datasource_id           VARCHAR(200) NOT NULL,
    table_name              VARCHAR(200) NOT NULL,
    model_type              VARCHAR(200) NOT NULL,
    table_config            VARCHAR(500),
    status                  VARCHAR(200) NOT NULL,
    build_log               VARCHAR(500),
    remark                  VARCHAR(200),
    create_by               VARCHAR(200) NOT NULL,
    create_date_time        TIMESTAMP NOT NULL,
    last_modified_by        VARCHAR(200) NOT NULL,
    last_modified_date_time TIMESTAMP NOT NULL,
    version_number          INTEGER NOT NULL,
    deleted                 INTEGER DEFAULT 0 NOT NULL,
    tenant_id               VARCHAR(200) NOT NULL
);

COMMENT ON TABLE sy_data_model IS '数据模型表';
COMMENT ON COLUMN sy_data_model.id IS '数据模型id';
COMMENT ON COLUMN sy_data_model.name IS '模型名称';
COMMENT ON COLUMN sy_data_model.layer_id IS '数据分层id';
COMMENT ON COLUMN sy_data_model.db_type IS '数据源类型';
COMMENT ON COLUMN sy_data_model.datasource_id IS '数据源id';
COMMENT ON COLUMN sy_data_model.table_name IS '表名';
COMMENT ON COLUMN sy_data_model.model_type IS '模型类型，LINK_TABLE 关联模型,MODEL_TABLE 原始模型';
COMMENT ON COLUMN sy_data_model.table_config IS '表高级配置';
COMMENT ON COLUMN sy_data_model.status IS '数据模型状态，INIT 新建、SUCCESS 成功、ERROR 失败';
COMMENT ON COLUMN sy_data_model.build_log IS '创建日志';
COMMENT ON COLUMN sy_data_model.remark IS '数据模型备注';
COMMENT ON COLUMN sy_data_model.create_by IS '创建人';
COMMENT ON COLUMN sy_data_model.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_data_model.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_data_model.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_data_model.version_number IS '版本号';
COMMENT ON COLUMN sy_data_model.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_data_model.tenant_id IS '租户id';

-- 模型字段表
CREATE TABLE sy_data_model_column
(
    id                      VARCHAR(200) NOT NULL PRIMARY KEY,
    name                    VARCHAR(200) NOT NULL,
    column_name             VARCHAR(200) NOT NULL,
    model_id                VARCHAR(200) NOT NULL,
    column_format_id        VARCHAR(200),
    column_index            INTEGER NOT NULL,
    link_column_type        VARCHAR(200),
    remark                  VARCHAR(200),
    create_by               VARCHAR(200) NOT NULL,
    create_date_time        TIMESTAMP NOT NULL,
    last_modified_by        VARCHAR(200) NOT NULL,
    last_modified_date_time TIMESTAMP NOT NULL,
    version_number          INTEGER NOT NULL,
    deleted                 INTEGER DEFAULT 0 NOT NULL,
    tenant_id               VARCHAR(200) NOT NULL
);

COMMENT ON TABLE sy_data_model_column IS '模型字段表';
COMMENT ON COLUMN sy_data_model_column.id IS '模型字段id';
COMMENT ON COLUMN sy_data_model_column.name IS '字段名称';
COMMENT ON COLUMN sy_data_model_column.column_name IS '表字段名';
COMMENT ON COLUMN sy_data_model_column.model_id IS '数据模型id';
COMMENT ON COLUMN sy_data_model_column.column_format_id IS '字段标准id';
COMMENT ON COLUMN sy_data_model_column.column_index IS '字段顺序';
COMMENT ON COLUMN sy_data_model_column.link_column_type IS '关联模型字段类型';
COMMENT ON COLUMN sy_data_model_column.remark IS '模型字段备注';
COMMENT ON COLUMN sy_data_model_column.create_by IS '创建人';
COMMENT ON COLUMN sy_data_model_column.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_data_model_column.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_data_model_column.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_data_model_column.version_number IS '版本号';
COMMENT ON COLUMN sy_data_model_column.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_data_model_column.tenant_id IS '租户id';