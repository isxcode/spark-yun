-- 新增udf定义表
create table SY_WORK_UDF
(
  id                      varchar(200)  not null comment 'udf唯一id'
    primary key,
  type                    varchar(20)   not null comment 'udf类型',
  file_id                 varchar(200)  not null comment 'jar文件id',
  func_name               varchar(200)  not null comment 'udf调用方法名',
  class_name              varchar(200)  not null comment 'udf方法class名称',
  result_type             varchar(200)  not null comment '返回值类型',
  status                  boolean       not null comment '是否启用此udf',
  create_by               varchar(200)  not null comment '创建人',
  create_date_time        datetime      not null comment '创建时间',
  last_modified_by        varchar(200)  not null comment '更新人',
  last_modified_date_time datetime      not null comment '更新时间',
  version_number          int           not null comment '版本号',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);