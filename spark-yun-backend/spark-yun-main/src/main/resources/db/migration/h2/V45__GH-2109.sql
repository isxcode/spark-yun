-- 依赖包表
create table SY_LIB_PACKAGE
(
    ID                      CHARACTER VARYING(200) not null primary key unique comment '依赖包id',
    NAME                    CHARACTER VARYING(200) not null comment '依赖包名称',
    REMARK                  CHARACTER VARYING(500) comment '依赖包备注',
    VERSION_NUMBER          int                    not null comment '版本号',
    CREATE_BY               CHARACTER VARYING(200) not null comment '创建人',
    CREATE_DATE_TIME        TIMESTAMP              not null comment '创建时间',
    LAST_MODIFIED_BY        CHARACTER VARYING(200) not null comment '更新人',
    LAST_MODIFIED_DATE_TIME TIMESTAMP              not null comment '更新时间',
    DELETED                 INTEGER default 0      not null comment '逻辑删除',
    TENANT_ID               CHARACTER VARYING(200) not null comment '租户id'
);

-- 依赖包和依赖关联表
create table SY_LIB_PACKAGE_FILE
(
    ID                      CHARACTER VARYING(200) not null primary key unique comment '依赖包和依赖关联id',
    LIB_PACKAGE_ID          CHARACTER VARYING(200) not null comment '依赖包id',
    FILE_ID                 CHARACTER VARYING(200) not null comment '文件id',
    REMARK                  CHARACTER VARYING(500) comment '依赖包关联备注',
    VERSION_NUMBER          int                    not null comment '版本号',
    CREATE_BY               CHARACTER VARYING(200) not null comment '创建人',
    CREATE_DATE_TIME        TIMESTAMP              not null comment '创建时间',
    LAST_MODIFIED_BY        CHARACTER VARYING(200) not null comment '更新人',
    LAST_MODIFIED_DATE_TIME TIMESTAMP              not null comment '更新时间',
    DELETED                 INTEGER default 0      not null comment '逻辑删除',
    TENANT_ID               CHARACTER VARYING(200) not null comment '租户id'
);

-- 新增任务包配置字段
alter table SY_WORK_CONFIG
    add LIB_PACKAGE_CONFIG longtext;

-- 版本表中添加任务包配置字段
alter table SY_WORK_VERSION
    add LIB_PACKAGE_CONFIG longtext;
