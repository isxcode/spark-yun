-- 删除资源文件的路径
alter table SY_FILE drop column FILE_PATH;

-- 添加备注字段
alter table SY_FILE
  add REMARK varchar(500) comment '备注';

-- 添加自定义jar作业配置
alter table SY_WORK_CONFIG
  add JAR_JOB_CONFIG text comment '自定义jar作业配置';

-- 版本添加自定义作业配置
alter table SY_WORK_VERSION
  add JAR_JOB_CONFIG text comment '自定义作业配置';

-- 依赖配置
alter table SY_WORK_CONFIG
  add LIB_CONFIG text comment '作业依赖文件';

-- 自定义配置
alter table SY_WORK_CONFIG
  add FUNC_CONFIG text comment '自定义函数配置';

alter table SY_WORK_VERSION
  add LIB_CONFIG text comment '依赖配置';

alter table SY_WORK_VERSION
  add FUNC_CONFIG text comment '自定义函数配置';

-- 修改自定义函数表
alter table SY_WORK_UDF
    rename to SY_FUNC;

-- 加注释TYPE
alter table SY_FUNC
    modify type varchar(20) not null comment 'UDF或者UDAF';

-- 删除自定义函数的STATUS
alter table SY_FUNC
    drop column STATUS;

-- 添加自定义函数备注字段
alter table SY_FUNC
  add REMARK varchar(500) comment '备注';