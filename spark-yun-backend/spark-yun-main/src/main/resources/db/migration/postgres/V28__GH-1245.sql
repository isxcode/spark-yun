-- 表元数据添加自定义备注
ALTER TABLE SY_META_TABLE_INFO
    ADD COLUMN CUSTOM_COMMENT varchar(500);

-- 元数据字段信息
CREATE TABLE SY_META_COLUMN_INFO (
    datasource_id           VARCHAR(200)  NOT NULL, -- 元数据表id
    table_name              VARCHAR(200)  NOT NULL, -- 表名
    column_name             VARCHAR(200)  NOT NULL, -- 字段名
    custom_comment          VARCHAR(500), -- 字段备注
    create_by               VARCHAR(200)  NOT NULL, -- 创建人
    create_date_time        TIMESTAMP     NOT NULL, -- 创建时间
    last_modified_by        VARCHAR(200)  NOT NULL, -- 更新人
    last_modified_date_time TIMESTAMP     NOT NULL, -- 更新时间
    version_number          INT           NOT NULL, -- 版本号
    deleted                 INT DEFAULT 0 NOT NULL, -- 逻辑删除
    tenant_id               VARCHAR(200)  NOT NULL, -- 租户id
    PRIMARY KEY (datasource_id, table_name, column_name)
);