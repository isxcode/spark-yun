alter table SY_WORK_CONFIG
  add corn varchar(200) null comment '定时表达式';

alter table SY_WORK
  add version_id varchar(200) null comment '作业当前最新版本号';

-- 作业配置版本表
create table if not exists SY_WORK_VERSION
(
  id                      varchar(200)  not null unique primary key comment '版本唯一id',
  work_id                 varchar(200)  not null comment '作业id',
  work_type               varchar(200)  not null comment '作业类型',
  datasource_id           varchar(200) comment '数据源id',
  cluster_id              varchar(200) comment '集群id',
  sql_script              varchar(2000) comment 'sql脚本',
  corn                    varchar(200)  not null comment '定时表达式',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 作业运行实例表
create table if not exists SY_WORK_INSTANCE
(
  id                      varchar(200)  not null unique primary key comment '实例唯一id',
  version_id              varchar(200) comment '实例版本id',
  work_id                 varchar(200) comment '作业id',
  instance_type           varchar(200) comment '实例类型',
  status                  varchar(200) comment '实例状态',
  plan_start_date_time    datetime comment '计划开始时间',
  next_plan_date_time     datetime comment '下一次开始时间',
  exec_start_date_time    datetime comment '执行开始时间',
  exec_end_date_time      datetime comment '执行结束时间',
  submit_log              varchar(2000) comment '提交日志',
  yarn_log                text comment 'yarn日志',
  spark_star_res          varchar(2000) comment 'spark-star插件返回',
  result_data             text comment '结果数据',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 用户行为记录表
create table if not exists SY_USER_ACTION
(
  id               varchar(200) not null unique primary key comment '用户行为唯一id',
  user_id          varchar(200) comment '用户id',
  tenant_id        varchar(200) comment '租户id',
  req_path         varchar(200) comment '请求路径',
  req_method       varchar(200) comment '请求方式',
  req_header       varchar(2000) comment '请求头',
  req_body         text comment '请求体',
  res_body         text comment '响应体',
  start_timestamp  long comment '开始时间戳',
  end_timestamp    long comment '结束时间戳',
  create_by        varchar(200) not null comment '创建人',
  create_date_time datetime     not null comment '创建时间'
);

-- 自定义表单表
create table if not exists SY_FORM
(
  id                      varchar(200)  not null unique primary key comment '自定义表单唯一id',
  name                    varchar(200)  not null comment '表单名称',
  datasource_id           varchar(200)  not null comment '数据源id',
  main_table              varchar(200) comment '主要对象表id',
  status                  varchar(200) comment '自定义表单状态',
  insert_sql              varchar(2000) not null comment '增sql语句',
  delete_sql              varchar(2000) not null comment '删sql语句',
  update_sql              varchar(2000) not null comment '改sql语句',
  select_sql              varchar(2000) not null comment '查sql语句',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 自定义表单组件表
create table if not exists SY_FORM_COMPONENT
(
  id                      varchar(200)  not null unique primary key comment '表单字段组件唯一id',
  form_id                 varchar(200) comment '自定义表单id',
  name                    varchar(200) comment '字段名称',
  component_type          varchar(200) comment '组件类型',
  component_key           varchar(200) comment '组件Key',
  is_display              boolean comment '是否显示组件',
  is_primary_key          boolean comment '是否为主键',
  show_value              varchar(200) comment '显示的值',
  value_sql               varchar(2000) comment '来源值查询sql',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 添加测试连接日志
alter table SY_DATASOURCE
  add connect_log varchar(2000) null comment '测试连接日志' after db_type;

-- 添加节点安装日志
alter table SY_CLUSTER_NODE
  add agent_log varchar(2000) null comment '代理日志';

-- 可以为null
alter table SY_CLUSTER_NODE
  modify hadoop_home_path varchar(200) null comment 'hadoop家目录';

-- 关闭节点的乐观锁
alter table SY_CLUSTER_NODE
  drop column version_number
