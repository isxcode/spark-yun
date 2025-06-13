-- 数据分层表
CREATE TABLE sy_data_layer (
    id                      VARCHAR(200)  NOT NULL COMMENT '数据分层id',
    name                    VARCHAR(200)  NOT NULL COMMENT '分层名称',
    table_rule              VARCHAR(500)  COMMENT '表名分层规范',
    parent_layer_id         VARCHAR(200)  COMMENT '父层级分层id',
    parent_id_list          VARCHAR(500)  COMMENT '父级链条',
    parent_name_list        VARCHAR(500)  COMMENT '父级链条名称',
    remark                  VARCHAR(500)  COMMENT '分层描述',
    create_by               VARCHAR(200)  NOT NULL COMMENT '创建人',
    create_date_time        DATETIME      NOT NULL COMMENT '创建时间',
    last_modified_by        VARCHAR(200)  NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME      NOT NULL COMMENT '更新时间',
    version_number          INT           NOT NULL COMMENT '版本号',
    deleted                 INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
    tenant_id               VARCHAR(200)  NOT NULL COMMENT '租户id',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据分层表';

-- 字段标准表
CREATE TABLE sy_column_format (
    id                      VARCHAR(200)  NOT NULL COMMENT '字段标准id',
    name                    VARCHAR(200)  NOT NULL COMMENT '标准名称',
    column_type_code        VARCHAR(200)  NOT NULL COMMENT '字段类型编码，TEXT、LONGTEXT、CUSTOM',
    column_type             VARCHAR(200)  COMMENT '字段长度，字段配置',
    column_rule             VARCHAR(500)  COMMENT '字段规范',
    status                  VARCHAR(200)  NOT NULL COMMENT '是否启用',
    remark                  VARCHAR(200)  COMMENT '字段标准备注',
    is_null                 VARCHAR(10)   NOT NULL COMMENT '可以为null',
    is_duplicate            VARCHAR(10)   NOT NULL COMMENT '可以重复',
    is_partition            VARCHAR(10)   NOT NULL COMMENT '是分区键',
    is_primary              VARCHAR(10)   NOT NULL COMMENT '是主键',
    default_value           VARCHAR(200)  COMMENT '默认值',
    create_by               VARCHAR(200)  NOT NULL COMMENT '创建人',
    create_date_time        DATETIME      NOT NULL COMMENT '创建时间',
    last_modified_by        VARCHAR(200)  NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME      NOT NULL COMMENT '更新时间',
    version_number          INT           NOT NULL COMMENT '版本号',
    deleted                 INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
    tenant_id               VARCHAR(200)  NOT NULL COMMENT '租户id',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段标准表';

-- 数据模型表
CREATE TABLE sy_data_model (
    id                      VARCHAR(200)  NOT NULL COMMENT '数据模型id',
    name                    VARCHAR(200)  NOT NULL COMMENT '模型名称',
    layer_id                VARCHAR(200)  NOT NULL COMMENT '数据分层id',
    db_type                 VARCHAR(200)  NOT NULL COMMENT '数据源类型',
    datasource_id           VARCHAR(200)  NOT NULL COMMENT '数据源id',
    table_name              VARCHAR(200)  NOT NULL COMMENT '表名',
    model_type              VARCHAR(200)  NOT NULL COMMENT '模型类型，LINK_TABLE 关联模型,MODEL_TABLE 原始模型',
    table_config            VARCHAR(500)  NULL COMMENT '表高级配置',
    status                  VARCHAR(200)  NOT NULL COMMENT '数据模型状态，INIT 新建、SUCCESS 成功、ERROR 失败',
    build_log               VARCHAR(500)  NULL COMMENT '创建日志',
    remark                  VARCHAR(200)  COMMENT '数据模型备注',
    create_by               VARCHAR(200)  NOT NULL COMMENT '创建人',
    create_date_time        DATETIME      NOT NULL COMMENT '创建时间',
    last_modified_by        VARCHAR(200)  NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME      NOT NULL COMMENT '更新时间',
    version_number          INT           NOT NULL COMMENT '版本号',
    deleted                 INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
    tenant_id               VARCHAR(200)  NOT NULL COMMENT '租户id',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据模型表';

-- 模型字段表
CREATE TABLE sy_data_model_column (
    id                      VARCHAR(200)  NOT NULL COMMENT '模型字段id',
    name                    VARCHAR(200)  NOT NULL COMMENT '字段名称',
    column_name             VARCHAR(200)  NOT NULL COMMENT '表字段名',
    model_id                VARCHAR(200)  NOT NULL COMMENT '数据模型id',
    column_format_id        VARCHAR(200)  COMMENT '字段标准id',
    column_index            INT           NOT NULL COMMENT '字段顺序',
    link_column_type        VARCHAR(200)  COMMENT '关联模型字段类型',
    remark                  VARCHAR(200)  COMMENT '模型字段备注',
    create_by               VARCHAR(200)  NOT NULL COMMENT '创建人',
    create_date_time        DATETIME      NOT NULL COMMENT '创建时间',
    last_modified_by        VARCHAR(200)  NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME      NOT NULL COMMENT '更新时间',
    version_number          INT           NOT NULL COMMENT '版本号',
    deleted                 INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
    tenant_id               VARCHAR(200)  NOT NULL COMMENT '租户id',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模型字段表';