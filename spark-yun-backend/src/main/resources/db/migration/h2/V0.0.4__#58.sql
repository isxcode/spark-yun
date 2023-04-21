-- 租户表
create table if not exists SY_TENANT
(
  id                      varchar(200)  not null unique primary key,
  name                    varchar(200)  not null,
  max_member_num          int           not null,
  max_workflow_num        int           not null,
  status                  varchar(200)  not null,
  introduce               varchar(500),
  remark                  varchar(500),
  check_date_time         datetime      not null,
  create_by               varchar(200)  not null,
  create_date_time        datetime      not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time datetime      not null,
  version_number          int           not null,
  deleted                 int default 0 not null
);

-- 用户表
create table if not exists SY_USER
(
  id                      varchar(200)  not null unique primary key,
  username                varchar(200)  not null,
  account                 varchar(200)  not null unique,
  passwd                  varchar(200)  not null,
  phone                   varchar(200),
  email                   varchar(200),
  introduce               varchar(500),
  remark                  varchar(500),
  role_code               varchar(200)  not null,
  status                  varchar(200)  not null,
  create_by               varchar(200)  not null,
  create_date_time        datetime      not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time datetime      not null,
  version_number          int           not null,
  deleted                 int default 0 not null
);

-- 租户用户关系表
create table if not exists SY_TENANT_USERS
(
  id                      varchar(200)  not null unique primary key,
  user_id                 varchar(200)  not null,
  tenant_id               varchar(200)  not null,
  role_code               varchar(200)  not null,
  status                  varchar(200)  not null,
  create_by               varchar(200)  not null,
  create_date_time        datetime      not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time datetime      not null,
  version_number          int           not null,
  deleted                 int default 0 not null
);

-- 集群表
create table if not exists SY_CLUSTER
(
  id                      varchar(200)       not null unique primary key,
  name                    varchar(200)       not null,
  remark                  varchar(500),
  status                  varchar(200)       not null,
  check_date_time         datetime           not null,
  all_node_num            int    default 0   not null,
  active_node_num         int    default 0   not null,
  all_memory_num          double default 0.0 not null,
  used_memory_num         double default 0.0 not null,
  all_storage_num         double default 0.0 not null,
  used_storage_num        double default 0.0 not null,
  create_by               varchar(200)       not null,
  create_date_time        datetime           not null,
  last_modified_by        varchar(200)       not null,
  last_modified_date_time datetime           not null,
  version_number          int                not null,
  deleted                 int    default 0   not null
);

-- 集群节点表
create table if not exists SY_CLUSTER_NODE
(
  id                      varchar(200)       not null unique primary key,
  name                    varchar(200)       not null,
  remark                  varchar(500),
  status                  varchar(200)       not null,
  check_date_time         datetime           not null,
  all_memory              double default 0.0 not null,
  used_memory             double default 0.0 not null,
  all_storage             double default 0.0 not null,
  used_storage            double default 0.0 not null,
  cpu_percent             double default 0.0 not null,
  cluster_id              varchar(200)       not null,
  host                    varchar(200)       not null,
  port                    int    default 22  not null,
  username                varchar(200)       not null,
  passwd                  varchar(200),
  agent_home_path         varchar(200)       not null,
  agent_port              varchar(200)       not null,
  hadoop_home_path        varchar(200)       not null,
  create_by               varchar(200)       not null,
  create_date_time        datetime           not null,
  last_modified_by        varchar(200)       not null,
  last_modified_date_time datetime           not null,
  version_number          int                not null,
  deleted                 int    default 0   not null,
  tenant_id               varchar(200)       not null
);

-- 数据源表
create table if not exists SY_DATASOURCE
(
  id                      varchar(200)  not null unique primary key,
  name                    varchar(200)  not null,
  jdbc_url                varchar(500)  not null,
  remark                  varchar(500),
  status                  varchar(200)  not null,
  check_date_time         datetime      not null,
  username                varchar(200)  not null,
  passwd                  varchar(200),
  db_type                 varchar(200)  not null,
  create_by               varchar(200)  not null,
  create_date_time        datetime      not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time datetime      not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null
);

-- 作业流表
create table if not exists SY_WORKFLOW
(
  id                      varchar(200)  not null unique primary key,
  name                    varchar(200)  not null,
  remark                  varchar(500),
  status                  varchar(200)  not null,
  create_by               varchar(200)  not null,
  create_date_time        datetime      not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time datetime      not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null
);

-- 作业表
create table if not exists SY_WORK
(
  id                      varchar(200)  not null unique primary key,
  name                    varchar(200)  not null,
  remark                  varchar(500),
  status                  varchar(200)  not null,
  work_type               varchar(200)  not null,
  config_id               varchar(200)  not null,
  workflow_id             varchar(200)  not null,
  create_by               varchar(200)  not null,
  create_date_time        datetime      not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time datetime      not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null
);

-- 作业配置表
create table if not exists SY_WORK_CONFIG
(
  id                      varchar(200)  not null unique primary key,
  datasource_id           varchar(200),
  cluster_id              varchar(200),
  sql_script              varchar(2000),
  create_by               varchar(200)  not null,
  create_date_time        datetime      not null,
  last_modified_by        varchar(200)  not null,
  last_modified_date_time datetime      not null,
  version_number          int           not null,
  deleted                 int default 0 not null,
  tenant_id               varchar(200)  not null
);

-- 初始化系统管理员
insert into SY_USER (id, username, account, passwd, role_code, status, create_by, create_date_time, last_modified_by,
                     last_modified_date_time, version_number)
values ('admin_id', '系统管理员', 'admin', 'admin123', 'SYS_ADMIN', 'ENABLE', 'admin_id', now(), 'admin_id', now(), 1);
