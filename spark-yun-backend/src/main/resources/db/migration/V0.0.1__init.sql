-- 用户表
create table if not exists SY_USER
(
  id       varchar(200) null unique comment '用户唯一id',
  username varchar(200) null comment '用户名称',
  account  varchar(200) null comment '账号',
  passwd   varchar(200) null comment '密码'
);

-- 计算引擎表
create table if not exists SY_CALCULATE_ENGINE
(
  id              varchar(200) null unique comment '计算引擎唯一id',
  name            varchar(100) null comment '引擎名称',
  comment_info    varchar(100) null comment '备注',
  status          varchar(100) null comment '健康状态',
  check_date_time datetime     null comment '检测健康状态时间',
  all_node        int          null comment '总节点数',
  active_node     int          null comment '激活节点数',
  all_memory      double       null comment '所有可用内存数',
  used_memory     double       null comment '已使用内存数',
  all_storage     double       null comment '所有存储',
  used_storage    double       null comment '已使用存储'
);

-- 引擎节点
create table if not exists SY_ENGINE_NODE
(
  id                  varchar(100) null unique comment '节点唯一id',
  name                varchar(100) null comment '节点名称',
  comment_info        varchar(100) null comment '备注',
  status              varchar(100) null comment '状态',
  check_date_time     datetime     null comment '检查健康时间',
  all_memory          double       null comment '节点所有内存',
  used_memory         double       null comment '已使用内存',
  all_storage         double       null comment '所有存储',
  used_storage        double       null comment '已使用存储',
  cpu_percent         varchar(100) null comment 'CPU使用占比',
  calculate_engine_id varchar(100) null comment '属于计算引擎',
  host                varchar(100) null comment '服务器host',
  port                varchar(100) null comment '服务器端口号',
  username            varchar(100) null comment '服务器用户名',
  passwd              varchar(100) null comment '服务器密码',
  agent_home_path     varchar(100) null comment '代理安装路径',
  agent_port          varchar(100) null comment '代理端口号',
  hadoop_home_path    varchar(100) null comment 'hadoop的home目录'
);

-- 数据源
create table if not exists SY_DATASOURCE
(
  id              varchar(100) null unique comment '数据源唯一id',
  name            varchar(100) null comment '数据源名称',
  jdbc_url        varchar(200) null comment '数据源url',
  comment_info    varchar(100) null comment '备注',
  status          varchar(100) null comment '状态',
  check_date_time datetime     null comment '检测时间',
  username        varchar(100) null comment '用户名称',
  passwd          varchar(100) null comment '密码',
  datasource_type varchar(100) null comment '数据源类型'
);

-- 作业流
create table if not exists SY_WORKFLOW
(
  id           varchar(100) null unique comment '作业流唯一id',
  name         varchar(100) null comment '作业流名称',
  comment_info varchar(100) null comment '备注',
  status       varchar(100) null comment '作业流状态'
);

-- 作业
create table if not exists SY_WORK
(
  id               varchar(100) null unique comment '作业唯一id',
  name             varchar(100) null comment '作业名称',
  comment_info     varchar(100) null comment '备注',
  status           varchar(100) null comment '状态',
  work_type        varchar(100) null comment '作业类型',
  work_config_id   varchar(100) null comment '作业配置id',
  create_date_time datetime     null comment '作业创建时间',
  workflow_id      varchar(100) null comment '所属作业流id'
);

-- 作业配置
create table if not exists SY_WORK_CONFIG
(
  id                  varchar(100)  null unique comment '作业配置唯一id',
  datasource_id       varchar(100)  null comment '数据源id',
  calculate_engine_id varchar(100)  null comment '计算引擎id',
  sql                 varchar(2000) null comment 'sql'
);

insert into SY_USER (id, username, account, passwd) values ('admin_id', '管理员', 'admin', 'admin123');
