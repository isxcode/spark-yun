-- 创建资源文件表
create table SY_FILE
(
  id                      varchar(200)  not null comment '文件配置唯一id'
    primary key,
  file_name               varchar(200) null comment '文件名称',
  file_size               varchar(200) null comment '文件大小',
  file_path               varchar(200) null comment '文件存储路径',
  file_type               varchar(200) null comment '文件类型',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id',
  constraint id
    unique (id)
);

-- 添加同步作业的配置
alter table SY_WORK_CONFIG
  add sync_work_config text null comment '同步作业的配置' after corn;
