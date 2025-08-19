-- standalone集群节点支持安装flink-local组件
alter table SY_CLUSTER_NODE
  add INSTALL_FLINK_LOCAL bool default false null comment '是否安装flink-local组件';

alter table SY_CLUSTER_NODE
    add FLINK_HOME_PATH varchar(200) null comment 'standalone模式flink的安装目录';