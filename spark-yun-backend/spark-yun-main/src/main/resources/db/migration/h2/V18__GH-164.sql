-- 添加消息体表
create table SY_MESSAGE
(
  id                      varchar(200)  not null comment '消息消息体id'
    primary key,
  name                    varchar(200)  not null comment '消息体名称',
  remark                  varchar(500) comment '消息体备注',
  status                  varchar(100)  not null comment '消息体状态',
  msg_type                varchar(200)  not null comment '消息体类型，邮箱/阿里短信/飞书',
  msg_config              text          not null comment '消息体配置信息',
  response                text comment '检测响应',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 添加告警表
create table SY_ALARM
(
  id                      varchar(200)  not null comment '告警id'
    primary key,
  name                    varchar(200)  not null comment '告警名称',
  remark                  varchar(500) comment '告警备注',
  status                  varchar(100)  not null comment '消息状态，启动/停止',
  alarm_type              varchar(200)  not null comment '告警类型，作业/作业流',
  alarm_event             varchar(200)  not null comment '告警的事件，开始运行/运行失败/运行结束',
  msg_id                  varchar(100)  not null comment '使用的消息体',
  alarm_template          text          not null comment '告警的模版',
  receiver_list           text          null comment '告警接受者',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);

-- 添加消息告警实例表
create table SY_ALARM_INSTANCE
(
  id               varchar(200)  not null comment '告警实例id'
    primary key,
  alarm_id         varchar(200)  not null comment '告警id',
  send_status      varchar(100)  not null comment '是否发送成功',
  alarm_type       varchar(200)  not null comment '告警类型，作业/作业流',
  alarm_event      varchar(200)  not null comment '触发的事件',
  msg_id           varchar(100)  not null comment '告警的消息体',
  content          text          not null comment '发送消息的内容',
  response         text          not null comment '事件响应',
  instance_id      varchar(200)  not null comment '任务实例id',
  receiver         varchar(200)  not null comment '消息接受者',
  send_date_time   datetime      not null comment '发送的时间',
  create_date_time datetime      not null comment '创建时间',
  deleted          int default 0 not null comment '逻辑删除',
  tenant_id        varchar(200)  not null comment '租户id'
);

-- 作业添加基线
alter table SY_WORK_CONFIG
  add ALARM_LIST text;

comment on column SY_WORK_CONFIG.ALARM_LIST is '绑定的基线';

-- 作业流添加基线
alter table SY_WORKFLOW_CONFIG
  add ALARM_LIST text;

comment on column SY_WORKFLOW_CONFIG.ALARM_LIST is '绑定的基线';

-- 作业添加基线
alter table SY_WORK_VERSION
  add ALARM_LIST text;

comment on column SY_WORK_VERSION.ALARM_LIST is '绑定的基线';

-- 作业流添加基线
alter table SY_WORKFLOW_VERSION
  add ALARM_LIST text;

comment on column SY_WORKFLOW_VERSION.ALARM_LIST is '绑定的基线';


