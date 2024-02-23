-- 删除资源文件的路径
alter table SY_FILE
    drop column FILE_PATH;

-- 添加备注字段
alter table SY_FILE
    add REMARK varchar2(500);
comment on column SY_FILE.REMARK is '备注';

-- 添加自定义jar作业配置
alter table SY_WORK_CONFIG
    add JAR_JOB_CONFIG text;

comment on column SY_WORK_CONFIG.JAR_JOB_CONFIG is '自定义jar作业配置';

-- 版本添加自定义作业配置
alter table SY_WORK_VERSION
    add JAR_JOB_CONFIG text;

comment on column SY_WORK_VERSION.JAR_JOB_CONFIG is '自定义作业配置';

-- 依赖配置
alter table SY_WORK_CONFIG
    add LIB_CONFIG text;

comment on column SY_WORK_CONFIG.LIB_CONFIG is '作业依赖文件';

-- 自定义配置
alter table SY_WORK_CONFIG
    add FUNC_CONFIG text;

comment on column SY_WORK_CONFIG.FUNC_CONFIG is '自定义函数配置';

alter table SY_WORK_VERSION
    add LIB_CONFIG text;

comment on column SY_WORK_VERSION.LIB_CONFIG is '依赖配置';

alter table SY_WORK_VERSION
    add FUNC_CONFIG text;

comment on column SY_WORK_VERSION.FUNC_CONFIG is '自定义函数配置';
