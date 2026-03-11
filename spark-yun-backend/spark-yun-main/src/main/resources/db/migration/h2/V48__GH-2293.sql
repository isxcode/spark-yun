-- API黑白名单配置表
create table SY_API_ACCESS_RULE
(
    ID                      CHARACTER VARYING(200)  not null primary key unique comment '黑白名单id',
    NAME                    CHARACTER VARYING(200)  not null comment '规则名称',
    RULE_TYPE               CHARACTER VARYING(200)  not null comment '规则类型: WHITELIST/BLACKLIST',
    IP_ADDRESS              CHARACTER VARYING(2000) not null comment 'IP地址，多个用换行分隔，支持正则',
    REMARK                  CHARACTER VARYING(500)  comment '备注',
    VERSION_NUMBER          int                     not null comment '版本号',
    CREATE_BY               CHARACTER VARYING(200)  not null comment '创建人',
    CREATE_DATE_TIME        TIMESTAMP               not null comment '创建时间',
    LAST_MODIFIED_BY        CHARACTER VARYING(200)  not null comment '更新人',
    LAST_MODIFIED_DATE_TIME TIMESTAMP               not null comment '更新时间',
    DELETED                 INTEGER default 0       not null comment '逻辑删除',
    TENANT_ID               CHARACTER VARYING(200)  not null comment '租户id'
);

-- API表添加黑白名单关联字段
alter table SY_API
    add ACCESS_RULE_ID CHARACTER VARYING(200) comment '黑白名单配置id';
