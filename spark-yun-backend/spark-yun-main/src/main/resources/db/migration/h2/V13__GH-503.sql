-- 添加监控表
create table SY_MONITOR
(
  id                      varchar(200)  not null comment '分享表单链接id'
    primary key,
  cluster_id              varchar(200)  not null comment '集群id',
  cluster_node_id         varchar(200)  not null comment '集群节点id',
  status                  varchar(100)  not null comment '监控状态',
  log                     text          not null comment '日志',
  used_storage_size       long null comment '已使用存储',
  used_memory_size        long null comment '已使用内存',
  network_io_read_speed   long null comment '网络读速度',
  network_io_write_speed  long null comment '网络写速度',
  disk_io_read_speed      long null comment '磁盘读速度',
  disk_io_write_speed     long null comment '磁盘写速度',
  cpu_percent
    double null comment 'cpu占用',
  create_date_time        datetime      not null comment '创建时间',
  deleted                 int default 0 not null comment '逻辑删除',
  tenant_id               varchar(200)  not null comment '租户id'
);