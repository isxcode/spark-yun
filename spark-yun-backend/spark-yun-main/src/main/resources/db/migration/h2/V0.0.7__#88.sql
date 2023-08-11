-- 给作业实例，添加作业流实例id
alter table SY_WORK_INSTANCE
  add workflow_instance_id varchar(100) null comment '工作流实例id';

-- 给作业实例，添加是否被定时器触发过
alter table SY_WORK_INSTANCE
  add quartz_has_run boolean null comment '是否被定时器触发过';

-- 创建工作流配置表
create table SY_WORKFLOW_CONFIG
(
  id                      varchar(200)  not null comment '作业流配置唯一id'
    primary key,
  web_config              CLOB          null comment '前端配置',
  node_mapping            varchar(2000) null comment '节点映射关系',
  node_list               varchar(2000) null comment '节点列表',
  dag_start_list          varchar(2000) null comment 'DAG开始节点列表',
  dag_end_list            varchar(2000) null comment 'DAG结束节点列表',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id',
  corn                    varchar(300)  null comment '定时时间'
);

-- 创建工作流实例表
create table SY_WORKFLOW_INSTANCE
(
  id                      varchar(200)  not null comment '实例唯一id'
    primary key,
  version_id              varchar(200)  null comment '实例版本id',
  flow_id                 varchar(200)  null comment '作业流id',
  instance_type           varchar(200)  null comment '实例类型',
  status                  varchar(200)  null comment '实例状态',
  run_log                 CLOB          null comment '作业流运行日志',
  web_config              CLOB          null comment '前端页面配置信息',
  plan_start_date_time    datetime      null comment '计划开始时间',
  next_plan_date_time     datetime      null comment '下一次开始时间',
  exec_start_date_time    datetime      null comment '执行开始时间',
  exec_end_date_time      datetime      null comment '执行结束时间',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 创建工作流版本表
create table SY_WORKFLOW_VERSION
(
  id                      varchar(200)  not null comment '工作流版本唯一id'
    primary key,
  workflow_id             varchar(200)  not null comment '作业流id',
  name                    varchar(200)  not null comment '作业流版本名称',
  workflow_type           varchar(200)  null comment '作业类型',
  node_mapping            CLOB          null comment '节点映射',
  node_list               CLOB          null comment '节点',
  dag_start_list          CLOB          null comment '开始节点',
  dag_end_list            CLOB          null comment '结束节点',
  work_version_map        CLOB          null comment '作业版本映射',
  corn                    varchar(200)  not null comment '定时表达式',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 工作流添加工作流配置id
alter table SY_WORKFLOW
  add config_id varchar(100) null comment '工作流配置id';

-- 工作流添加工作流当前版本id
alter table SY_WORKFLOW
  add version_id varchar(100) null comment '版本id';

-- 工作流添加工作流类型
alter table SY_WORKFLOW
  add type varchar(200) null comment '工作流类型';

-- 作业添加置顶标志
alter table SY_WORK
  add top_index int null comment '作业置顶标志';

-- 工作流收藏表
create table SY_WORKFLOW_FAVOUR
(
  id                      varchar(200)  not null comment '工作流收藏唯一id'
    primary key,
  workflow_id             varchar(200)  null comment '工作流唯一id',
  user_id                 varchar(200)  null comment '用户id',
  top_index               int           null comment 'top排序标志',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 自定义锁表
CREATE TABLE SY_LOCKER
(
  id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  name  VARCHAR(2000) not null,
  box VARCHAR(2000) null
);

alter table SY_CLUSTER_NODE alter column passwd varchar(3000) not null comment '节点服务器密码';
