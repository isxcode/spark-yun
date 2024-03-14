create table SY_CONTAINER
(
  ID                      varchar(200)      not null
    primary key
    unique comment '容器id',
  NAME                    varchar(200)      not null comment '容器名称',
  REMARK                  varchar(500) comment '容器备注',
  STATUS                  varchar(200)      not null comment '容器状态',
  DATASOURCE_ID           varchar(200)      not null comment '数据源id',
  CLUSTER_ID              varchar(200)      not null comment '集群id',
  RESOURCE_LEVEL          varchar(200)      not null comment '消耗资源等级',
  SPARK_CONFIG            varchar(2000) comment 'spark配置',
  PORT                    int comment '容器端口号',
  SUBMIT_LOG              text comment '容器端口号',
  RUNNING_LOG             text comment '容器端口号',
  APPLICATION_ID          varchar(200) comment '应用id',
  CREATE_BY               varchar(200)      not null comment '创建人',
  CREATE_DATE_TIME        TIMESTAMP         not null comment '创建时间',
  LAST_MODIFIED_BY        varchar(200)      not null comment '更新人',
  LAST_MODIFIED_DATE_TIME TIMESTAMP         not null comment '更新时间',
  DELETED                 INTEGER default 0 not null comment '逻辑删除',
  TENANT_ID               varchar(200)      not null comment '租户id'
);

-- 作业支持容器sql
alter table SY_WORK_CONFIG
    add CONTAINER_ID varchar(200);

-- 作业版本支持容器sql
alter table SY_WORK_VERSION
    add CONTAINER_ID varchar(200);