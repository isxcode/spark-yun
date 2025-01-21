-- 添加数据大屏分享的链接表
CREATE TABLE SY_VIEW_LINK
(
  id                      varchar(200) NOT NULL,
  form_id                 varchar(200) NOT NULL,
  form_version            varchar(200) NOT NULL,
  form_token              varchar(500) NOT NULL,
  create_by               varchar(200) NOT NULL,
  invalid_date_time       timestamp    NOT NULL,
  create_date_time        timestamp    NOT NULL,
  last_modified_by        varchar(200) NOT NULL,
  last_modified_date_time timestamp    NOT NULL,
  version_number          int          NOT NULL,
  deleted                 int          NOT NULL DEFAULT 0,
  tenant_id               varchar(200) NOT NULL,
  PRIMARY KEY (id)
);

-- 为每个列添加注释
COMMENT ON COLUMN SY_FORM_LINK.id IS '数据大屏分享链接id';
COMMENT ON COLUMN SY_FORM_LINK.form_id IS '大屏id';
COMMENT ON COLUMN SY_FORM_LINK.form_version IS '大屏版本';
COMMENT ON COLUMN SY_FORM_LINK.form_token IS '分享大屏的匿名token';
COMMENT ON COLUMN SY_FORM_LINK.create_by IS '创建人';
COMMENT ON COLUMN SY_FORM_LINK.invalid_date_time IS '到期时间';
COMMENT ON COLUMN SY_FORM_LINK.create_date_time IS '创建时间';
COMMENT ON COLUMN SY_FORM_LINK.last_modified_by IS '更新人';
COMMENT ON COLUMN SY_FORM_LINK.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN SY_FORM_LINK.version_number IS '版本号';
COMMENT ON COLUMN SY_FORM_LINK.deleted IS '逻辑删除';
COMMENT ON COLUMN SY_FORM_LINK.tenant_id IS '租户id';