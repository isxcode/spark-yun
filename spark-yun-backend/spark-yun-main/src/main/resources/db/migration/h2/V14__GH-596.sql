-- standalone集群节点支持安装spark-local组件
alter table SY_CLUSTER_NODE
  add INSTALL_SPARK_LOCAL BOOLEAN default FALSE;

comment on column SY_CLUSTER_NODE.INSTALL_SPARK_LOCAL is '是否安装spark-local组件';