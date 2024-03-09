-- 添加接口调用作业配置
alter table SY_WORK_CONFIG
  add API_WORK_CONFIG varchar(1000000000) null comment '接口调用作业的配置';