-- standalone集群节点支持安装flink-local组件
alter table SY_CLUSTER_NODE
  add INSTALL_FLINK_LOCAL BOOLEAN default FALSE;

comment on column SY_CLUSTER_NODE.INSTALL_FLINK_LOCAL is '是否安装spark-local组件';

-- 添加flink_home_path
alter table SY_CLUSTER_NODE
    add flink_home_path varchar(200) null comment 'standalone模式flink的安装目录';