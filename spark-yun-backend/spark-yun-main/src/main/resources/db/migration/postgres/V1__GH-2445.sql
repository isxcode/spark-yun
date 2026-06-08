CREATE TABLE IF NOT EXISTS sy_alarm
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  status VARCHAR(100) NOT NULL,
  remark VARCHAR(500),
  alarm_type VARCHAR(200) NOT NULL,
  alarm_event VARCHAR(200) NOT NULL,
  msg_id VARCHAR(100) NOT NULL,
  alarm_template TEXT NOT NULL,
  receiver_list TEXT,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_alarm.id IS '告警id';
COMMENT ON COLUMN sy_alarm.name IS '告警名称';
COMMENT ON COLUMN sy_alarm.status IS '消息状态，启动/停止';
COMMENT ON COLUMN sy_alarm.remark IS '告警备注';
COMMENT ON COLUMN sy_alarm.alarm_type IS '告警类型，作业/作业流';
COMMENT ON COLUMN sy_alarm.alarm_event IS '告警的事件，开始运行/运行失败/运行结束';
COMMENT ON COLUMN sy_alarm.msg_id IS '使用的消息体';
COMMENT ON COLUMN sy_alarm.alarm_template IS '告警的模版';
COMMENT ON COLUMN sy_alarm.receiver_list IS '告警接受者';
COMMENT ON COLUMN sy_alarm.create_by IS '创建人';
COMMENT ON COLUMN sy_alarm.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_alarm.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_alarm.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_alarm.version_number IS '版本号';
COMMENT ON COLUMN sy_alarm.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_alarm.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_alarm_instance
(
  id VARCHAR(200) NOT NULL,
  alarm_id VARCHAR(200) NOT NULL,
  send_status VARCHAR(100) NOT NULL,
  alarm_type VARCHAR(200) NOT NULL,
  alarm_event VARCHAR(200) NOT NULL,
  msg_id VARCHAR(100) NOT NULL,
  content TEXT NOT NULL,
  response TEXT NOT NULL,
  instance_id VARCHAR(200) NOT NULL,
  receiver VARCHAR(200) NOT NULL,
  send_date_time TIMESTAMP NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_alarm_instance.id IS '告警实例id';
COMMENT ON COLUMN sy_alarm_instance.alarm_id IS '告警id';
COMMENT ON COLUMN sy_alarm_instance.send_status IS '是否发送成功';
COMMENT ON COLUMN sy_alarm_instance.alarm_type IS '告警类型，作业/作业流';
COMMENT ON COLUMN sy_alarm_instance.alarm_event IS '触发的事件';
COMMENT ON COLUMN sy_alarm_instance.msg_id IS '告警的消息体';
COMMENT ON COLUMN sy_alarm_instance.content IS '发送消息的内容';
COMMENT ON COLUMN sy_alarm_instance.response IS '事件响应';
COMMENT ON COLUMN sy_alarm_instance.instance_id IS '任务实例id';
COMMENT ON COLUMN sy_alarm_instance.receiver IS '消息接受者';
COMMENT ON COLUMN sy_alarm_instance.send_date_time IS '发送的时间';
COMMENT ON COLUMN sy_alarm_instance.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_alarm_instance.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_alarm_instance.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_api
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  path VARCHAR(200) NOT NULL,
  api_type VARCHAR(200) NOT NULL,
  remark VARCHAR(2000),
  req_header VARCHAR(2000),
  req_body VARCHAR(2000),
  api_sql VARCHAR(2000) NOT NULL,
  res_body VARCHAR(2000) NOT NULL,
  datasource_id VARCHAR(200) NOT NULL,
  status VARCHAR(200) NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  token_type VARCHAR(200),
  page_type VARCHAR(50),
  access_rule_id VARCHAR(200),
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_api.id IS '唯一API的id';
COMMENT ON COLUMN sy_api.name IS 'API名称';
COMMENT ON COLUMN sy_api.path IS 'API访问地址';
COMMENT ON COLUMN sy_api.api_type IS 'API类型';
COMMENT ON COLUMN sy_api.remark IS 'API备注';
COMMENT ON COLUMN sy_api.req_header IS '请求头';
COMMENT ON COLUMN sy_api.req_body IS '请求体';
COMMENT ON COLUMN sy_api.api_sql IS '执行的sql';
COMMENT ON COLUMN sy_api.res_body IS '响应体';
COMMENT ON COLUMN sy_api.datasource_id IS '数据源id';
COMMENT ON COLUMN sy_api.status IS 'API状态';
COMMENT ON COLUMN sy_api.create_by IS '创建人';
COMMENT ON COLUMN sy_api.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_api.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_api.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_api.version_number IS '版本号';
COMMENT ON COLUMN sy_api.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_api.tenant_id IS '租户id';
COMMENT ON COLUMN sy_api.token_type IS '认证方式';
COMMENT ON COLUMN sy_api.page_type IS '分页状态';
COMMENT ON COLUMN sy_api.access_rule_id IS '黑白名单配置id';

CREATE TABLE IF NOT EXISTS sy_api_access_rule
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  rule_type VARCHAR(200) NOT NULL,
  ip_address VARCHAR(2000) NOT NULL,
  remark VARCHAR(500),
  version_number INT NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL
);
COMMENT ON TABLE sy_api_access_rule IS 'API黑白名单配置表';
COMMENT ON COLUMN sy_api_access_rule.id IS '黑白名单id';
COMMENT ON COLUMN sy_api_access_rule.name IS '规则名称';
COMMENT ON COLUMN sy_api_access_rule.rule_type IS '规则类型: WHITELIST/BLACKLIST';
COMMENT ON COLUMN sy_api_access_rule.ip_address IS 'IP地址，多个用换行分隔，支持正则';
COMMENT ON COLUMN sy_api_access_rule.remark IS '备注';
COMMENT ON COLUMN sy_api_access_rule.version_number IS '版本号';
COMMENT ON COLUMN sy_api_access_rule.create_by IS '创建人';
COMMENT ON COLUMN sy_api_access_rule.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_api_access_rule.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_api_access_rule.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_api_access_rule.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_api_access_rule.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_cluster
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  remark VARCHAR(500),
  status VARCHAR(200) NOT NULL,
  check_date_time TIMESTAMP NOT NULL,
  all_node_num INT NOT NULL,
  active_node_num INT NOT NULL,
  all_memory_num DOUBLE PRECISION NOT NULL,
  used_memory_num DOUBLE PRECISION NOT NULL,
  all_storage_num DOUBLE PRECISION NOT NULL,
  used_storage_num DOUBLE PRECISION NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  cluster_type VARCHAR(100),
  default_cluster BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_cluster IS '集群表';
COMMENT ON COLUMN sy_cluster.id IS '集群唯一id';
COMMENT ON COLUMN sy_cluster.name IS '集群名称';
COMMENT ON COLUMN sy_cluster.remark IS '集群描述';
COMMENT ON COLUMN sy_cluster.status IS '集群状态';
COMMENT ON COLUMN sy_cluster.check_date_time IS '检测时间';
COMMENT ON COLUMN sy_cluster.all_node_num IS '所有节点';
COMMENT ON COLUMN sy_cluster.active_node_num IS '激活节点数';
COMMENT ON COLUMN sy_cluster.all_memory_num IS '所有内存';
COMMENT ON COLUMN sy_cluster.used_memory_num IS '已使用内存';
COMMENT ON COLUMN sy_cluster.all_storage_num IS '所有存储';
COMMENT ON COLUMN sy_cluster.used_storage_num IS '已使用存储';
COMMENT ON COLUMN sy_cluster.create_by IS '创建人';
COMMENT ON COLUMN sy_cluster.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_cluster.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_cluster.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_cluster.version_number IS '版本号';
COMMENT ON COLUMN sy_cluster.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_cluster.tenant_id IS '租户id';
COMMENT ON COLUMN sy_cluster.cluster_type IS '集群的类型';
COMMENT ON COLUMN sy_cluster.default_cluster IS '默认计算集群';

CREATE TABLE IF NOT EXISTS sy_cluster_node
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  remark VARCHAR(500),
  status VARCHAR(200) NOT NULL,
  check_date_time TIMESTAMP NOT NULL,
  all_memory DOUBLE PRECISION NOT NULL,
  used_memory DOUBLE PRECISION NOT NULL,
  all_storage DOUBLE PRECISION NOT NULL,
  used_storage DOUBLE PRECISION NOT NULL,
  cpu_percent DOUBLE PRECISION NOT NULL,
  cluster_id VARCHAR(200) NOT NULL,
  host VARCHAR(200) NOT NULL,
  port VARCHAR(200) NOT NULL,
  agent_log VARCHAR(2000),
  username VARCHAR(200) NOT NULL,
  passwd VARCHAR(5000) NOT NULL,
  agent_home_path VARCHAR(200) NOT NULL,
  agent_port VARCHAR(200) NOT NULL,
  hadoop_home_path VARCHAR(200),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  spark_home_path VARCHAR(200),
  install_spark_local BOOLEAN DEFAULT FALSE,
  install_flink_local BOOLEAN DEFAULT FALSE,
  flink_home_path VARCHAR(200),
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_cluster_node IS '集群节点表';
COMMENT ON COLUMN sy_cluster_node.id IS '集群节点唯一id';
COMMENT ON COLUMN sy_cluster_node.name IS '节点名称';
COMMENT ON COLUMN sy_cluster_node.remark IS '节点描述';
COMMENT ON COLUMN sy_cluster_node.status IS '节点状态';
COMMENT ON COLUMN sy_cluster_node.check_date_time IS '检测时间';
COMMENT ON COLUMN sy_cluster_node.all_memory IS '所有内存';
COMMENT ON COLUMN sy_cluster_node.used_memory IS '已使用内存';
COMMENT ON COLUMN sy_cluster_node.all_storage IS '所有存储';
COMMENT ON COLUMN sy_cluster_node.used_storage IS '已使用存储';
COMMENT ON COLUMN sy_cluster_node.cpu_percent IS 'cpu使用占比';
COMMENT ON COLUMN sy_cluster_node.cluster_id IS '集群id';
COMMENT ON COLUMN sy_cluster_node.host IS '节点服务器host';
COMMENT ON COLUMN sy_cluster_node.port IS '节点服务器端口号';
COMMENT ON COLUMN sy_cluster_node.agent_log IS '代理日志';
COMMENT ON COLUMN sy_cluster_node.username IS '节点服务器用户名';
COMMENT ON COLUMN sy_cluster_node.passwd IS '节点服务器密码';
COMMENT ON COLUMN sy_cluster_node.agent_home_path IS '至轻云代理安装目录';
COMMENT ON COLUMN sy_cluster_node.agent_port IS '至轻云代理服务端口号';
COMMENT ON COLUMN sy_cluster_node.hadoop_home_path IS 'hadoop家目录';
COMMENT ON COLUMN sy_cluster_node.create_by IS '创建人';
COMMENT ON COLUMN sy_cluster_node.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_cluster_node.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_cluster_node.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_cluster_node.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_cluster_node.tenant_id IS '租户id';
COMMENT ON COLUMN sy_cluster_node.spark_home_path IS 'standalone模式spark的安装目录';
COMMENT ON COLUMN sy_cluster_node.install_spark_local IS '是否安装spark-local组件';
COMMENT ON COLUMN sy_cluster_node.install_flink_local IS '是否安装spark-local组件';
COMMENT ON COLUMN sy_cluster_node.flink_home_path IS 'standalone模式flink的安装目录';

CREATE TABLE IF NOT EXISTS sy_column_format
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  column_type_code VARCHAR(200) NOT NULL,
  column_type VARCHAR(200),
  column_rule VARCHAR(500),
  status VARCHAR(200) NOT NULL,
  remark VARCHAR(200),
  is_null VARCHAR(10) NOT NULL,
  is_duplicate VARCHAR(10) NOT NULL,
  is_partition VARCHAR(10) NOT NULL,
  is_primary VARCHAR(10) NOT NULL,
  default_value VARCHAR(200),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_column_format IS '字段标准表';
COMMENT ON COLUMN sy_column_format.id IS '字段标准id';
COMMENT ON COLUMN sy_column_format.name IS '标准名称';
COMMENT ON COLUMN sy_column_format.column_type_code IS '字段类型编码，TEXT、LONGTEXT、CUSTOM';
COMMENT ON COLUMN sy_column_format.column_type IS '字段长度，字段配置';
COMMENT ON COLUMN sy_column_format.column_rule IS '字段规范';
COMMENT ON COLUMN sy_column_format.status IS '是否启用';
COMMENT ON COLUMN sy_column_format.remark IS '字段标准备注';
COMMENT ON COLUMN sy_column_format.is_null IS '可以为null';
COMMENT ON COLUMN sy_column_format.is_duplicate IS '可以重复';
COMMENT ON COLUMN sy_column_format.is_partition IS '是分区键';
COMMENT ON COLUMN sy_column_format.is_primary IS '是主键';
COMMENT ON COLUMN sy_column_format.default_value IS '默认值';
COMMENT ON COLUMN sy_column_format.create_by IS '创建人';
COMMENT ON COLUMN sy_column_format.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_column_format.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_column_format.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_column_format.version_number IS '版本号';
COMMENT ON COLUMN sy_column_format.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_column_format.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_container
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  remark VARCHAR(500),
  status VARCHAR(200) NOT NULL,
  datasource_id VARCHAR(200) NOT NULL,
  cluster_id VARCHAR(200) NOT NULL,
  resource_level VARCHAR(200) NOT NULL,
  spark_config VARCHAR(2000),
  port INT,
  submit_log TEXT,
  running_log TEXT,
  application_id VARCHAR(200),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_container.id IS '容器id';
COMMENT ON COLUMN sy_container.name IS '容器名称';
COMMENT ON COLUMN sy_container.remark IS '容器备注';
COMMENT ON COLUMN sy_container.status IS '容器状态';
COMMENT ON COLUMN sy_container.datasource_id IS '数据源id';
COMMENT ON COLUMN sy_container.cluster_id IS '集群id';
COMMENT ON COLUMN sy_container.resource_level IS '消耗资源等级';
COMMENT ON COLUMN sy_container.spark_config IS 'spark配置';
COMMENT ON COLUMN sy_container.port IS '容器端口号';
COMMENT ON COLUMN sy_container.submit_log IS '提交日志';
COMMENT ON COLUMN sy_container.running_log IS '运行日志';
COMMENT ON COLUMN sy_container.application_id IS '应用id';
COMMENT ON COLUMN sy_container.create_by IS '创建人';
COMMENT ON COLUMN sy_container.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_container.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_container.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_container.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_container.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_data_layer
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  table_rule VARCHAR(500),
  parent_layer_id VARCHAR(200),
  parent_id_list VARCHAR(500),
  parent_name_list VARCHAR(500),
  remark VARCHAR(500),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_data_layer IS '数据分层表';
COMMENT ON COLUMN sy_data_layer.id IS '数据分层id';
COMMENT ON COLUMN sy_data_layer.name IS '分层名称';
COMMENT ON COLUMN sy_data_layer.table_rule IS '表名分层规范';
COMMENT ON COLUMN sy_data_layer.parent_layer_id IS '父层级分层id';
COMMENT ON COLUMN sy_data_layer.parent_id_list IS '父级链条';
COMMENT ON COLUMN sy_data_layer.parent_name_list IS '父级链条名称';
COMMENT ON COLUMN sy_data_layer.remark IS '分层描述';
COMMENT ON COLUMN sy_data_layer.create_by IS '创建人';
COMMENT ON COLUMN sy_data_layer.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_data_layer.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_data_layer.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_data_layer.version_number IS '版本号';
COMMENT ON COLUMN sy_data_layer.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_data_layer.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_data_model
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  layer_id VARCHAR(200) NOT NULL,
  db_type VARCHAR(200) NOT NULL,
  datasource_id VARCHAR(200) NOT NULL,
  table_name VARCHAR(200) NOT NULL,
  model_type VARCHAR(200) NOT NULL,
  table_config TEXT,
  status VARCHAR(200) NOT NULL,
  build_log TEXT,
  remark VARCHAR(200),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_data_model IS '数据模型表';
COMMENT ON COLUMN sy_data_model.id IS '数据模型id';
COMMENT ON COLUMN sy_data_model.name IS '模型名称';
COMMENT ON COLUMN sy_data_model.layer_id IS '数据分层id';
COMMENT ON COLUMN sy_data_model.db_type IS '数据源类型';
COMMENT ON COLUMN sy_data_model.datasource_id IS '数据源id';
COMMENT ON COLUMN sy_data_model.table_name IS '表名';
COMMENT ON COLUMN sy_data_model.model_type IS '模型类型，LINK_TABLE 关联模型,MODEL_TABLE 原始模型';
COMMENT ON COLUMN sy_data_model.table_config IS '表高级配置';
COMMENT ON COLUMN sy_data_model.status IS '数据模型状态，INIT 新建、SUCCESS 成功、ERROR 失败';
COMMENT ON COLUMN sy_data_model.build_log IS '创建日志';
COMMENT ON COLUMN sy_data_model.remark IS '数据模型备注';
COMMENT ON COLUMN sy_data_model.create_by IS '创建人';
COMMENT ON COLUMN sy_data_model.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_data_model.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_data_model.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_data_model.version_number IS '版本号';
COMMENT ON COLUMN sy_data_model.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_data_model.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_data_model_column
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  column_name VARCHAR(200) NOT NULL,
  model_id VARCHAR(200) NOT NULL,
  column_format_id VARCHAR(200),
  column_index INT NOT NULL,
  link_column_type VARCHAR(200),
  remark VARCHAR(200),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_data_model_column IS '模型字段表';
COMMENT ON COLUMN sy_data_model_column.id IS '模型字段id';
COMMENT ON COLUMN sy_data_model_column.name IS '字段名称';
COMMENT ON COLUMN sy_data_model_column.column_name IS '表字段名';
COMMENT ON COLUMN sy_data_model_column.model_id IS '数据模型id';
COMMENT ON COLUMN sy_data_model_column.column_format_id IS '字段标准id';
COMMENT ON COLUMN sy_data_model_column.column_index IS '字段顺序';
COMMENT ON COLUMN sy_data_model_column.link_column_type IS '关联模型字段类型';
COMMENT ON COLUMN sy_data_model_column.remark IS '模型字段备注';
COMMENT ON COLUMN sy_data_model_column.create_by IS '创建人';
COMMENT ON COLUMN sy_data_model_column.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_data_model_column.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_data_model_column.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_data_model_column.version_number IS '版本号';
COMMENT ON COLUMN sy_data_model_column.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_data_model_column.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_database_driver
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  db_type VARCHAR(200) NOT NULL,
  file_name VARCHAR(500) NOT NULL,
  remark VARCHAR(500),
  driver_type VARCHAR(200) NOT NULL,
  is_default_driver BOOLEAN NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS sy_datasource
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  jdbc_url VARCHAR(500) NOT NULL,
  remark VARCHAR(500),
  status VARCHAR(200) NOT NULL,
  check_date_time TIMESTAMP NOT NULL,
  username VARCHAR(200),
  passwd VARCHAR(200),
  connect_log TEXT,
  db_type VARCHAR(200) NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  metastore_uris VARCHAR(500),
  driver_id VARCHAR(100) NOT NULL,
  kafka_config TEXT,
  connect_config TEXT,
  fe_nodes VARCHAR(500),
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_datasource IS '数据源表';
COMMENT ON COLUMN sy_datasource.id IS '数据源唯一id';
COMMENT ON COLUMN sy_datasource.name IS '数据源名称';
COMMENT ON COLUMN sy_datasource.jdbc_url IS '数据源jdbcUrl';
COMMENT ON COLUMN sy_datasource.remark IS '描述';
COMMENT ON COLUMN sy_datasource.status IS '状态';
COMMENT ON COLUMN sy_datasource.check_date_time IS '检测时间';
COMMENT ON COLUMN sy_datasource.username IS '数据源用户名';
COMMENT ON COLUMN sy_datasource.passwd IS '数据源密码';
COMMENT ON COLUMN sy_datasource.connect_log IS '测试连接日志';
COMMENT ON COLUMN sy_datasource.db_type IS '数据源类型';
COMMENT ON COLUMN sy_datasource.create_by IS '创建人';
COMMENT ON COLUMN sy_datasource.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_datasource.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_datasource.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_datasource.version_number IS '版本号';
COMMENT ON COLUMN sy_datasource.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_datasource.tenant_id IS '租户id';
COMMENT ON COLUMN sy_datasource.metastore_uris IS 'hive数据源 hive.metastore.uris 配置';
COMMENT ON COLUMN sy_datasource.driver_id IS '数据库驱动id';
COMMENT ON COLUMN sy_datasource.kafka_config IS 'kafka数据源配置';
COMMENT ON COLUMN sy_datasource.connect_config IS '高级配置';

CREATE TABLE IF NOT EXISTS sy_file
(
  id VARCHAR(200) NOT NULL,
  file_name VARCHAR(200),
  file_size VARCHAR(200),
  file_type VARCHAR(200),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  remark VARCHAR(500),
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_file.remark IS '备注';

CREATE TABLE IF NOT EXISTS sy_form
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  datasource_id VARCHAR(200) NOT NULL,
  main_table VARCHAR(200),
  status VARCHAR(200),
  insert_sql VARCHAR(2000),
  delete_sql VARCHAR(2000),
  update_sql VARCHAR(2000),
  select_sql VARCHAR(2000),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  remark VARCHAR(500),
  form_web_config TEXT,
  form_version VARCHAR(50),
  create_mode VARCHAR(50),
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_form.id IS '自定义表单唯一id';
COMMENT ON COLUMN sy_form.name IS '表单名称';
COMMENT ON COLUMN sy_form.datasource_id IS '数据源id';
COMMENT ON COLUMN sy_form.main_table IS '主要对象表id';
COMMENT ON COLUMN sy_form.status IS '自定义表单状态';
COMMENT ON COLUMN sy_form.insert_sql IS '增sql语句';
COMMENT ON COLUMN sy_form.delete_sql IS '删sql语句';
COMMENT ON COLUMN sy_form.update_sql IS '改sql语句';
COMMENT ON COLUMN sy_form.select_sql IS '查sql语句';
COMMENT ON COLUMN sy_form.create_by IS '创建人';
COMMENT ON COLUMN sy_form.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_form.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_form.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_form.version_number IS '版本号';
COMMENT ON COLUMN sy_form.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_form.tenant_id IS '租户id';
COMMENT ON COLUMN sy_form.remark IS '表单备注';
COMMENT ON COLUMN sy_form.form_web_config IS '前端所有的配置';
COMMENT ON COLUMN sy_form.form_version IS '表单版本号';
COMMENT ON COLUMN sy_form.create_mode IS '该表单主表的创建模式';

CREATE TABLE IF NOT EXISTS sy_form_component
(
  id VARCHAR(200) NOT NULL,
  form_id VARCHAR(200),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  component_config TEXT,
  uuid VARCHAR(50),
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_form_component.id IS '表单字段组件唯一id';
COMMENT ON COLUMN sy_form_component.form_id IS '自定义表单id';
COMMENT ON COLUMN sy_form_component.create_by IS '创建人';
COMMENT ON COLUMN sy_form_component.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_form_component.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_form_component.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_form_component.version_number IS '版本号';
COMMENT ON COLUMN sy_form_component.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_form_component.tenant_id IS '租户id';
COMMENT ON COLUMN sy_form_component.component_config IS '组件的配置';
COMMENT ON COLUMN sy_form_component.uuid IS '前端的uuid';

CREATE TABLE IF NOT EXISTS sy_form_link
(
  id VARCHAR(200) NOT NULL,
  form_id VARCHAR(200) NOT NULL,
  form_version VARCHAR(200) NOT NULL,
  form_token VARCHAR(500) NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  invalid_date_time TIMESTAMP NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_form_link.id IS '数据大屏分享链接id';
COMMENT ON COLUMN sy_form_link.form_id IS '大屏id';
COMMENT ON COLUMN sy_form_link.form_version IS '大屏版本';
COMMENT ON COLUMN sy_form_link.form_token IS '分享大屏的匿名token';
COMMENT ON COLUMN sy_form_link.create_by IS '创建人';
COMMENT ON COLUMN sy_form_link.invalid_date_time IS '到期时间';
COMMENT ON COLUMN sy_form_link.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_form_link.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_form_link.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_form_link.version_number IS '版本号';
COMMENT ON COLUMN sy_form_link.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_form_link.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_func
(
  id VARCHAR(200) NOT NULL,
  type VARCHAR(20) NOT NULL,
  file_id VARCHAR(200) NOT NULL,
  func_name VARCHAR(200) NOT NULL,
  class_name VARCHAR(200) NOT NULL,
  result_type VARCHAR(200) NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  remark VARCHAR(500),
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_func IS '用户定义的函数（UDF）表，用于存储UDF的元数据信息';
COMMENT ON COLUMN sy_func.id IS 'udf的唯一标识符';
COMMENT ON COLUMN sy_func.type IS 'UDF或者UDAF';
COMMENT ON COLUMN sy_func.file_id IS 'jar文件的唯一标识符';
COMMENT ON COLUMN sy_func.func_name IS 'udf的调用方法名';
COMMENT ON COLUMN sy_func.class_name IS '包含udf方法的类名';
COMMENT ON COLUMN sy_func.result_type IS 'udf方法的返回值类型';
COMMENT ON COLUMN sy_func.create_by IS '创建此udf记录的用户';
COMMENT ON COLUMN sy_func.create_date_time IS 'udf记录的创建时间';
COMMENT ON COLUMN sy_func.last_modified_by IS '最后修改此udf记录的用户';
COMMENT ON COLUMN sy_func.last_modified_date_time IS 'udf记录的最后修改时间';
COMMENT ON COLUMN sy_func.version_number IS 'udf记录的版本号';
COMMENT ON COLUMN sy_func.deleted IS '逻辑删除标记，0表示未删除';
COMMENT ON COLUMN sy_func.tenant_id IS '租户的唯一标识符';
COMMENT ON COLUMN sy_func.remark IS '备注';

CREATE TABLE IF NOT EXISTS sy_lib_package
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  file_id_list VARCHAR(5000),
  remark VARCHAR(500),
  version_number INT NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_lib_package.id IS '依赖包id';
COMMENT ON COLUMN sy_lib_package.name IS '依赖包名称';
COMMENT ON COLUMN sy_lib_package.file_id_list IS '依赖包中依赖';
COMMENT ON COLUMN sy_lib_package.remark IS '依赖包备注';
COMMENT ON COLUMN sy_lib_package.version_number IS '版本号';
COMMENT ON COLUMN sy_lib_package.create_by IS '创建人';
COMMENT ON COLUMN sy_lib_package.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_lib_package.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_lib_package.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_lib_package.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_lib_package.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_license
(
  id VARCHAR(200) NOT NULL,
  code VARCHAR(200) NOT NULL,
  company_name VARCHAR(200) NOT NULL,
  logo VARCHAR(2000) NOT NULL,
  remark VARCHAR(2000),
  issuer VARCHAR(200) NOT NULL,
  start_date_time TIMESTAMP NOT NULL,
  end_date_time TIMESTAMP NOT NULL,
  max_tenant_num INT,
  max_member_num INT,
  max_workflow_num INT,
  status VARCHAR(200) NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_license.id IS '许可证唯一id';
COMMENT ON COLUMN sy_license.code IS '许可证编号';
COMMENT ON COLUMN sy_license.company_name IS '公司名称';
COMMENT ON COLUMN sy_license.logo IS '公司logo';
COMMENT ON COLUMN sy_license.remark IS '许可证备注';
COMMENT ON COLUMN sy_license.issuer IS '许可证签发人';
COMMENT ON COLUMN sy_license.start_date_time IS '许可证起始时间';
COMMENT ON COLUMN sy_license.end_date_time IS '许可证到期时间';
COMMENT ON COLUMN sy_license.max_tenant_num IS '最大租户数';
COMMENT ON COLUMN sy_license.max_member_num IS '最大成员数';
COMMENT ON COLUMN sy_license.max_workflow_num IS '最大作业流数';
COMMENT ON COLUMN sy_license.status IS '证书状态';
COMMENT ON COLUMN sy_license.create_by IS '创建人';
COMMENT ON COLUMN sy_license.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_license.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_license.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_license.version_number IS '版本号';
COMMENT ON COLUMN sy_license.deleted IS '逻辑删除';

CREATE TABLE IF NOT EXISTS sy_locker
(
  id INT NOT NULL,
  name VARCHAR(200) NOT NULL,
  box VARCHAR(2000),
  lock_owner VARCHAR(200),
  expire_time TIMESTAMP,
  create_date_time TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uk_sy_locker_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS sy_message
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  status VARCHAR(100) NOT NULL,
  remark VARCHAR(500),
  msg_type VARCHAR(200) NOT NULL,
  msg_config TEXT NOT NULL,
  response TEXT,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_message.id IS '消息消息体id';
COMMENT ON COLUMN sy_message.name IS '消息体名称';
COMMENT ON COLUMN sy_message.status IS '消息体状态';
COMMENT ON COLUMN sy_message.remark IS '消息体备注';
COMMENT ON COLUMN sy_message.msg_type IS '消息体类型，邮箱/阿里短信/飞书';
COMMENT ON COLUMN sy_message.msg_config IS '消息体配置信息';
COMMENT ON COLUMN sy_message.response IS '检测响应';
COMMENT ON COLUMN sy_message.create_by IS '创建人';
COMMENT ON COLUMN sy_message.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_message.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_message.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_message.version_number IS '版本号';
COMMENT ON COLUMN sy_message.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_message.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_meta_column
(
  datasource_id VARCHAR(200) NOT NULL,
  table_name VARCHAR(200) NOT NULL,
  column_name VARCHAR(200) NOT NULL,
  column_type VARCHAR(200) NOT NULL,
  column_comment VARCHAR(500),
  is_partition_column BOOLEAN NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (datasource_id, table_name, column_name)
);

CREATE TABLE IF NOT EXISTS sy_meta_column_info
(
  datasource_id VARCHAR(200) NOT NULL,
  table_name VARCHAR(200) NOT NULL,
  column_name VARCHAR(200) NOT NULL,
  custom_comment VARCHAR(500),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (datasource_id, table_name, column_name)
);

CREATE TABLE IF NOT EXISTS sy_meta_column_lineage
(
  id VARCHAR(200) NOT NULL,
  from_db_id VARCHAR(200) NOT NULL,
  from_table_name VARCHAR(200) NOT NULL,
  from_column_name VARCHAR(200) NOT NULL,
  work_id VARCHAR(200) NOT NULL,
  work_version_id VARCHAR(200) NOT NULL,
  to_db_id VARCHAR(200) NOT NULL,
  to_table_name VARCHAR(200) NOT NULL,
  to_column_name VARCHAR(200) NOT NULL,
  remark VARCHAR(500),
  version_number INT NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_meta_column_lineage IS '字段血缘表';
COMMENT ON COLUMN sy_meta_column_lineage.id IS '字段血缘id';
COMMENT ON COLUMN sy_meta_column_lineage.from_db_id IS '来源数据源';
COMMENT ON COLUMN sy_meta_column_lineage.from_table_name IS '来源表名';
COMMENT ON COLUMN sy_meta_column_lineage.from_column_name IS '来源字段名';
COMMENT ON COLUMN sy_meta_column_lineage.work_id IS '作业';
COMMENT ON COLUMN sy_meta_column_lineage.work_version_id IS '作业版本';
COMMENT ON COLUMN sy_meta_column_lineage.to_db_id IS '去向数据源';
COMMENT ON COLUMN sy_meta_column_lineage.to_table_name IS '去向表名';
COMMENT ON COLUMN sy_meta_column_lineage.to_column_name IS '去向字段名';
COMMENT ON COLUMN sy_meta_column_lineage.remark IS '血缘备注';
COMMENT ON COLUMN sy_meta_column_lineage.version_number IS '版本号';
COMMENT ON COLUMN sy_meta_column_lineage.create_by IS '创建人';
COMMENT ON COLUMN sy_meta_column_lineage.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_meta_column_lineage.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_meta_column_lineage.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_meta_column_lineage.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_meta_column_lineage.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_meta_database
(
  datasource_id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  db_name VARCHAR(200),
  db_type VARCHAR(200) NOT NULL,
  db_comment VARCHAR(500),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  status VARCHAR(200),
  PRIMARY KEY (datasource_id)
);

CREATE TABLE IF NOT EXISTS sy_meta_instance
(
  id VARCHAR(200) NOT NULL,
  meta_work_id VARCHAR(200) NOT NULL,
  trigger_type VARCHAR(200) NOT NULL,
  status VARCHAR(500) NOT NULL,
  start_date_time TIMESTAMP NOT NULL,
  end_date_time TIMESTAMP,
  collect_log TEXT,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  runner_owner VARCHAR(200),
  heartbeat_date_time TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS sy_meta_table
(
  datasource_id VARCHAR(200) NOT NULL,
  table_name VARCHAR(200) NOT NULL,
  table_comment VARCHAR(500),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (datasource_id, table_name)
);

CREATE TABLE IF NOT EXISTS sy_meta_table_info
(
  datasource_id VARCHAR(200) NOT NULL,
  table_name VARCHAR(200) NOT NULL,
  column_count BIGINT,
  total_rows BIGINT,
  total_size BIGINT,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  custom_comment VARCHAR(500),
  PRIMARY KEY (datasource_id, table_name)
);

CREATE TABLE IF NOT EXISTS sy_meta_work
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  db_type VARCHAR(200) NOT NULL,
  datasource_id VARCHAR(100) NOT NULL,
  collect_type VARCHAR(100) NOT NULL,
  table_pattern VARCHAR(100) NOT NULL,
  cron_config VARCHAR(500),
  status VARCHAR(500) NOT NULL,
  remark VARCHAR(500),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS sy_monitor
(
  id VARCHAR(200) NOT NULL,
  cluster_id VARCHAR(200) NOT NULL,
  cluster_node_id VARCHAR(200) NOT NULL,
  status VARCHAR(100) NOT NULL,
  log TEXT NOT NULL,
  used_storage_size DOUBLE PRECISION,
  used_memory_size DOUBLE PRECISION,
  network_io_read_speed DOUBLE PRECISION,
  network_io_write_speed DOUBLE PRECISION,
  disk_io_read_speed DOUBLE PRECISION,
  disk_io_write_speed DOUBLE PRECISION,
  cpu_percent DOUBLE PRECISION,
  create_date_time TIMESTAMP NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_monitor.id IS '分享表单链接id';
COMMENT ON COLUMN sy_monitor.cluster_id IS '集群id';
COMMENT ON COLUMN sy_monitor.cluster_node_id IS '集群节点id';
COMMENT ON COLUMN sy_monitor.status IS '监控状态';
COMMENT ON COLUMN sy_monitor.log IS '日志';
COMMENT ON COLUMN sy_monitor.used_storage_size IS '已使用存储';
COMMENT ON COLUMN sy_monitor.used_memory_size IS '已使用内存';
COMMENT ON COLUMN sy_monitor.network_io_read_speed IS '网络读速度';
COMMENT ON COLUMN sy_monitor.network_io_write_speed IS '网络写速度';
COMMENT ON COLUMN sy_monitor.disk_io_read_speed IS '磁盘读速度';
COMMENT ON COLUMN sy_monitor.disk_io_write_speed IS '磁盘写速度';
COMMENT ON COLUMN sy_monitor.cpu_percent IS 'cpu占用';
COMMENT ON COLUMN sy_monitor.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_monitor.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_monitor.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_real
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  status VARCHAR(200) NOT NULL,
  cluster_id VARCHAR(500) NOT NULL,
  spark_config TEXT NOT NULL,
  sync_config TEXT,
  lib_config VARCHAR(500),
  func_config VARCHAR(500),
  submit_log TEXT,
  running_log TEXT,
  application_id VARCHAR(500),
  remark VARCHAR(500),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_real IS '实时作业信息表';
COMMENT ON COLUMN sy_real.id IS '分享表单链接id';
COMMENT ON COLUMN sy_real.name IS '实时作业名称';
COMMENT ON COLUMN sy_real.status IS '运行状态';
COMMENT ON COLUMN sy_real.cluster_id IS '集群id';
COMMENT ON COLUMN sy_real.spark_config IS '集群配置';
COMMENT ON COLUMN sy_real.sync_config IS '数据同步配置';
COMMENT ON COLUMN sy_real.lib_config IS '依赖配置';
COMMENT ON COLUMN sy_real.func_config IS '函数配置';
COMMENT ON COLUMN sy_real.submit_log IS '提交日志';
COMMENT ON COLUMN sy_real.running_log IS '运行日志';
COMMENT ON COLUMN sy_real.application_id IS '应用id';
COMMENT ON COLUMN sy_real.remark IS '备注';
COMMENT ON COLUMN sy_real.create_by IS '创建人';
COMMENT ON COLUMN sy_real.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_real.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_real.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_real.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_real.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_secret_key
(
  id VARCHAR(200) NOT NULL,
  key_name VARCHAR(200) NOT NULL,
  secret_value VARCHAR(200) NOT NULL,
  remark VARCHAR(500),
  version_number INT NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_secret_key.id IS '全局变量id';
COMMENT ON COLUMN sy_secret_key.key_name IS '全局变量key';
COMMENT ON COLUMN sy_secret_key.secret_value IS '全局变量value';
COMMENT ON COLUMN sy_secret_key.version_number IS '版本号';
COMMENT ON COLUMN sy_secret_key.create_by IS '创建人';
COMMENT ON COLUMN sy_secret_key.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_secret_key.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_secret_key.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_secret_key.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_secret_key.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_sso_auth
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  status VARCHAR(200) NOT NULL,
  sso_type VARCHAR(200) NOT NULL,
  client_id VARCHAR(200) NOT NULL,
  client_secret VARCHAR(500) NOT NULL,
  scope VARCHAR(500),
  auth_url VARCHAR(500) NOT NULL,
  access_token_url VARCHAR(500) NOT NULL,
  redirect_url VARCHAR(500) NOT NULL,
  user_url VARCHAR(500) NOT NULL,
  auth_json_path VARCHAR(500) NOT NULL,
  remark VARCHAR(500),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_sso_auth IS '单点登录认证表';
COMMENT ON COLUMN sy_sso_auth.id IS '单点id';
COMMENT ON COLUMN sy_sso_auth.name IS '单点名称';
COMMENT ON COLUMN sy_sso_auth.status IS '单点状态';
COMMENT ON COLUMN sy_sso_auth.sso_type IS '单点类型';
COMMENT ON COLUMN sy_sso_auth.client_id IS 'clientId';
COMMENT ON COLUMN sy_sso_auth.client_secret IS 'clientSecret';
COMMENT ON COLUMN sy_sso_auth.scope IS 'scope';
COMMENT ON COLUMN sy_sso_auth.auth_url IS '授权认证地址';
COMMENT ON COLUMN sy_sso_auth.access_token_url IS 'token获取地址';
COMMENT ON COLUMN sy_sso_auth.redirect_url IS '跳转地址';
COMMENT ON COLUMN sy_sso_auth.user_url IS '获取用户信息地址';
COMMENT ON COLUMN sy_sso_auth.auth_json_path IS '解析用户信息jsonPath';
COMMENT ON COLUMN sy_sso_auth.remark IS '单点备注';
COMMENT ON COLUMN sy_sso_auth.create_by IS '创建人';
COMMENT ON COLUMN sy_sso_auth.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_sso_auth.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_sso_auth.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_sso_auth.version_number IS '版本号';
COMMENT ON COLUMN sy_sso_auth.deleted IS '逻辑删除';

CREATE TABLE IF NOT EXISTS sy_tenant
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  used_member_num INT NOT NULL,
  max_member_num INT NOT NULL,
  used_workflow_num INT NOT NULL,
  max_workflow_num INT NOT NULL,
  status VARCHAR(200) NOT NULL,
  introduce VARCHAR(500),
  remark VARCHAR(500),
  check_date_time TIMESTAMP NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  valid_start_date_time TIMESTAMP,
  valid_end_date_time TIMESTAMP,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_tenant.id IS '租户唯一id';
COMMENT ON COLUMN sy_tenant.name IS '租户名称';
COMMENT ON COLUMN sy_tenant.used_member_num IS '已使用成员数';
COMMENT ON COLUMN sy_tenant.max_member_num IS '最大成员数';
COMMENT ON COLUMN sy_tenant.used_workflow_num IS '已使用作业流数';
COMMENT ON COLUMN sy_tenant.max_workflow_num IS '最大作业流数';
COMMENT ON COLUMN sy_tenant.status IS '租户状态';
COMMENT ON COLUMN sy_tenant.introduce IS '租户简介';
COMMENT ON COLUMN sy_tenant.remark IS '租户描述';
COMMENT ON COLUMN sy_tenant.check_date_time IS '检测时间';
COMMENT ON COLUMN sy_tenant.create_by IS '创建人';
COMMENT ON COLUMN sy_tenant.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_tenant.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_tenant.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_tenant.version_number IS '版本号';
COMMENT ON COLUMN sy_tenant.deleted IS '逻辑删除';

CREATE TABLE IF NOT EXISTS sy_tenant_users
(
  id VARCHAR(200) NOT NULL,
  user_id VARCHAR(200) NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  role_code VARCHAR(200) NOT NULL,
  status VARCHAR(200) NOT NULL,
  remark VARCHAR(200),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_tenant_users IS '租户用户关系表';
COMMENT ON COLUMN sy_tenant_users.id IS '关系唯一id';
COMMENT ON COLUMN sy_tenant_users.user_id IS '用户id';
COMMENT ON COLUMN sy_tenant_users.tenant_id IS '租户id';
COMMENT ON COLUMN sy_tenant_users.role_code IS '角色编码';
COMMENT ON COLUMN sy_tenant_users.status IS '用户状态';
COMMENT ON COLUMN sy_tenant_users.remark IS '备注';
COMMENT ON COLUMN sy_tenant_users.create_by IS '创建人';
COMMENT ON COLUMN sy_tenant_users.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_tenant_users.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_tenant_users.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_tenant_users.version_number IS '版本号';
COMMENT ON COLUMN sy_tenant_users.deleted IS '逻辑删除';

CREATE TABLE IF NOT EXISTS sy_user
(
  id VARCHAR(200) NOT NULL,
  username VARCHAR(200) NOT NULL,
  account VARCHAR(200) NOT NULL,
  passwd VARCHAR(200),
  phone VARCHAR(200),
  email VARCHAR(200),
  introduce VARCHAR(500),
  remark VARCHAR(500),
  role_code VARCHAR(200) NOT NULL,
  status VARCHAR(200) NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  current_tenant_id VARCHAR(200),
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  valid_start_date_time TIMESTAMP,
  valid_end_date_time TIMESTAMP,
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_user IS '用户表';
COMMENT ON COLUMN sy_user.id IS '用户唯一id';
COMMENT ON COLUMN sy_user.username IS '用户名称';
COMMENT ON COLUMN sy_user.account IS '用户账号';
COMMENT ON COLUMN sy_user.passwd IS '账号密码';
COMMENT ON COLUMN sy_user.phone IS '手机号';
COMMENT ON COLUMN sy_user.email IS '邮箱';
COMMENT ON COLUMN sy_user.introduce IS '简介';
COMMENT ON COLUMN sy_user.remark IS '描述';
COMMENT ON COLUMN sy_user.role_code IS '角色编码';
COMMENT ON COLUMN sy_user.status IS '用户状态';
COMMENT ON COLUMN sy_user.create_by IS '创建人';
COMMENT ON COLUMN sy_user.current_tenant_id IS '当前用户使用的租户id';
COMMENT ON COLUMN sy_user.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_user.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_user.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_user.version_number IS '版本号';
COMMENT ON COLUMN sy_user.deleted IS '逻辑删除';

CREATE TABLE IF NOT EXISTS sy_user_action
(
  id VARCHAR(200) NOT NULL,
  user_id VARCHAR(200),
  tenant_id VARCHAR(200),
  req_path VARCHAR(200),
  req_method VARCHAR(200),
  req_header VARCHAR(2000),
  req_body TEXT,
  res_body TEXT,
  start_timestamp BIGINT,
  end_timestamp BIGINT,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_user_action.id IS '用户行为唯一id';
COMMENT ON COLUMN sy_user_action.user_id IS '用户id';
COMMENT ON COLUMN sy_user_action.tenant_id IS '租户id';
COMMENT ON COLUMN sy_user_action.req_path IS '请求路径';
COMMENT ON COLUMN sy_user_action.req_method IS '请求方式';
COMMENT ON COLUMN sy_user_action.req_header IS '请求头';
COMMENT ON COLUMN sy_user_action.req_body IS '请求体';
COMMENT ON COLUMN sy_user_action.res_body IS '响应体';
COMMENT ON COLUMN sy_user_action.start_timestamp IS '开始时间戳';
COMMENT ON COLUMN sy_user_action.end_timestamp IS '结束时间戳';
COMMENT ON COLUMN sy_user_action.create_by IS '创建人';
COMMENT ON COLUMN sy_user_action.create_date_time IS '创建时间';

CREATE TABLE IF NOT EXISTS sy_view
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  remark VARCHAR(500),
  status VARCHAR(200) NOT NULL,
  background_file_id VARCHAR(200),
  card_list VARCHAR(2000),
  web_config TEXT,
  version_number INT NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_view.id IS '数据大屏id';
COMMENT ON COLUMN sy_view.name IS '大屏名称';
COMMENT ON COLUMN sy_view.remark IS '大屏备注';
COMMENT ON COLUMN sy_view.status IS '大屏状态';
COMMENT ON COLUMN sy_view.background_file_id IS '背景图文件id';
COMMENT ON COLUMN sy_view.card_list IS '大屏中包含的卡片';
COMMENT ON COLUMN sy_view.web_config IS '大屏显示配置';
COMMENT ON COLUMN sy_view.version_number IS '版本号';
COMMENT ON COLUMN sy_view.create_by IS '创建人';
COMMENT ON COLUMN sy_view.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_view.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_view.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_view.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_view.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_view_card
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  remark VARCHAR(500),
  status VARCHAR(200) NOT NULL,
  type VARCHAR(200) NOT NULL,
  datasource_id VARCHAR(200) NOT NULL,
  example_data VARCHAR(2000) NOT NULL,
  web_config TEXT,
  data_sql VARCHAR(2000),
  version_number INT NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_view_card.id IS '大屏组件id';
COMMENT ON COLUMN sy_view_card.name IS '大屏组件名称';
COMMENT ON COLUMN sy_view_card.remark IS '大屏组件备注';
COMMENT ON COLUMN sy_view_card.status IS '大屏组件状态';
COMMENT ON COLUMN sy_view_card.type IS '大屏组件类型';
COMMENT ON COLUMN sy_view_card.datasource_id IS '数据源id';
COMMENT ON COLUMN sy_view_card.example_data IS '示例数据sql';
COMMENT ON COLUMN sy_view_card.web_config IS '前端显示配置';
COMMENT ON COLUMN sy_view_card.data_sql IS '数据sql';
COMMENT ON COLUMN sy_view_card.version_number IS '版本号';
COMMENT ON COLUMN sy_view_card.create_by IS '创建人';
COMMENT ON COLUMN sy_view_card.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_view_card.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_view_card.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_view_card.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_view_card.tenant_id IS '租户id';

CREATE TABLE IF NOT EXISTS sy_view_link
(
  id VARCHAR(200) NOT NULL,
  form_id VARCHAR(200) NOT NULL,
  form_version VARCHAR(200) NOT NULL,
  form_token VARCHAR(500) NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  invalid_date_time TIMESTAMP NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS sy_work
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  remark VARCHAR(500),
  status VARCHAR(200) NOT NULL,
  work_type VARCHAR(200) NOT NULL,
  config_id VARCHAR(200) NOT NULL,
  workflow_id VARCHAR(200) NOT NULL,
  version_id VARCHAR(200),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  top_index INT,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_work.id IS '作业唯一id';
COMMENT ON COLUMN sy_work.name IS '作业名称';
COMMENT ON COLUMN sy_work.remark IS '作业描述';
COMMENT ON COLUMN sy_work.status IS '作业状态';
COMMENT ON COLUMN sy_work.work_type IS '作业类型';
COMMENT ON COLUMN sy_work.config_id IS '作业配置id';
COMMENT ON COLUMN sy_work.workflow_id IS '作业流id';
COMMENT ON COLUMN sy_work.version_id IS '作业当前最新版本号';
COMMENT ON COLUMN sy_work.create_by IS '创建人';
COMMENT ON COLUMN sy_work.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_work.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_work.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_work.version_number IS '版本号';
COMMENT ON COLUMN sy_work.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_work.tenant_id IS '租户id';
COMMENT ON COLUMN sy_work.top_index IS '作业置顶标志';

CREATE TABLE IF NOT EXISTS sy_work_config
(
  id VARCHAR(200) NOT NULL,
  datasource_id VARCHAR(200),
  script TEXT,
  cron_config VARCHAR(2000),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  sync_work_config TEXT,
  cluster_config TEXT,
  sync_rule TEXT,
  udf_status BOOLEAN DEFAULT FALSE,
  jar_conf TEXT,
  jar_job_config TEXT,
  lib_config TEXT,
  func_config TEXT,
  container_id VARCHAR(200),
  api_work_config TEXT,
  alarm_list TEXT,
  excel_sync_config TEXT,
  db_migrate_config TEXT,
  query_config TEXT,
  lib_package_config TEXT,
  spark_etl_config TEXT,
  sync_flink_config TEXT,
  api_sync_config TEXT,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_work_config.id IS '作业配置唯一id';
COMMENT ON COLUMN sy_work_config.datasource_id IS '数据源id';
COMMENT ON COLUMN sy_work_config.script IS '统一脚本内容，包括sql、bash、python脚本';
COMMENT ON COLUMN sy_work_config.cron_config IS '定时表达式';
COMMENT ON COLUMN sy_work_config.create_by IS '创建人';
COMMENT ON COLUMN sy_work_config.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_work_config.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_work_config.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_work_config.version_number IS '版本号';
COMMENT ON COLUMN sy_work_config.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_work_config.tenant_id IS '租户id';
COMMENT ON COLUMN sy_work_config.cluster_config IS '计算集群配置';
COMMENT ON COLUMN sy_work_config.sync_rule IS '数据同步规则';
COMMENT ON COLUMN sy_work_config.jar_conf IS 'JAR 配置';
COMMENT ON COLUMN sy_work_config.jar_job_config IS '自定义jar作业配置';
COMMENT ON COLUMN sy_work_config.lib_config IS '作业依赖文件';
COMMENT ON COLUMN sy_work_config.func_config IS '自定义函数配置';
COMMENT ON COLUMN sy_work_config.api_work_config IS '接口调用作业的配置';
COMMENT ON COLUMN sy_work_config.alarm_list IS '绑定的基线';
COMMENT ON COLUMN sy_work_config.lib_package_config IS '任务包配置';

CREATE TABLE IF NOT EXISTS sy_work_event
(
  id VARCHAR(200) NOT NULL,
  event_process INT NOT NULL,
  event_context TEXT,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
);
COMMENT ON TABLE sy_work_event IS '作业事件表';
COMMENT ON COLUMN sy_work_event.id IS '事件id';
COMMENT ON COLUMN sy_work_event.event_process IS '事件进程';
COMMENT ON COLUMN sy_work_event.event_context IS '事件上下文';
COMMENT ON COLUMN sy_work_event.create_by IS '创建人';
COMMENT ON COLUMN sy_work_event.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_work_event.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_work_event.last_modified_date_time IS '更新时间';

CREATE TABLE IF NOT EXISTS sy_work_instance
(
  id VARCHAR(200) NOT NULL,
  version_id VARCHAR(200),
  work_id VARCHAR(200),
  instance_type VARCHAR(200),
  status VARCHAR(200),
  plan_start_date_time TIMESTAMP,
  next_plan_date_time TIMESTAMP,
  exec_start_date_time TIMESTAMP,
  exec_end_date_time TIMESTAMP,
  submit_log TEXT,
  yarn_log TEXT,
  spark_star_res VARCHAR(2000),
  result_data TEXT,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  workflow_instance_id VARCHAR(100),
  quartz_has_run BOOLEAN,
  duration INT,
  work_info TEXT,
  event_id VARCHAR(100),
  runner_owner VARCHAR(200),
  heartbeat_date_time TIMESTAMP,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_work_instance.id IS '实例唯一id';
COMMENT ON COLUMN sy_work_instance.version_id IS '实例版本id';
COMMENT ON COLUMN sy_work_instance.work_id IS '作业id';
COMMENT ON COLUMN sy_work_instance.instance_type IS '实例类型';
COMMENT ON COLUMN sy_work_instance.status IS '实例状态';
COMMENT ON COLUMN sy_work_instance.plan_start_date_time IS '计划开始时间';
COMMENT ON COLUMN sy_work_instance.next_plan_date_time IS '下一次开始时间';
COMMENT ON COLUMN sy_work_instance.exec_start_date_time IS '执行开始时间';
COMMENT ON COLUMN sy_work_instance.exec_end_date_time IS '执行结束时间';
COMMENT ON COLUMN sy_work_instance.submit_log IS '提交日志';
COMMENT ON COLUMN sy_work_instance.yarn_log IS 'yarn日志';
COMMENT ON COLUMN sy_work_instance.spark_star_res IS 'spark-star插件返回';
COMMENT ON COLUMN sy_work_instance.result_data IS '结果数据';
COMMENT ON COLUMN sy_work_instance.create_by IS '创建人';
COMMENT ON COLUMN sy_work_instance.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_work_instance.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_work_instance.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_work_instance.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_work_instance.tenant_id IS '租户id';
COMMENT ON COLUMN sy_work_instance.workflow_instance_id IS '工作流实例id';
COMMENT ON COLUMN sy_work_instance.quartz_has_run IS '是否被定时器触发过';
COMMENT ON COLUMN sy_work_instance.duration IS '耗时时间（秒）';
COMMENT ON COLUMN sy_work_instance.work_info IS '存储作业信息';

CREATE TABLE IF NOT EXISTS sy_work_version
(
  id VARCHAR(200) NOT NULL,
  work_id VARCHAR(200) NOT NULL,
  work_type VARCHAR(200) NOT NULL,
  datasource_id VARCHAR(200),
  cluster_config TEXT,
  script TEXT,
  cron_config TEXT NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  sync_work_config TEXT,
  sync_rule TEXT,
  jar_job_config TEXT,
  lib_config TEXT,
  func_config TEXT,
  container_id VARCHAR(200),
  api_work_config TEXT,
  alarm_list TEXT,
  excel_sync_config TEXT,
  db_migrate_config TEXT,
  query_config TEXT,
  lib_package_config TEXT,
  spark_etl_config TEXT,
  sync_flink_config TEXT,
  api_sync_config TEXT,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_work_version.id IS '版本唯一id';
COMMENT ON COLUMN sy_work_version.work_id IS '作业id';
COMMENT ON COLUMN sy_work_version.work_type IS '作业类型';
COMMENT ON COLUMN sy_work_version.datasource_id IS '数据源id';
COMMENT ON COLUMN sy_work_version.cluster_config IS '集群配置';
COMMENT ON COLUMN sy_work_version.script IS '脚本内容';
COMMENT ON COLUMN sy_work_version.cron_config IS '定时表达配置';
COMMENT ON COLUMN sy_work_version.create_by IS '创建人';
COMMENT ON COLUMN sy_work_version.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_work_version.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_work_version.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_work_version.version_number IS '版本号';
COMMENT ON COLUMN sy_work_version.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_work_version.tenant_id IS '租户id';
COMMENT ON COLUMN sy_work_version.sync_work_config IS '同步作业的配置';
COMMENT ON COLUMN sy_work_version.sync_rule IS '数据同步规则';
COMMENT ON COLUMN sy_work_version.jar_job_config IS '自定义作业配置';
COMMENT ON COLUMN sy_work_version.lib_config IS '依赖配置';
COMMENT ON COLUMN sy_work_version.func_config IS '自定义函数配置';
COMMENT ON COLUMN sy_work_version.api_work_config IS '接口调用作业的配置';
COMMENT ON COLUMN sy_work_version.alarm_list IS '绑定的基线';
COMMENT ON COLUMN sy_work_version.lib_package_config IS '任务包配置';

CREATE TABLE IF NOT EXISTS sy_workflow
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  remark VARCHAR(500),
  status VARCHAR(200) NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  config_id VARCHAR(100),
  version_id VARCHAR(100),
  type VARCHAR(200),
  default_cluster_id VARCHAR(200),
  next_date_time TIMESTAMP,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_workflow.id IS '作业流唯一id';
COMMENT ON COLUMN sy_workflow.name IS '作业流名称';
COMMENT ON COLUMN sy_workflow.remark IS '作业流描述';
COMMENT ON COLUMN sy_workflow.status IS '状态';
COMMENT ON COLUMN sy_workflow.create_by IS '创建人';
COMMENT ON COLUMN sy_workflow.create_date_time IS '创建时间';
COMMENT ON COLUMN sy_workflow.last_modified_by IS '更新人';
COMMENT ON COLUMN sy_workflow.last_modified_date_time IS '更新时间';
COMMENT ON COLUMN sy_workflow.version_number IS '版本号';
COMMENT ON COLUMN sy_workflow.deleted IS '逻辑删除';
COMMENT ON COLUMN sy_workflow.tenant_id IS '租户id';
COMMENT ON COLUMN sy_workflow.config_id IS '工作流配置id';
COMMENT ON COLUMN sy_workflow.version_id IS '版本id';
COMMENT ON COLUMN sy_workflow.type IS '工作流类型';
COMMENT ON COLUMN sy_workflow.default_cluster_id IS '默认计算引擎';
COMMENT ON COLUMN sy_workflow.next_date_time IS '下一个日期时间';

CREATE TABLE IF NOT EXISTS sy_workflow_config
(
  id VARCHAR(200) NOT NULL,
  web_config TEXT,
  node_mapping TEXT,
  node_list VARCHAR(2000),
  dag_start_list VARCHAR(2000),
  dag_end_list VARCHAR(2000),
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  cron_config TEXT,
  external_call CHAR(1),
  access_key VARCHAR(100),
  alarm_list TEXT,
  invoke_status VARCHAR(100) DEFAULT 'OFF' NOT NULL,
  invoke_url TEXT,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_workflow_config.cron_config IS '定时表达式';
COMMENT ON COLUMN sy_workflow_config.external_call IS '外部调用标志';
COMMENT ON COLUMN sy_workflow_config.access_key IS '访问密钥';
COMMENT ON COLUMN sy_workflow_config.alarm_list IS '绑定的基线';
COMMENT ON COLUMN sy_workflow_config.invoke_status IS '是否启动外部调用';

CREATE TABLE IF NOT EXISTS sy_workflow_favour
(
  id VARCHAR(200) NOT NULL,
  workflow_id VARCHAR(200),
  user_id VARCHAR(200),
  top_index INT,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS sy_workflow_instance
(
  id VARCHAR(200) NOT NULL,
  version_id VARCHAR(200),
  flow_id VARCHAR(200),
  instance_type VARCHAR(200),
  status VARCHAR(200),
  run_log TEXT,
  web_config TEXT,
  plan_start_date_time TIMESTAMP,
  next_plan_date_time TIMESTAMP,
  exec_start_date_time TIMESTAMP,
  exec_end_date_time TIMESTAMP,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  duration INT,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_workflow_instance.duration IS '耗时时间（秒）';

CREATE TABLE IF NOT EXISTS sy_workflow_version
(
  id VARCHAR(200) NOT NULL,
  name VARCHAR(200) NOT NULL,
  workflow_id VARCHAR(200) NOT NULL,
  workflow_type VARCHAR(200),
  node_mapping TEXT,
  node_list TEXT,
  dag_start_list TEXT,
  dag_end_list TEXT,
  work_version_map TEXT,
  cron_config VARCHAR(2000) NOT NULL,
  create_by VARCHAR(200) NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(200) NOT NULL,
  last_modified_date_time TIMESTAMP NOT NULL,
  version_number INT NOT NULL,
  deleted INT DEFAULT 0 NOT NULL,
  tenant_id VARCHAR(200) NOT NULL,
  web_config TEXT,
  alarm_list TEXT,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN sy_workflow_version.cron_config IS '定时表达式';
COMMENT ON COLUMN sy_workflow_version.web_config IS '作业流的dag图';
COMMENT ON COLUMN sy_workflow_version.alarm_list IS '绑定的基线';

INSERT INTO sy_user (
  id, username, account, passwd, role_code, status, create_by, create_date_time,
  last_modified_by, last_modified_date_time, version_number
)
VALUES (
  'admin_id', '系统管理员', 'admin', '', 'ROLE_SYS_ADMIN', 'ENABLE', 'admin_id', NOW(),
  'admin_id', NOW(), 0
);

INSERT INTO sy_database_driver (
  id, name, db_type, file_name, driver_type, create_by, create_date_time,
  last_modified_by, last_modified_date_time, version_number, deleted, tenant_id, remark, is_default_driver
)
VALUES
  ('clickhouse_0.8.2', 'clickhouse_0.8.2', 'CLICKHOUSE', 'clickhouse-jdbc-0.8.2-shaded-all.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('db2_11.5.8.0', 'db2_11.5.8.0', 'DB2', 'jcc-11.5.8.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('dm_8.1.1.49', 'dm_8.1.1.49', 'DM', 'Dm8JdbcDriver18-8.1.1.49.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('doris_mysql_5.1.49', 'doris_mysql_5.1.49', 'DORIS', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('duckdb_jdbc-1.4.2.0', 'duckdb_jdbc-1.4.2.0', 'DUCK_DB', 'duckdb_jdbc-1.4.2.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('gauss(postgresql_42.6.0)', 'gauss(postgresql_42.6.0)', 'GAUSS', 'postgresql-42.6.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('gbase8a', 'gbase8a', 'GBASE', 'gbase-connector-java-9.5.0.7-build1-bin.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('greenplum(postgresql-42.6.0)', 'greenplum(postgresql-42.6.0)', 'GREENPLUM', 'postgresql-42.6.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('h2-2.2.224', 'h2-2.2.224', 'H2', 'h2-2.2.224.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('hana_2.18.13', 'hana_2.18.13', 'HANA_SAP', 'ngdbc-2.18.13.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('hive_3.1.3', 'hive_3.1.3', 'HIVE', 'hive-jdbc-3.1.3-standalone.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', FALSE),
  ('hive_uber_2.6.3.jar', 'hive_uber_2.6.3.jar', 'HIVE', 'hive-jdbc-uber-2.6.3.0-235.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('impala_hive_uber_2.6.3', 'impala_hive_uber_2.6.3', 'IMPALA', 'hive-jdbc-uber-2.6.3.0-235.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('kafka_client_3.1.2', 'kafka_client_3.1.2', 'KAFKA', 'kafka_client_3.1.2.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('mysql_5.1.49', 'mysql_5.1.49', 'MYSQL', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', FALSE),
  ('mysql_8.1.0', 'mysql_8.1.0', 'MYSQL', 'mysql-connector-j-8.1.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('oceanbase_2.4.6', 'oceanbase_2.4.6', 'OCEANBASE', 'oceanbase-client-2.4.6.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('ojdbc8-19.23.0.0', 'ojdbc8-19.23.0.0', 'ORACLE', 'ojdbc8-19.23.0.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('open_gauss(postgresql_42.6.0)', 'open_gauss(postgresql_42.6.0)', 'OPEN_GAUSS', 'postgresql-42.6.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('oracle_11g', 'oracle_11g', 'ORACLE', 'ojdbc6.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', FALSE),
  ('postgre_42.6.0', 'postgre_42.6.0', 'POSTGRE_SQL', 'postgresql-42.6.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('presto-jdbc-0.295', 'presto-jdbc-0.295', 'PRESTO', 'presto-jdbc-0.295.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('selectDB_mysql_5.1.49', 'selectDB_mysql_5.1.49', 'SELECT_DB', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('sqlServer_12.4.2.jre8', 'sqlServer_12.4.2.jre8', 'SQL_SERVER', 'mssql-jdbc-12.4.2.jre8.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('star_rocks(mysql_5.1.49)', 'star_rocks(mysql_5.1.49)', 'STAR_ROCKS', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('sybase', 'sybase', 'SYBASE', 'jconn4-16.0.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('taos-jdbcdriver-3.7.7-dist', 'taos-jdbcdriver-3.7.7-dist', 'T_DENGINE', 'taos-jdbcdriver-3.7.7-dist.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('tidb(mysql_5.1.49)', 'tidb(mysql_5.1.49)', 'TIDB', 'mysql-connector-java-5.1.49.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE),
  ('trino-jdbc-418', 'trino-jdbc-418', 'TRINO', 'trino-jdbc-418.jar', 'SYSTEM_DRIVER', 'zhiqingyun', '2023-11-01 16:54:34', 'zhiqingyun', '2023-11-01 16:54:39', 1, 0, 'zhiqingyun', '系统自带驱动', TRUE);
