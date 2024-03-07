create table SY_REAL
(
  id                      varchar(200)  not null comment '分享表单链接id'
    primary key,
  name                    varchar(200)  not null comment '实时作业名称',
  status                  varchar(200)  not null comment '运行状态',
  cluster_id              varchar(500)  not null comment '集群id',
  spark_config            text          not null comment '集群配置',
  sync_config             text comment '数据同步配置',
  lib_config              varchar(500) comment '依赖配置',
  func_config             varchar(500) comment '函数配置',
  submit_log              text comment '提交日志',
  running_log             text comment '运行日志',
  remark                  varchar(500) comment '备注',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);