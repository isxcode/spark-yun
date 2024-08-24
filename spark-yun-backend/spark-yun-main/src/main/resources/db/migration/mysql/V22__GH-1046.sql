-- 新增excel同步配置字段
ALTER TABLE SY_WORK_CONFIG
    ADD EXCEL_SYNC_CONFIG LONGTEXT;

-- 实例表中添加EXCEL_SYNC_CONFIG字段
ALTER TABLE SY_WORK_VERSION
    ADD EXCEL_SYNC_CONFIG LONGTEXT;

-- 元数据相关联的表
-- 元数据db表
CREATE TABLE SY_META_DATABASE (
    datasource_id           VARCHAR(200)  NOT NULL COMMENT '元数据数据源id',
    name                    VARCHAR(200)  NOT NULL COMMENT '数据源名称',
    db_name                 VARCHAR(200)  NULL COMMENT 'db名称',
    db_type                 VARCHAR(200)  NOT NULL COMMENT '数据源类型',
    db_comment              VARCHAR(500) COMMENT '数据源备注',
    create_by               VARCHAR(200)  NOT NULL COMMENT '创建人',
    create_date_time        DATETIME      NOT NULL COMMENT '创建时间',
    last_modified_by        VARCHAR(200)  NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME      NOT NULL COMMENT '更新时间',
    version_number          INT           NOT NULL COMMENT '版本号',
    deleted                 INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
    tenant_id               VARCHAR(200)  NOT NULL COMMENT '租户id',
    PRIMARY KEY (datasource_id)
);

-- 元数据表信息
CREATE TABLE SY_META_TABLE (
    datasource_id           VARCHAR(200)  NOT NULL COMMENT '元数据表id',
    table_name              VARCHAR(200)  NOT NULL COMMENT '表名',
    table_comment           VARCHAR(500) COMMENT '表备注',
    create_by               VARCHAR(200)  NOT NULL COMMENT '创建人',
    create_date_time        DATETIME      NOT NULL COMMENT '创建时间',
    last_modified_by        VARCHAR(200)  NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME      NOT NULL COMMENT '更新时间',
    version_number          INT           NOT NULL COMMENT '版本号',
    deleted                 INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
    tenant_id               VARCHAR(200)  NOT NULL COMMENT '租户id',
    PRIMARY KEY (datasource_id, table_name)
);

-- 元数据字段信息
CREATE TABLE SY_META_COLUMN (
    datasource_id           VARCHAR(200)  NOT NULL COMMENT '元数据表id',
    table_name              VARCHAR(200)  NOT NULL COMMENT '表名',
    column_name             VARCHAR(200)  NOT NULL COMMENT '字段名',
    column_type             VARCHAR(200)  NOT NULL COMMENT '字段类型',
    column_comment          VARCHAR(500) COMMENT '字段备注',
    is_partition_column     BOOLEAN NOT NULL COMMENT '是否为分区字段',
    create_by               VARCHAR(200)  NOT NULL COMMENT '创建人',
    create_date_time        DATETIME      NOT NULL COMMENT '创建时间',
    last_modified_by        VARCHAR(200)  NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME      NOT NULL COMMENT '更新时间',
    version_number          INT           NOT NULL COMMENT '版本号',
    deleted                 INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
    tenant_id               VARCHAR(200)  NOT NULL COMMENT '租户id',
    PRIMARY KEY (datasource_id, table_name, column_name)
);

-- 元数据采集任务表
CREATE TABLE SY_META_WORK (
    id                      VARCHAR(200)  NOT NULL COMMENT '元数据采集任务id',
    name                    VARCHAR(200)  NOT NULL COMMENT '任务名',
    db_type                 VARCHAR(200)  NOT NULL COMMENT '数据源类型',
    datasource_id           VARCHAR(100)  NOT NULL COMMENT '数据源id',
    collect_type            VARCHAR(100)  NOT NULL COMMENT '采集方式',
    table_pattern           VARCHAR(100)  NOT NULL COMMENT '表名表达式',
    cron_config             VARCHAR(500) COMMENT '调度配置',
    status                  VARCHAR(500)  NOT NULL COMMENT '任务状态',
    remark                  VARCHAR(500) COMMENT '表备注',
    create_by               VARCHAR(200)  NOT NULL COMMENT '创建人',
    create_date_time        DATETIME      NOT NULL COMMENT '创建时间',
    last_modified_by        VARCHAR(200)  NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME      NOT NULL COMMENT '更新时间',
    version_number          INT           NOT NULL COMMENT '版本号',
    deleted                 INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
    tenant_id               VARCHAR(200)  NOT NULL COMMENT '租户id',
    PRIMARY KEY (id)
);

-- 元数据采集实例表
CREATE TABLE SY_META_INSTANCE (
    id                      VARCHAR(200)  NOT NULL COMMENT '元数据采集实例id',
    meta_work_id            VARCHAR(200)  NOT NULL COMMENT '元数据采集任务id',
    trigger_type            VARCHAR(200)  NOT NULL COMMENT '触发类型',
    status                  VARCHAR(500)  NOT NULL COMMENT '实例类型',
    start_date_time         DATETIME      NOT NULL COMMENT '开始时间',
    end_date_time           DATETIME COMMENT '结束时间',
    collect_log             LONGTEXT COMMENT '采集日志',
    create_by               VARCHAR(200)  NOT NULL COMMENT '创建人',
    create_date_time        DATETIME      NOT NULL COMMENT '创建时间',
    last_modified_by        VARCHAR(200)  NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME      NOT NULL COMMENT '更新时间',
    version_number          INT           NOT NULL COMMENT '版本号',
    deleted                 INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
    tenant_id               VARCHAR(200)  NOT NULL COMMENT '租户id',
    PRIMARY KEY (id)
);

-- 元数据表基础信息表
CREATE TABLE SY_META_TABLE_INFO (
    datasource_id           VARCHAR(200)  NOT NULL COMMENT '元数据表id',
    table_name              VARCHAR(200)  NOT NULL COMMENT '表名',
    column_count            BIGINT COMMENT '字段数',
    total_rows              BIGINT COMMENT '总条数',
    total_size              BIGINT COMMENT '总大小',
    create_by               VARCHAR(200)  NOT NULL COMMENT '创建人',
    create_date_time        DATETIME      NOT NULL COMMENT '创建时间',
    last_modified_by        VARCHAR(200)  NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME      NOT NULL COMMENT '更新时间',
    version_number          INT           NOT NULL COMMENT '版本号',
    deleted                 INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
    tenant_id               VARCHAR(200)  NOT NULL COMMENT '租户id',
    PRIMARY KEY (datasource_id, table_name)
);