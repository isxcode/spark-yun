-- 新增excel同步配置字段
ALTER TABLE SY_WORK_CONFIG
    ADD COLUMN EXCEL_SYNC_CONFIG TEXT;

-- 实例表中添加EXCEL_SYNC_CONFIG字段
ALTER TABLE SY_WORK_VERSION
    ADD COLUMN EXCEL_SYNC_CONFIG TEXT;

-- 元数据相关联的表
-- 元数据db表
CREATE TABLE SY_META_DATABASE (
    datasource_id           VARCHAR(200)  NOT NULL PRIMARY KEY, -- 元数据数据源id
    name                    VARCHAR(200)  NOT NULL, -- 数据源名称
    db_name                 VARCHAR(200), -- db名称
    db_type                 VARCHAR(200)  NOT NULL, -- 数据源类型
    db_comment              VARCHAR(500), -- 数据源备注
    create_by               VARCHAR(200)  NOT NULL, -- 创建人
    create_date_time        TIMESTAMP     NOT NULL, -- 创建时间
    last_modified_by        VARCHAR(200)  NOT NULL, -- 更新人
    last_modified_date_time TIMESTAMP     NOT NULL, -- 更新时间
    version_number          INT           NOT NULL, -- 版本号
    deleted                 INT DEFAULT 0 NOT NULL, -- 逻辑删除
    tenant_id               VARCHAR(200)  NOT NULL  -- 租户id
);

-- 元数据表信息
CREATE TABLE SY_META_TABLE (
    datasource_id           VARCHAR(200)  NOT NULL, -- 元数据表id
    table_name              VARCHAR(200)  NOT NULL, -- 表名
    table_comment           VARCHAR(500), -- 表备注
    create_by               VARCHAR(200)  NOT NULL, -- 创建人
    create_date_time        TIMESTAMP     NOT NULL, -- 创建时间
    last_modified_by        VARCHAR(200)  NOT NULL, -- 更新人
    last_modified_date_time TIMESTAMP     NOT NULL, -- 更新时间
    version_number          INT           NOT NULL, -- 版本号
    deleted                 INT DEFAULT 0 NOT NULL, -- 逻辑删除
    tenant_id               VARCHAR(200)  NOT NULL, -- 租户id
    PRIMARY KEY (datasource_id, table_name)
);

-- 元数据字段信息
CREATE TABLE SY_META_COLUMN (
    datasource_id           VARCHAR(200)  NOT NULL, -- 元数据表id
    table_name              VARCHAR(200)  NOT NULL, -- 表名
    column_name             VARCHAR(200)  NOT NULL, -- 字段名
    column_type             VARCHAR(200)  NOT NULL, -- 字段类型
    column_comment          VARCHAR(500), -- 字段备注
    is_partition_column     BOOLEAN       NOT NULL, -- 是否为分区字段
    create_by               VARCHAR(200)  NOT NULL, -- 创建人
    create_date_time        TIMESTAMP     NOT NULL, -- 创建时间
    last_modified_by        VARCHAR(200)  NOT NULL, -- 更新人
    last_modified_date_time TIMESTAMP     NOT NULL, -- 更新时间
    version_number          INT           NOT NULL, -- 版本号
    deleted                 INT DEFAULT 0 NOT NULL, -- 逻辑删除
    tenant_id               VARCHAR(200)  NOT NULL, -- 租户id
    PRIMARY KEY (datasource_id, table_name, column_name)
);

-- 元数据采集任务表
CREATE TABLE SY_META_WORK (
    id                      VARCHAR(200)  NOT NULL PRIMARY KEY, -- 元数据采集任务id
    name                    VARCHAR(200)  NOT NULL, -- 任务名
    db_type                 VARCHAR(200)  NOT NULL, -- 数据源类型
    datasource_id           VARCHAR(100)  NOT NULL, -- 数据源id
    collect_type            VARCHAR(100)  NOT NULL, -- 采集方式
    table_pattern           VARCHAR(100)  NOT NULL, -- 表名表达式
    cron_config             VARCHAR(500), -- 调度配置
    status                  VARCHAR(500)  NOT NULL, -- 任务状态
    remark                  VARCHAR(500), -- 表备注
    create_by               VARCHAR(200)  NOT NULL, -- 创建人
    create_date_time        TIMESTAMP     NOT NULL, -- 创建时间
    last_modified_by        VARCHAR(200)  NOT NULL, -- 更新人
    last_modified_date_time TIMESTAMP     NOT NULL, -- 更新时间
    version_number          INT           NOT NULL, -- 版本号
    deleted                 INT DEFAULT 0 NOT NULL, -- 逻辑删除
    tenant_id               VARCHAR(200)  NOT NULL  -- 租户id
);

-- 元数据采集实例表
CREATE TABLE SY_META_INSTANCE (
    id                      VARCHAR(200)  NOT NULL PRIMARY KEY, -- 元数据采集实例id
    meta_work_id            VARCHAR(200)  NOT NULL, -- 元数据采集任务id
    trigger_type            VARCHAR(200)  NOT NULL, -- 触发类型
    status                  VARCHAR(500)  NOT NULL, -- 实例类型
    start_date_time         TIMESTAMP     NOT NULL, -- 开始时间
    end_date_time           TIMESTAMP, -- 结束时间
    collect_log             TEXT, -- 采集日志
    create_by               VARCHAR(200)  NOT NULL, -- 创建人
    create_date_time        TIMESTAMP     NOT NULL, -- 创建时间
    last_modified_by        VARCHAR(200)  NOT NULL, -- 更新人
    last_modified_date_time TIMESTAMP     NOT NULL, -- 更新时间
    version_number          INT           NOT NULL, -- 版本号
    deleted                 INT DEFAULT 0 NOT NULL, -- 逻辑删除
    tenant_id               VARCHAR(200)  NOT NULL  -- 租户id
);

-- 元数据表基础信息表
CREATE TABLE SY_META_TABLE_INFO (
    datasource_id           VARCHAR(200)  NOT NULL, -- 元数据表id
    table_name              VARCHAR(200)  NOT NULL, -- 表名
    column_count            BIGINT, -- 字段数
    total_rows              BIGINT, -- 总条数
    total_size              BIGINT, -- 总大小
    create_by               VARCHAR(200)  NOT NULL, -- 创建人
    create_date_time        TIMESTAMP     NOT NULL, -- 创建时间
    last_modified_by        VARCHAR(200)  NOT NULL, -- 更新人
    last_modified_date_time TIMESTAMP     NOT NULL, -- 更新时间
    version_number          INT           NOT NULL, -- 版本号
    deleted                 INT DEFAULT 0 NOT NULL, -- 逻辑删除
    tenant_id               VARCHAR(200)  NOT NULL, -- 租户id
    PRIMARY KEY (datasource_id, table_name)
);