-- 字段血缘表
create table SY_META_COLUMN_LINEAGE
(
    ID                      CHARACTER VARYING(200) not null primary key unique comment '字段血缘id',
    FROM_DB_ID              CHARACTER VARYING(200) not null comment '来源数据源',
    FROM_TABLE_NAME         CHARACTER VARYING(200) not null comment '来源表名',
    FROM_COLUMN_NAME        CHARACTER VARYING(200) not null comment '来源字段名',
    WORK_ID                 CHARACTER VARYING(200) not null comment '作业',
    WORK_VERSION_ID         CHARACTER VARYING(200) not null comment '作业版本',
    TO_DB_ID                CHARACTER VARYING(200) not null comment '去向数据源',
    TO_TABLE_NAME           CHARACTER VARYING(200) not null comment '去向表名',
    TO_COLUMN_NAME          CHARACTER VARYING(200) not null comment '去向字段名',
    REMARK                  CHARACTER VARYING(500) comment '血缘备注',
    VERSION_NUMBER          int                    not null comment '版本号',
    CREATE_BY               CHARACTER VARYING(200) not null comment '创建人',
    CREATE_DATE_TIME        TIMESTAMP              not null comment '创建时间',
    LAST_MODIFIED_BY        CHARACTER VARYING(200) not null comment '更新人',
    LAST_MODIFIED_DATE_TIME TIMESTAMP              not null comment '更新时间',
    DELETED                 INTEGER default 0      not null comment '逻辑删除',
    TENANT_ID               CHARACTER VARYING(200) not null comment '租户id'
);