alter table SY_CLUSTER
  add cluster_type varchar(100) null comment '集群的类型';

update SY_CLUSTER
set cluster_type='yarn'
where 1 = 1;

