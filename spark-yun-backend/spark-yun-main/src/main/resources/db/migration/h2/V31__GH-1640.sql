-- 单点登录信息表
create table SY_SSO_AUTH
(
    id                      varchar(200)  not null comment '单点id' primary key,
    name                    varchar(200)  not null comment '单点名称',
    status                  varchar(200)  not null comment '单点状态',
    sso_type                varchar(200)  not null comment '单点类型',
    client_id               varchar(200)  not null comment 'clientId',
    client_secret           varchar(500)  not null comment 'clientSecret',
    scope                   varchar(500)  null comment 'scope',
    auth_url                varchar(500)  not null comment '授权认证地址',
    access_token_url        varchar(500)  not null comment 'token获取地址',
    redirect_url            varchar(500)  not null comment '跳转地址',
    user_url                varchar(500)  not null comment '获取用户信息地址',
    auth_json_path          varchar(500)  not null comment '解析用户信息jsonPath',
    remark                  varchar(500) comment '单点备注',
    create_by               varchar(200)  not null comment '创建人',
    create_date_time        datetime      not null comment '创建时间',
    last_modified_by        varchar(200)  not null comment '更新人',
    last_modified_date_time datetime      not null comment '更新时间',
    version_number          int           not null comment '版本号',
    deleted                 int default 0 not null comment '逻辑删除'
);