-- 大屏组件
create table SY_VIEW_CARD
(
  ID                      VARCHAR(200)  not null primary key unique comment '大屏组件id',
  NAME                    VARCHAR(200)  not null comment '大屏组件名称',
  REMARK                  VARCHAR(500) comment '大屏组件备注',
  STATUS                  VARCHAR(200)  not null comment '大屏组件状态',
  TYPE                    VARCHAR(200)  not null comment '大屏组件类型',
  DATASOURCE_ID           VARCHAR(200)  not null comment '数据源id',
  EXAMPLE_DATA            VARCHAR(2000) not null comment '示例数据sql',
  WEB_CONFIG              text comment '前端显示配置',
  DATA_SQL                VARCHAR(2000) comment '数据sql',
  VERSION_NUMBER          int           not null comment '版本号',
  CREATE_BY               VARCHAR(200)  not null comment '创建人',
  CREATE_DATE_TIME        Datetime      not null comment '创建时间',
  LAST_MODIFIED_BY        VARCHAR(200)  not null comment '更新人',
  LAST_MODIFIED_DATE_TIME Datetime      not null comment '更新时间',
  DELETED                 INT default 0 not null comment '逻辑删除',
  TENANT_ID               VARCHAR(200)  not null comment '租户id'
);

-- 数据大屏
create table SY_VIEW
(
  ID                      VARCHAR(200)  not null primary key unique comment '数据大屏id',
  NAME                    VARCHAR(200)  not null comment '大屏名称',
  REMARK                  VARCHAR(500) comment '大屏备注',
  STATUS                  VARCHAR(200)  not null comment '大屏状态',
  BACKGROUND_FILE_ID      VARCHAR(200) comment '背景图文件id',
  CARD_LIST               VARCHAR(2000) comment '大屏中包含的卡片',
  WEB_CONFIG              text comment '大屏显示配置',
  VERSION_NUMBER          int           not null comment '版本号',
  CREATE_BY               VARCHAR(200)  not null comment '创建人',
  CREATE_DATE_TIME        Datetime      not null comment '创建时间',
  LAST_MODIFIED_BY        VARCHAR(200)  not null comment '更新人',
  LAST_MODIFIED_DATE_TIME Datetime      not null comment '更新时间',
  DELETED                 INT default 0 not null comment '逻辑删除',
  TENANT_ID               VARCHAR(200)  not null comment '租户id'
);