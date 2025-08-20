-- 全局加密配置表
create table SY_SECRET_KEY
(
    ID                      CHARACTER VARYING(200) not null primary key unique comment '全局变量id',
    KEY_NAME                CHARACTER VARYING(200) not null comment '全局变量key',
    SECRET_VALUE            CHARACTER VARYING(500) comment '全局变量value',
    REMARK                  CHARACTER VARYING(500) comment '全局变量备注',
    VERSION_NUMBER          int                    not null comment '版本号',
    CREATE_BY               CHARACTER VARYING(200) not null comment '创建人',
    CREATE_DATE_TIME        TIMESTAMP              not null comment '创建时间',
    LAST_MODIFIED_BY        CHARACTER VARYING(200) not null comment '更新人',
    LAST_MODIFIED_DATE_TIME TIMESTAMP              not null comment '更新时间',
    DELETED                 INTEGER default 0      not null comment '逻辑删除',
    TENANT_ID               CHARACTER VARYING(200) not null comment '租户id'
);