alter table SY_API
    modify api_sql varchar(2000) null comment '执行的sql';

-- 添加分享表单的链接表
create table SY_FORM_LINK
(
  id                      varchar(200)  not null comment '分享表单链接id'
    primary key,
  form_id                 varchar(200)  not null comment '表单id',
  form_version            varchar(200)  not null comment '表单版本',
  form_token              varchar(500)  not null comment '分享表单的匿名token',
  create_by               varchar(200)  not null comment '创建人',
  invalid_date_time       datetime      not null comment '到期时间',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);
