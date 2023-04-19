-- 修改用户表
alter table SY_USER add phone varchar(200) null unique comment '手机号';
alter table SY_USER add email varchar(200) null unique comment '邮箱';
alter table SY_USER add comment_info varchar(200) null unique comment '备注';
alter table SY_USER add status varchar(200) null unique comment '用户状态';
alter table SY_USER add latest_tenant_id varchar(200) null unique comment '最近租户id';
alter table SY_USER add create_date_time datetime comment '创建时间';
alter table SY_USER add avatar_url datetime comment '头像url';

-- 租户表
create table if not exists SY_TENANT
(
  id               varchar(200) null unique comment '租户唯一id',
  name             varchar(200) null comment '租户名称',
  theme_color      varchar(200) null comment '租户主题色名称',
  max_member_num   varchar(200) null comment '最大成员数',
  max_workflow_num varchar(200) null comment '最大作业流数量',
  status           varchar(200) null comment '当前租户状态',
  avatar_url       varchar(200) null comment '租户头像url',
  introduce        varchar(200) null comment '简介',
  comment_info     varchar(200) null comment '备注',
  create_date_time datetime     null comment '创建时间',
  check_date_time  datetime     null comment '检测时间'
);

-- 租户用户关系表
create table if not exists SY_TENANT_USERS
(
  id          varchar(200) null unique comment '关联关系唯一id',
  user_id     varchar(200) null comment '用户id',
  tenant_id   varchar(200) null comment '租户id',
  tenant_role varchar(200) null comment '租户角色'
);

-- 证书列表
create table if not exists SY_LICENSE
(
  id          varchar(200) null unique comment '关联关系唯一id',
  user_id     varchar(200) null comment '用户id',
  tenant_id   varchar(200) null comment '租户id',
  tenant_role varchar(200) null comment '租户角色'
);
