-- standalone集群节点支持安装spark-local组件
alter table SY_CLUSTER_NODE
  add INSTALL_SPARK_LOCAL bool default false null comment '是否安装spark-local组件';
