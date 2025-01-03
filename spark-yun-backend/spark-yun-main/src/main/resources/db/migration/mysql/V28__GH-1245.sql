-- 表元数据添加自定义备注
ALTER TABLE SY_META_TABLE_INFO
    ADD CUSTOM_COMMENT varchar(500);

-- 元数据字段信息
CREATE TABLE SY_META_COLUMN_INFO (
    datasource_id           VARCHAR(200)  NOT NULL COMMENT '元数据表id',
    table_name              VARCHAR(200)  NOT NULL COMMENT '表名',
    column_name             VARCHAR(200)  NOT NULL COMMENT '字段名',
    custom_comment          VARCHAR(500) COMMENT '字段备注',
    create_by               VARCHAR(200)  NOT NULL COMMENT '创建人',
    create_date_time        DATETIME      NOT NULL COMMENT '创建时间',
    last_modified_by        VARCHAR(200)  NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME      NOT NULL COMMENT '更新时间',
    version_number          INT           NOT NULL COMMENT '版本号',
    deleted                 INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
    tenant_id               VARCHAR(200)  NOT NULL COMMENT '租户id',
    PRIMARY KEY (datasource_id, table_name, column_name)
);