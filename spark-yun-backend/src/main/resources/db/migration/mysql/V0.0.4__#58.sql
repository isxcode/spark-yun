-- 租户表
create table if not exists SY_TENANT
(
  id                      varchar(200)  not null unique primary key comment '租户唯一id',
  name                    varchar(200)  not null comment '租户名称',
  used_member_num         int           not null comment '已使用成员数',
  max_member_num          int           not null comment '最大成员数',
  used_workflow_num       int           not null comment '已使用作业流数',
  max_workflow_num        int           not null comment '最大作业流数',
  status                  varchar(200)  not null comment '租户状态',
  introduce               varchar(500) comment '租户简介',
  remark                  varchar(500) comment '租户描述',
  check_date_time         datetime      not null comment '检测时间',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除'
);

-- 用户表
create table if not exists SY_USER
(
  id                      varchar(200)  not null unique primary key comment '用户唯一id',
  username                varchar(200)  not null comment '用户名称',
  account                 varchar(200)  not null unique comment '用户账号',
  passwd                  varchar(200)  not null comment '账号密码',
  phone                   varchar(200) comment '手机号',
  email                   varchar(200) comment '邮箱',
  introduce               varchar(500) comment '简介',
  remark                  varchar(500) comment '描述',
  role_code               varchar(200)  not null comment '角色编码',
  status                  varchar(200)  not null comment '用户状态',
  create_by               varchar(200)  not null comment '创建人',
  current_tenant_id       varchar(200) comment '当前用户使用的租户id',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除'
);

-- 租户用户关系表
create table if not exists SY_TENANT_USERS
(
  id                      varchar(200)  not null unique primary key comment '关系唯一id',
  user_id                 varchar(200)  not null comment '用户id',
  tenant_id               varchar(200)  not null comment '租户id',
  role_code               varchar(200)  not null comment '角色编码',
  status                  varchar(200)  not null comment '用户状态',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除'
);

-- 集群表
create table if not exists SY_CLUSTER
(
  id                      varchar(200)  not null unique primary key comment '集群唯一id',
  name                    varchar(200)  not null comment '集群名称',
  remark                  varchar(500) comment '集群描述',
  status                  varchar(200)  not null comment '集群状态',
  check_date_time         datetime      not null comment '检测时间',
  all_node_num            int           not null comment '所有节点',
  active_node_num         int           not null comment '激活节点数',
  all_memory_num          double        not null comment '所有内存',
  used_memory_num         double        not null comment '已使用内存',
  all_storage_num         double        not null comment '所有存储',
  used_storage_num        double        not null comment '已使用存储',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 集群节点表
create table if not exists SY_CLUSTER_NODE
(
  id                      varchar(200)  not null unique primary key comment '集群节点唯一id',
  name                    varchar(200)  not null comment '节点名称',
  remark                  varchar(500) comment '节点描述',
  status                  varchar(200)  not null comment '节点状态',
  check_date_time         datetime      not null comment '检测时间',
  all_memory              double        not null comment '所有内存',
  used_memory             double        not null comment '已使用内存',
  all_storage             double        not null comment '所有存储',
  used_storage            double        not null comment '已使用存储',
  cpu_percent             double        not null comment 'cpu使用占比',
  cluster_id              varchar(200)  not null comment '集群id',
  host                    varchar(200)  not null comment '节点服务器host',
  port                    int           not null comment '节点服务器端口号',
  username                varchar(200)  not null comment '节点服务器用户名',
  passwd                  varchar(200)  not null comment '节点服务器',
  agent_home_path         varchar(200)  not null comment '至轻云代理安装目录',
  agent_port              varchar(200)  not null comment '至轻云代理服务端口号',
  hadoop_home_path        varchar(200)  not null comment 'hadoop家目录',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 数据源表
create table if not exists SY_DATASOURCE
(
  id                      varchar(200)  not null unique primary key comment '数据源唯一id',
  name                    varchar(200)  not null comment '数据源名称',
  jdbc_url                varchar(500)  not null comment '数据源jdbcUrl',
  remark                  varchar(500) comment '描述',
  status                  varchar(200)  not null comment '状态',
  check_date_time         datetime      not null comment '检测时间',
  username                varchar(200) comment '数据源用户名',
  passwd                  varchar(200) comment '数据源密码',
  db_type                 varchar(200)  not null comment '数据源类型',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 作业流表
create table if not exists SY_WORKFLOW
(
  id                      varchar(200)  not null unique primary key comment '作业流唯一id',
  name                    varchar(200)  not null comment '作业流名称',
  remark                  varchar(500) comment '作业流描述',
  status                  varchar(200)  not null comment '状态',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 作业表
create table if not exists SY_WORK
(
  id                      varchar(200)  not null unique primary key comment '作业唯一id',
  name                    varchar(200)  not null comment '作业名称',
  remark                  varchar(500) comment '作业描述',
  status                  varchar(200)  not null comment '作业状态',
  work_type               varchar(200)  not null comment '作业类型',
  config_id               varchar(200)  not null comment '作业配置id',
  workflow_id             varchar(200)  not null comment '作业流id',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 作业配置表
create table if not exists SY_WORK_CONFIG
(
  id                      varchar(200)  not null unique primary key comment '作业配置唯一id',
  datasource_id           varchar(200) comment '数据源id',
  cluster_id              varchar(200) comment '集群id',
  sql_script              varchar(2000) comment 'sql脚本',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 初始化系统管理员
insert into SY_USER (id, username, account, passwd, role_code, status, create_by, create_date_time, last_modified_by,
                     last_modified_date_time, version_number)
values ('admin_id', '系统管理员', 'admin', 'admin123', 'ROLE_SYS_ADMIN', 'ENABLE', 'admin_id', now(), 'admin_id', now(),
        0);

-- 许可证表
create table if not exists SY_LICENSE
(
  id                      varchar(200)  not null unique primary key comment '许可证唯一id',
  code                    varchar(200)  not null comment '许可证编号',
  company_name            varchar(200)  not null comment '公司名称',
  logo                    varchar(2000) not null comment '公司logo',
  remark                  varchar(2000) comment '许可证备注',
  issuer                  varchar(200)  not null comment '许可证签发人',
  start_date_time         datetime      not null comment '许可证起始时间',
  end_date_time           datetime      not null comment '许可证到期时间',
  max_tenant_num          int comment '最大租户数',
  max_member_num          int comment '最大成员数',
  max_workflow_num        int comment '最大作业流数',
  status                  varchar(200)  not null comment '证书状态',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除'
);

-- 自定义API表
create table if not exists SY_API
(
  id                      varchar(200)  not null unique primary key comment '唯一API的id',
  name                    varchar(200)  not null comment 'API名称',
  path                    varchar(200)  not null comment 'API访问地址',
  api_type                varchar(200)  not null comment 'API类型',
  remark                  varchar(2000) comment 'API备注',
  req_header              varchar(2000) comment '请求头',
  req_body                varchar(2000) comment '请求体',
  api_sql                 varchar(2000) not null comment '执行的sql',
  res_body                varchar(2000) not null comment '响应体',
  datasource_id           varchar(200)  not null comment '数据源id',
  status                  varchar(200)  not null comment 'API状态',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);










