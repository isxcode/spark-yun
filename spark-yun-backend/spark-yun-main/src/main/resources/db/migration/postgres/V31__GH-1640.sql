-- 单点登录信息表
CREATE TABLE SY_SSO_AUTH (
    id                      VARCHAR(200) NOT NULL PRIMARY KEY,
    name                    VARCHAR(200) NOT NULL,
    status                  VARCHAR(200) NOT NULL,
    sso_type                VARCHAR(200) NOT NULL,
    client_id               VARCHAR(200) NOT NULL,
    client_secret           VARCHAR(500) NOT NULL,
    scope                   VARCHAR(500),
    auth_url                VARCHAR(500) NOT NULL,
    access_token_url        VARCHAR(500) NOT NULL,
    redirect_url            VARCHAR(500) NOT NULL,
    user_url                VARCHAR(500) NOT NULL,
    auth_json_path          VARCHAR(500) NOT NULL,
    remark                  VARCHAR(500),
    create_by               VARCHAR(200) NOT NULL,
    create_date_time        TIMESTAMP NOT NULL,
    last_modified_by        VARCHAR(200) NOT NULL,
    last_modified_date_time TIMESTAMP NOT NULL,
    version_number          INTEGER NOT NULL,
    deleted                 INTEGER DEFAULT 0 NOT NULL
);
COMMENT ON TABLE SY_SSO_AUTH IS '单点登录认证表';
COMMENT ON COLUMN SY_SSO_AUTH.id IS '单点id';
COMMENT ON COLUMN SY_SSO_AUTH.name IS '单点名称';
COMMENT ON COLUMN SY_SSO_AUTH.status IS '单点状态';
COMMENT ON COLUMN SY_SSO_AUTH.sso_type IS '单点类型';
COMMENT ON COLUMN SY_SSO_AUTH.client_id IS 'clientId';
COMMENT ON COLUMN SY_SSO_AUTH.client_secret IS 'clientSecret';
COMMENT ON COLUMN SY_SSO_AUTH.scope IS 'scope';
COMMENT ON COLUMN SY_SSO_AUTH.auth_url IS '授权认证地址';
COMMENT ON COLUMN SY_SSO_AUTH.access_token_url IS 'token获取地址';
COMMENT ON COLUMN SY_SSO_AUTH.redirect_url IS '跳转地址';
COMMENT ON COLUMN SY_SSO_AUTH.user_url IS '获取用户信息地址';
COMMENT ON COLUMN SY_SSO_AUTH.auth_json_path IS '解析用户信息jsonPath';
COMMENT ON COLUMN SY_SSO_AUTH.remark IS '单点备注';
COMMENT ON COLUMN SY_SSO_AUTH.create_by IS '创建人';
COMMENT ON COLUMN SY_SSO_AUTH.create_date_time IS '创建时间';
COMMENT ON COLUMN SY_SSO_AUTH.last_modified_by IS '更新人';
COMMENT ON COLUMN SY_SSO_AUTH.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN SY_SSO_AUTH.version_number IS '版本号';
COMMENT ON COLUMN SY_SSO_AUTH.deleted IS '逻辑删除';