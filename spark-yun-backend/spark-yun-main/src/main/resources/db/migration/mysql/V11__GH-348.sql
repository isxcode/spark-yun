-- 添加接口调用作业配置
alter table SY_WORK_CONFIG
  add API_WORK_CONFIG text null comment '接口调用作业的配置';

-- 版本中添加接口调用作业配置
alter table SY_WORK_VERSION
  add API_WORK_CONFIG text null comment '接口调用作业的配置';