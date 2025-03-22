-- 单点登录信息表
CREATE TABLE SY_SSO_AUTH (
    id                      VARCHAR(200) NOT NULL COMMENT '单点id',
    name                    VARCHAR(200) NOT NULL COMMENT '单点名称',
    status                  VARCHAR(200) NOT NULL COMMENT '单点状态',
    sso_type                VARCHAR(200) NOT NULL COMMENT '单点类型',
    client_id               VARCHAR(200) NOT NULL COMMENT 'clientId',
    client_secret           VARCHAR(500) NOT NULL COMMENT 'clientSecret',
    scope                   VARCHAR(500) NULL COMMENT 'scope',
    auth_url                VARCHAR(500) NOT NULL COMMENT '授权认证地址',
    access_token_url        VARCHAR(500) NOT NULL COMMENT 'token获取地址',
    redirect_url            VARCHAR(500) NOT NULL COMMENT '跳转地址',
    user_url                VARCHAR(500) NOT NULL COMMENT '获取用户信息地址',
    auth_json_path          VARCHAR(500) NOT NULL COMMENT '解析用户信息jsonPath',
    remark                  VARCHAR(500) NULL COMMENT '单点备注',
    create_by               VARCHAR(200) NOT NULL COMMENT '创建人',
    create_date_time        DATETIME NOT NULL COMMENT '创建时间',
    last_modified_by        VARCHAR(200) NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
    version_number          INT NOT NULL COMMENT '版本号',
    deleted                 INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
    PRIMARY KEY (id)
) COMMENT='单点登录认证表';