CREATE TABLE IF NOT EXISTS sy_alarm
(
  id VARCHAR(200) NOT NULL COMMENT '告警id',
  name VARCHAR(200) NOT NULL COMMENT '告警名称',
  status VARCHAR(100) NOT NULL COMMENT '消息状态，启动/停止',
  remark VARCHAR(500) COMMENT '告警备注',
  alarm_type VARCHAR(200) NOT NULL COMMENT '告警类型，作业/作业流',
  alarm_event VARCHAR(200) NOT NULL COMMENT '告警的事件，开始运行/运行失败/运行结束',
  msg_id VARCHAR(100) NOT NULL COMMENT '使用的消息体',
  alarm_template LONGTEXT NOT NULL COMMENT '告警的模版',
  receiver_list LONGTEXT COMMENT '告警接受者',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_alarm_instance
(
  id VARCHAR(200) NOT NULL COMMENT '告警实例id',
  alarm_id VARCHAR(200) NOT NULL COMMENT '告警id',
  send_status VARCHAR(100) NOT NULL COMMENT '是否发送成功',
  alarm_type VARCHAR(200) NOT NULL COMMENT '告警类型，作业/作业流',
  alarm_event VARCHAR(200) NOT NULL COMMENT '触发的事件',
  msg_id VARCHAR(100) NOT NULL COMMENT '告警的消息体',
  content LONGTEXT NOT NULL COMMENT '发送消息的内容',
  response LONGTEXT NOT NULL COMMENT '事件响应',
  instance_id VARCHAR(200) NOT NULL COMMENT '任务实例id',
  receiver VARCHAR(200) NOT NULL COMMENT '消息接受者',
  send_date_time DATETIME NOT NULL COMMENT '发送的时间',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_api
(
  id VARCHAR(200) NOT NULL COMMENT '唯一API的id',
  name VARCHAR(200) NOT NULL COMMENT 'API名称',
  path VARCHAR(200) NOT NULL COMMENT 'API访问地址',
  api_type VARCHAR(200) NOT NULL COMMENT 'API类型',
  remark VARCHAR(2000) COMMENT 'API备注',
  req_header VARCHAR(2000) COMMENT '请求头',
  req_body VARCHAR(2000) COMMENT '请求体',
  api_sql VARCHAR(2000) COMMENT '执行的sql',
  res_body VARCHAR(2000) NOT NULL COMMENT '响应体',
  datasource_id VARCHAR(200) NOT NULL COMMENT '数据源id',
  status VARCHAR(200) NOT NULL COMMENT 'API状态',
  token_type VARCHAR(200) COMMENT '认证方式',
  page_type BOOL COMMENT '分页状态',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  access_rule_id VARCHAR(200) COMMENT '黑白名单配置id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_api_access_rule
(
  id VARCHAR(200) NOT NULL COMMENT '黑白名单id',
  name VARCHAR(200) NOT NULL COMMENT '规则名称',
  rule_type VARCHAR(200) NOT NULL COMMENT '规则类型: WHITELIST/BLACKLIST',
  ip_address VARCHAR(2000) NOT NULL COMMENT 'IP地址，多个用换行分隔，支持正则',
  remark VARCHAR(500) COMMENT '备注',
  version_number INT NOT NULL COMMENT '版本号',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API黑白名单配置表';

CREATE TABLE IF NOT EXISTS sy_cluster
(
  id VARCHAR(200) NOT NULL COMMENT '集群唯一id',
  name VARCHAR(200) NOT NULL COMMENT '集群名称',
  remark VARCHAR(500) COMMENT '集群描述',
  status VARCHAR(200) NOT NULL COMMENT '集群状态',
  check_date_time DATETIME NOT NULL COMMENT '检测时间',
  all_node_num INT NOT NULL COMMENT '所有节点',
  active_node_num INT NOT NULL COMMENT '激活节点数',
  all_memory_num DOUBLE NOT NULL COMMENT '所有内存',
  used_memory_num DOUBLE NOT NULL COMMENT '已使用内存',
  all_storage_num DOUBLE NOT NULL COMMENT '所有存储',
  used_storage_num DOUBLE NOT NULL COMMENT '已使用存储',
  default_cluster BOOL DEFAULT FALSE COMMENT '默认计算集群',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  cluster_type VARCHAR(100) COMMENT '集群的类型',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_cluster_node
(
  id VARCHAR(200) NOT NULL COMMENT '集群节点唯一id',
  name VARCHAR(200) NOT NULL COMMENT '节点名称',
  remark VARCHAR(500) COMMENT '节点描述',
  status VARCHAR(200) NOT NULL COMMENT '节点状态',
  check_date_time DATETIME NOT NULL COMMENT '检测时间',
  all_memory DOUBLE NOT NULL COMMENT '所有内存',
  used_memory DOUBLE NOT NULL COMMENT '已使用内存',
  all_storage DOUBLE NOT NULL COMMENT '所有存储',
  used_storage DOUBLE NOT NULL COMMENT '已使用存储',
  cpu_percent DOUBLE NOT NULL COMMENT 'cpu使用占比',
  cluster_id VARCHAR(200) NOT NULL COMMENT '集群id',
  host VARCHAR(200) NOT NULL COMMENT '节点服务器host',
  port INT NOT NULL COMMENT '节点服务器端口号',
  agent_log VARCHAR(2000) COMMENT '代理日志',
  username VARCHAR(200) NOT NULL COMMENT '节点服务器用户名',
  passwd VARCHAR(5000) NOT NULL COMMENT '节点服务器密码',
  agent_home_path VARCHAR(200) NOT NULL COMMENT '至轻云代理安装目录',
  agent_port VARCHAR(200) NOT NULL COMMENT '至轻云代理服务端口号',
  hadoop_home_path VARCHAR(200) COMMENT 'hadoop家目录',
  spark_home_path VARCHAR(200) COMMENT 'standalone模式spark的安装目录',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  install_spark_local BOOL DEFAULT FALSE COMMENT '是否安装spark-local组件',
  install_flink_local BOOL DEFAULT FALSE COMMENT '是否安装flink-local组件',
  flink_home_path VARCHAR(200) COMMENT 'standalone模式flink的安装目录',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_column_format
(
  id VARCHAR(200) NOT NULL COMMENT '字段标准id',
  name VARCHAR(200) NOT NULL COMMENT '标准名称',
  column_type_code VARCHAR(200) NOT NULL COMMENT '字段类型编码，TEXT、LONGTEXT、CUSTOM',
  column_type VARCHAR(200) COMMENT '字段长度，字段配置',
  column_rule VARCHAR(500) COMMENT '字段规范',
  status VARCHAR(200) NOT NULL COMMENT '是否启用',
  remark VARCHAR(200) COMMENT '字段标准备注',
  is_null VARCHAR(10) NOT NULL COMMENT '可以为null',
  is_duplicate VARCHAR(10) NOT NULL COMMENT '可以重复',
  is_partition VARCHAR(10) NOT NULL COMMENT '是分区键',
  is_primary VARCHAR(10) NOT NULL COMMENT '是主键',
  default_value VARCHAR(200) COMMENT '默认值',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段标准表';

CREATE TABLE IF NOT EXISTS sy_container
(
  id VARCHAR(200) NOT NULL COMMENT '容器id',
  name VARCHAR(200) NOT NULL COMMENT '容器名称',
  remark VARCHAR(500) COMMENT '容器备注',
  status VARCHAR(200) NOT NULL COMMENT '容器状态',
  datasource_id VARCHAR(200) NOT NULL COMMENT '数据源id',
  cluster_id VARCHAR(200) NOT NULL COMMENT '集群id',
  resource_level VARCHAR(200) NOT NULL COMMENT '消耗资源等级',
  spark_config VARCHAR(2000) COMMENT 'spark配置',
  port INT COMMENT '容器端口号',
  submit_log LONGTEXT COMMENT '容器端口号',
  running_log LONGTEXT COMMENT '容器端口号',
  application_id VARCHAR(200) COMMENT '应用id',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_data_layer
(
  id VARCHAR(200) NOT NULL COMMENT '数据分层id',
  name VARCHAR(200) NOT NULL COMMENT '分层名称',
  table_rule VARCHAR(500) COMMENT '表名分层规范',
  parent_layer_id VARCHAR(200) COMMENT '父层级分层id',
  parent_id_list VARCHAR(500) COMMENT '父级链条',
  parent_name_list VARCHAR(500) COMMENT '父级链条名称',
  remark VARCHAR(500) COMMENT '分层描述',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据分层表';

CREATE TABLE IF NOT EXISTS sy_data_model
(
  id VARCHAR(200) NOT NULL COMMENT '数据模型id',
  name VARCHAR(200) NOT NULL COMMENT '模型名称',
  layer_id VARCHAR(200) NOT NULL COMMENT '数据分层id',
  db_type VARCHAR(200) NOT NULL COMMENT '数据源类型',
  datasource_id VARCHAR(200) NOT NULL COMMENT '数据源id',
  table_name VARCHAR(200) NOT NULL COMMENT '表名',
  model_type VARCHAR(200) NOT NULL COMMENT '模型类型，LINK_TABLE 关联模型,MODEL_TABLE 原始模型',
  table_config LONGTEXT,
  status VARCHAR(200) NOT NULL COMMENT '数据模型状态，INIT 新建、SUCCESS 成功、ERROR 失败',
  build_log LONGTEXT,
  remark VARCHAR(200) COMMENT '数据模型备注',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据模型表';

CREATE TABLE IF NOT EXISTS sy_data_model_column
(
  id VARCHAR(200) NOT NULL COMMENT '模型字段id',
  name VARCHAR(200) NOT NULL COMMENT '字段名称',
  column_name VARCHAR(200) NOT NULL COMMENT '表字段名',
  model_id VARCHAR(200) NOT NULL COMMENT '数据模型id',
  column_format_id VARCHAR(200) COMMENT '字段标准id',
  column_index INT NOT NULL COMMENT '字段顺序',
  link_column_type VARCHAR(200) COMMENT '关联模型字段类型',
  remark VARCHAR(200) COMMENT '模型字段备注',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模型字段表';

CREATE TABLE IF NOT EXISTS sy_database_driver
(
  id VARCHAR(200) NOT NULL COMMENT '数据源驱动唯一id',
  name VARCHAR(200) NOT NULL COMMENT '数据源驱动名称',
  db_type VARCHAR(200) NOT NULL COMMENT '数据源类型',
  file_name VARCHAR(500) NOT NULL COMMENT '驱动名称',
  remark VARCHAR(500) COMMENT '描述',
  driver_type VARCHAR(200) NOT NULL COMMENT '驱动类型',
  is_default_driver BOOL NOT NULL COMMENT '是否为默认驱动',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_datasource
(
  id VARCHAR(200) NOT NULL COMMENT '数据源唯一id',
  name VARCHAR(200) NOT NULL COMMENT '数据源名称',
  jdbc_url VARCHAR(500) NOT NULL COMMENT '数据源jdbcUrl',
  remark VARCHAR(500) COMMENT '描述',
  status VARCHAR(200) NOT NULL COMMENT '状态',
  check_date_time DATETIME NOT NULL COMMENT '检测时间',
  username VARCHAR(200) COMMENT '数据源用户名',
  passwd VARCHAR(200) COMMENT '数据源密码',
  connect_log VARCHAR(2000) COMMENT '测试连接日志',
  db_type VARCHAR(200) NOT NULL COMMENT '数据源类型',
  metastore_uris VARCHAR(500),
  driver_id VARCHAR(100) NOT NULL COMMENT '数据库驱动id',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  kafka_config LONGTEXT COMMENT 'kafka数据源配置',
  connect_config LONGTEXT COMMENT '高级配置',
  fe_nodes VARCHAR(500),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_file
(
  id VARCHAR(200) NOT NULL COMMENT '文件配置唯一id',
  file_name VARCHAR(200) COMMENT '文件名称',
  file_size VARCHAR(200) COMMENT '文件大小',
  file_type VARCHAR(200) COMMENT '文件类型',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  remark VARCHAR(500) COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_form
(
  id VARCHAR(200) NOT NULL COMMENT '自定义表单唯一id',
  name VARCHAR(200) NOT NULL COMMENT '表单名称',
  datasource_id VARCHAR(200) NOT NULL COMMENT '数据源id',
  main_table VARCHAR(200) COMMENT '主要对象表id',
  status VARCHAR(200) COMMENT '自定义表单状态',
  remark VARCHAR(500) COMMENT '表单备注',
  insert_sql VARCHAR(2000) COMMENT '增sql语句',
  delete_sql VARCHAR(2000) COMMENT '删sql语句',
  update_sql VARCHAR(2000) COMMENT '改sql语句',
  select_sql VARCHAR(2000) COMMENT '查sql语句',
  form_web_config LONGTEXT COMMENT '前端所有的配置',
  form_version VARCHAR(50) COMMENT '表单版本号',
  create_mode VARCHAR(50) NOT NULL COMMENT '该表单主表的创建模式',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_form_component
(
  id VARCHAR(200) NOT NULL COMMENT '表单字段组件唯一id',
  form_id VARCHAR(200) COMMENT '自定义表单id',
  uuid VARCHAR(50) NOT NULL COMMENT '前端的uuid',
  component_config LONGTEXT COMMENT '组件的配置',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_form_link
(
  id VARCHAR(200) NOT NULL COMMENT '分享表单链接id',
  form_id VARCHAR(200) NOT NULL COMMENT '表单id',
  form_version VARCHAR(200) NOT NULL COMMENT '表单版本',
  form_token VARCHAR(500) NOT NULL COMMENT '分享表单的匿名token',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  invalid_date_time DATETIME NOT NULL COMMENT '到期时间',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_func
(
  id VARCHAR(200) NOT NULL COMMENT 'udf唯一id',
  type VARCHAR(20) NOT NULL COMMENT 'UDF或者UDAF',
  file_id VARCHAR(200) NOT NULL COMMENT 'jar文件id',
  func_name VARCHAR(200) NOT NULL COMMENT 'udf调用方法名',
  class_name VARCHAR(200) NOT NULL COMMENT 'udf方法class名称',
  result_type VARCHAR(200) NOT NULL COMMENT '返回值类型',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  remark VARCHAR(500) COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_lib_package
(
  id VARCHAR(200) NOT NULL COMMENT '依赖包id',
  name VARCHAR(200) NOT NULL COMMENT '依赖包名称',
  file_id_list VARCHAR(5000) COMMENT '依赖包中依赖',
  remark VARCHAR(500) COMMENT '依赖包备注',
  version_number INT NOT NULL COMMENT '版本号',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_license
(
  id VARCHAR(200) NOT NULL COMMENT '许可证唯一id',
  code VARCHAR(200) NOT NULL COMMENT '许可证编号',
  company_name VARCHAR(200) NOT NULL COMMENT '公司名称',
  logo VARCHAR(2000) NOT NULL COMMENT '公司logo',
  remark VARCHAR(2000) COMMENT '许可证备注',
  issuer VARCHAR(200) NOT NULL COMMENT '许可证签发人',
  start_date_time DATETIME NOT NULL COMMENT '许可证起始时间',
  end_date_time DATETIME NOT NULL COMMENT '许可证到期时间',
  max_tenant_num INT COMMENT '最大租户数',
  max_member_num INT COMMENT '最大成员数',
  max_workflow_num INT COMMENT '最大作业流数',
  status VARCHAR(200) NOT NULL COMMENT '证书状态',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_locker
(
  id INT AUTO_INCREMENT NOT NULL,
  name VARCHAR(200) NOT NULL,
  box VARCHAR(2000),
  lock_owner VARCHAR(200),
  expire_time DATETIME,
  create_date_time DATETIME,
  PRIMARY KEY (id),
  CONSTRAINT uk_sy_locker_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_message
(
  id VARCHAR(200) NOT NULL COMMENT '消息消息体id',
  name VARCHAR(200) NOT NULL COMMENT '消息体名称',
  status VARCHAR(100) NOT NULL COMMENT '消息体状态',
  remark VARCHAR(500) COMMENT '消息体备注',
  msg_type VARCHAR(200) NOT NULL COMMENT '消息体类型，邮箱/阿里短信/飞书',
  msg_config LONGTEXT NOT NULL COMMENT '消息体配置信息',
  response LONGTEXT COMMENT '检测响应',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_meta_column
(
  datasource_id VARCHAR(200) NOT NULL COMMENT '元数据表id',
  table_name VARCHAR(200) NOT NULL COMMENT '表名',
  column_name VARCHAR(200) NOT NULL COMMENT '字段名',
  column_type VARCHAR(200) NOT NULL COMMENT '字段类型',
  column_comment VARCHAR(500) COMMENT '字段备注',
  is_partition_column BOOL NOT NULL COMMENT '是否为分区字段',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (datasource_id, table_name, column_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_meta_column_info
(
  datasource_id VARCHAR(200) NOT NULL COMMENT '元数据表id',
  table_name VARCHAR(200) NOT NULL COMMENT '表名',
  column_name VARCHAR(200) NOT NULL COMMENT '字段名',
  custom_comment VARCHAR(500) COMMENT '字段备注',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (datasource_id, table_name, column_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_meta_column_lineage
(
  id VARCHAR(200) NOT NULL COMMENT '字段血缘id',
  from_db_id VARCHAR(200) NOT NULL COMMENT '来源数据源',
  from_table_name VARCHAR(200) NOT NULL COMMENT '来源表名',
  from_column_name VARCHAR(200) NOT NULL COMMENT '来源字段名',
  work_id VARCHAR(200) NOT NULL COMMENT '作业',
  work_version_id VARCHAR(200) NOT NULL COMMENT '作业版本',
  to_db_id VARCHAR(200) NOT NULL COMMENT '去向数据源',
  to_table_name VARCHAR(200) NOT NULL COMMENT '去向表名',
  to_column_name VARCHAR(200) NOT NULL COMMENT '去向字段名',
  remark VARCHAR(500) COMMENT '血缘备注',
  version_number INT NOT NULL COMMENT '版本号',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段血缘表';

CREATE TABLE IF NOT EXISTS sy_meta_database
(
  datasource_id VARCHAR(200) NOT NULL COMMENT '元数据数据源id',
  name VARCHAR(200) NOT NULL COMMENT '数据源名称',
  db_name VARCHAR(200) COMMENT 'db名称',
  db_type VARCHAR(200) NOT NULL COMMENT '数据源类型',
  db_comment VARCHAR(500) COMMENT '数据源备注',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  status VARCHAR(200),
  PRIMARY KEY (datasource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_meta_instance
(
  id VARCHAR(200) NOT NULL COMMENT '元数据采集实例id',
  meta_work_id VARCHAR(200) NOT NULL COMMENT '元数据采集任务id',
  trigger_type VARCHAR(200) NOT NULL COMMENT '触发类型',
  status VARCHAR(500) NOT NULL COMMENT '实例类型',
  start_date_time DATETIME NOT NULL COMMENT '开始时间',
  end_date_time DATETIME COMMENT '结束时间',
  collect_log LONGTEXT COMMENT '采集日志',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  runner_owner VARCHAR(200),
  heartbeat_date_time DATETIME,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_meta_table
(
  datasource_id VARCHAR(200) NOT NULL COMMENT '元数据表id',
  table_name VARCHAR(200) NOT NULL COMMENT '表名',
  table_comment VARCHAR(500) COMMENT '表备注',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (datasource_id, table_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_meta_table_info
(
  datasource_id VARCHAR(200) NOT NULL COMMENT '元数据表id',
  table_name VARCHAR(200) NOT NULL COMMENT '表名',
  column_count BIGINT COMMENT '字段数',
  total_rows BIGINT COMMENT '总条数',
  total_size BIGINT COMMENT '总大小',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  custom_comment VARCHAR(500),
  PRIMARY KEY (datasource_id, table_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_meta_work
(
  id VARCHAR(200) NOT NULL COMMENT '元数据采集任务id',
  name VARCHAR(200) NOT NULL COMMENT '任务名',
  db_type VARCHAR(200) NOT NULL COMMENT '数据源类型',
  datasource_id VARCHAR(100) NOT NULL COMMENT '数据源id',
  collect_type VARCHAR(100) NOT NULL COMMENT '采集方式',
  table_pattern VARCHAR(100) NOT NULL COMMENT '表名表达式',
  cron_config VARCHAR(500) COMMENT '调度配置',
  status VARCHAR(500) NOT NULL COMMENT '任务状态',
  remark VARCHAR(500) COMMENT '表备注',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_monitor
(
  id VARCHAR(200) NOT NULL COMMENT '分享表单链接id',
  cluster_id VARCHAR(200) NOT NULL COMMENT '集群id',
  cluster_node_id VARCHAR(200) NOT NULL COMMENT '集群节点id',
  status VARCHAR(100) NOT NULL COMMENT '监控状态',
  log LONGTEXT NOT NULL COMMENT '日志',
  used_storage_size DOUBLE,
  used_memory_size DOUBLE,
  network_io_read_speed DOUBLE,
  network_io_write_speed DOUBLE,
  disk_io_read_speed DOUBLE,
  disk_io_write_speed DOUBLE,
  cpu_percent DOUBLE COMMENT 'cpu占用',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_real
(
  id VARCHAR(200) NOT NULL COMMENT '分享表单链接id',
  name VARCHAR(200) NOT NULL COMMENT '实时作业名称',
  status VARCHAR(200) NOT NULL COMMENT '运行状态',
  cluster_id VARCHAR(500) NOT NULL COMMENT '集群id',
  spark_config LONGTEXT NOT NULL COMMENT '集群配置',
  sync_config LONGTEXT COMMENT '数据同步配置',
  lib_config VARCHAR(500) COMMENT '依赖配置',
  func_config VARCHAR(500) COMMENT '函数配置',
  submit_log LONGTEXT COMMENT '提交日志',
  running_log LONGTEXT COMMENT '运行日志',
  application_id VARCHAR(500) COMMENT '应用id',
  remark VARCHAR(500) COMMENT '备注',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_secret_key
(
  id VARCHAR(200) NOT NULL COMMENT '全局变量id',
  key_name VARCHAR(200) NOT NULL COMMENT '全局变量key',
  secret_value VARCHAR(500) NOT NULL COMMENT '全局变量value',
  remark VARCHAR(500) COMMENT '全局变量备注',
  version_number INT NOT NULL COMMENT '版本号',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_sso_auth
(
  id VARCHAR(200) NOT NULL COMMENT '单点id',
  name VARCHAR(200) NOT NULL COMMENT '单点名称',
  status VARCHAR(200) NOT NULL COMMENT '单点状态',
  sso_type VARCHAR(200) NOT NULL COMMENT '单点类型',
  client_id VARCHAR(200) NOT NULL COMMENT 'clientId',
  client_secret VARCHAR(500) NOT NULL COMMENT 'clientSecret',
  scope VARCHAR(500) COMMENT 'scope',
  auth_url VARCHAR(500) NOT NULL COMMENT '授权认证地址',
  access_token_url VARCHAR(500) NOT NULL COMMENT 'token获取地址',
  redirect_url VARCHAR(500) NOT NULL COMMENT '跳转地址',
  user_url VARCHAR(500) NOT NULL COMMENT '获取用户信息地址',
  auth_json_path VARCHAR(500) NOT NULL COMMENT '解析用户信息jsonPath',
  remark VARCHAR(500) COMMENT '单点备注',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='单点登录认证表';

CREATE TABLE IF NOT EXISTS sy_tenant
(
  id VARCHAR(200) NOT NULL COMMENT '租户唯一id',
  name VARCHAR(200) NOT NULL COMMENT '租户名称',
  used_member_num INT NOT NULL COMMENT '已使用成员数',
  max_member_num INT NOT NULL COMMENT '最大成员数',
  used_workflow_num INT NOT NULL COMMENT '已使用作业流数',
  max_workflow_num INT NOT NULL COMMENT '最大作业流数',
  status VARCHAR(200) NOT NULL COMMENT '租户状态',
  introduce VARCHAR(500) COMMENT '租户简介',
  remark VARCHAR(500) COMMENT '租户描述',
  check_date_time DATETIME NOT NULL COMMENT '检测时间',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  valid_start_date_time DATETIME,
  valid_end_date_time DATETIME,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_tenant_users
(
  id VARCHAR(200) NOT NULL COMMENT '关系唯一id',
  user_id VARCHAR(200) NOT NULL COMMENT '用户id',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  role_code VARCHAR(200) NOT NULL COMMENT '角色编码',
  status VARCHAR(200) NOT NULL COMMENT '用户状态',
  remark VARCHAR(200) COMMENT '备注',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_user
(
  id VARCHAR(200) NOT NULL COMMENT '用户唯一id',
  username VARCHAR(200) NOT NULL COMMENT '用户名称',
  account VARCHAR(200) NOT NULL COMMENT '用户账号',
  passwd VARCHAR(200) COMMENT '账号密码',
  phone VARCHAR(200) COMMENT '手机号',
  email VARCHAR(200) COMMENT '邮箱',
  introduce VARCHAR(500) COMMENT '简介',
  remark VARCHAR(500) COMMENT '描述',
  role_code VARCHAR(200) NOT NULL COMMENT '角色编码',
  status VARCHAR(200) NOT NULL COMMENT '用户状态',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  current_tenant_id VARCHAR(200) COMMENT '当前用户使用的租户id',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  valid_start_date_time DATETIME,
  valid_end_date_time DATETIME,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_user_action
(
  id VARCHAR(200) NOT NULL COMMENT '用户行为唯一id',
  user_id VARCHAR(200) COMMENT '用户id',
  tenant_id VARCHAR(200) COMMENT '租户id',
  req_path VARCHAR(200) COMMENT '请求路径',
  req_method VARCHAR(200) COMMENT '请求方式',
  req_header VARCHAR(2000) COMMENT '请求头',
  req_body LONGTEXT COMMENT '请求体',
  res_body LONGTEXT COMMENT '响应体',
  start_timestamp BIGINT COMMENT '开始时间戳',
  end_timestamp BIGINT COMMENT '结束时间戳',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_view
(
  id VARCHAR(200) NOT NULL COMMENT '数据大屏id',
  name VARCHAR(200) NOT NULL COMMENT '大屏名称',
  remark VARCHAR(500) COMMENT '大屏备注',
  status VARCHAR(200) NOT NULL COMMENT '大屏状态',
  background_file_id VARCHAR(200) COMMENT '背景图文件id',
  card_list VARCHAR(2000) COMMENT '大屏中包含的卡片',
  web_config LONGTEXT COMMENT '大屏显示配置',
  version_number INT NOT NULL COMMENT '版本号',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_view_card
(
  id VARCHAR(200) NOT NULL COMMENT '大屏组件id',
  name VARCHAR(200) NOT NULL COMMENT '大屏组件名称',
  remark VARCHAR(500) COMMENT '大屏组件备注',
  status VARCHAR(200) NOT NULL COMMENT '大屏组件状态',
  type VARCHAR(200) NOT NULL COMMENT '大屏组件类型',
  datasource_id VARCHAR(200) NOT NULL COMMENT '数据源id',
  example_data VARCHAR(2000) NOT NULL COMMENT '示例数据sql',
  web_config LONGTEXT COMMENT '前端显示配置',
  data_sql VARCHAR(2000) COMMENT '数据sql',
  version_number INT NOT NULL COMMENT '版本号',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_view_link
(
  id VARCHAR(200) NOT NULL COMMENT '数据大屏分享链接id',
  view_id VARCHAR(200) NOT NULL COMMENT '大屏id',
  view_version VARCHAR(200) NOT NULL COMMENT '大屏版本',
  view_token VARCHAR(500) NOT NULL COMMENT '分享大屏的匿名token',
  invalid_date_time DATETIME NOT NULL COMMENT '到期时间',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_work
(
  id VARCHAR(200) NOT NULL COMMENT '作业唯一id',
  name VARCHAR(200) NOT NULL COMMENT '作业名称',
  remark VARCHAR(500) COMMENT '作业描述',
  status VARCHAR(200) NOT NULL COMMENT '作业状态',
  work_type VARCHAR(200) NOT NULL COMMENT '作业类型',
  config_id VARCHAR(200) NOT NULL COMMENT '作业配置id',
  workflow_id VARCHAR(200) NOT NULL COMMENT '作业流id',
  version_id VARCHAR(200) COMMENT '作业当前最新版本号',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  top_index INT COMMENT '作业置顶标志',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_work_config
(
  id VARCHAR(200) NOT NULL COMMENT '作业配置唯一id',
  datasource_id VARCHAR(200) COMMENT '数据源id',
  script LONGTEXT COMMENT 'sql脚本',
  cron_config VARCHAR(2000) COMMENT '定时表达式',
  sync_work_config LONGTEXT COMMENT '同步作业的配置',
  cluster_config LONGTEXT COMMENT '计算集群配置',
  sync_rule LONGTEXT COMMENT '数据同步规则',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  udf_status BOOL DEFAULT FALSE,
  jar_conf LONGTEXT,
  jar_job_config LONGTEXT COMMENT '自定义jar作业配置',
  lib_config LONGTEXT COMMENT '作业依赖文件',
  func_config LONGTEXT COMMENT '自定义函数配置',
  container_id VARCHAR(200),
  api_work_config LONGTEXT COMMENT '接口调用作业的配置',
  alarm_list LONGTEXT COMMENT '绑定的基线',
  excel_sync_config LONGTEXT,
  db_migrate_config LONGTEXT COMMENT '整库同步配置',
  query_config LONGTEXT COMMENT '查询配置',
  lib_package_config LONGTEXT COMMENT '任务包配置',
  spark_etl_config LONGTEXT,
  sync_flink_config LONGTEXT,
  api_sync_config LONGTEXT,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_work_event
(
  id VARCHAR(200) NOT NULL COMMENT '事件id',
  event_process INT NOT NULL COMMENT '事件进程',
  event_context LONGTEXT COMMENT '事件上下文',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业事件表';

CREATE TABLE IF NOT EXISTS sy_work_instance
(
  id VARCHAR(200) NOT NULL COMMENT '实例唯一id',
  version_id VARCHAR(200) COMMENT '实例版本id',
  work_id VARCHAR(200) COMMENT '作业id',
  instance_type VARCHAR(200) COMMENT '实例类型',
  status VARCHAR(200) COMMENT '实例状态',
  plan_start_date_time DATETIME COMMENT '计划开始时间',
  next_plan_date_time DATETIME COMMENT '下一次开始时间',
  exec_start_date_time DATETIME COMMENT '执行开始时间',
  exec_end_date_time DATETIME COMMENT '执行结束时间',
  duration INT COMMENT '耗时时间（秒）',
  submit_log LONGTEXT COMMENT '提交日志',
  yarn_log LONGTEXT COMMENT 'yarn日志',
  spark_star_res VARCHAR(2000) COMMENT 'spark-star插件返回',
  result_data LONGTEXT COMMENT '结果数据',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  workflow_instance_id VARCHAR(100) COMMENT '工作流实例id',
  quartz_has_run BOOLEAN COMMENT '是否被定时器触发过',
  work_info LONGTEXT COMMENT '存储作业信息',
  event_id VARCHAR(100),
  runner_owner VARCHAR(200),
  heartbeat_date_time DATETIME,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_work_version
(
  id VARCHAR(200) NOT NULL COMMENT '版本唯一id',
  work_id VARCHAR(200) NOT NULL COMMENT '作业id',
  work_type VARCHAR(200) NOT NULL COMMENT '作业类型',
  datasource_id VARCHAR(200) COMMENT '数据源id',
  cluster_config VARCHAR(200) COMMENT '集群id',
  script LONGTEXT COMMENT 'sql脚本',
  cron_config VARCHAR(200) NOT NULL COMMENT '定时表达式',
  sync_work_config LONGTEXT COMMENT '同步作业的配置',
  sync_rule LONGTEXT COMMENT '数据同步规则',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  jar_job_config LONGTEXT COMMENT '自定义作业配置',
  lib_config LONGTEXT COMMENT '依赖配置',
  func_config LONGTEXT COMMENT '自定义函数配置',
  container_id VARCHAR(200),
  api_work_config LONGTEXT COMMENT '接口调用作业的配置',
  alarm_list LONGTEXT COMMENT '绑定的基线',
  excel_sync_config LONGTEXT,
  query_config LONGTEXT COMMENT '查询配置',
  lib_package_config LONGTEXT COMMENT '任务包配置',
  spark_etl_config LONGTEXT,
  sync_flink_config LONGTEXT,
  api_sync_config LONGTEXT,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_workflow
(
  id VARCHAR(200) NOT NULL COMMENT '作业流唯一id',
  name VARCHAR(200) NOT NULL COMMENT '作业流名称',
  remark VARCHAR(500) COMMENT '作业流描述',
  status VARCHAR(200) NOT NULL COMMENT '状态',
  default_cluster_id VARCHAR(200) COMMENT '默认计算引擎',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  config_id VARCHAR(100) COMMENT '工作流配置id',
  version_id VARCHAR(100) COMMENT '版本id',
  type VARCHAR(200) COMMENT '工作流类型',
  next_date_time DATETIME COMMENT '是否安装spark-local组件',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_workflow_config
(
  id VARCHAR(200) NOT NULL COMMENT '作业流配置唯一id',
  web_config LONGTEXT COMMENT '前端配置',
  node_mapping LONGTEXT,
  node_list VARCHAR(2000) COMMENT '节点列表',
  dag_start_list VARCHAR(2000) COMMENT 'DAG开始节点列表',
  dag_end_list VARCHAR(2000) COMMENT 'DAG结束节点列表',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  cron_config VARCHAR(300) COMMENT '定时时间',
  external_call CHAR(1),
  access_key VARCHAR(100),
  alarm_list LONGTEXT COMMENT '绑定的基线',
  invoke_status VARCHAR(100) DEFAULT 'OFF' NOT NULL COMMENT '是否启动外部调用',
  invoke_url LONGTEXT,
  db_migrate_config LONGTEXT COMMENT '整库同步配置',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_workflow_favour
(
  id VARCHAR(200) NOT NULL COMMENT '工作流收藏唯一id',
  workflow_id VARCHAR(200) COMMENT '工作流唯一id',
  user_id VARCHAR(200) COMMENT '用户id',
  top_index INT COMMENT 'top排序标志',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_workflow_instance
(
  id VARCHAR(200) NOT NULL COMMENT '实例唯一id',
  version_id VARCHAR(200) COMMENT '实例版本id',
  flow_id VARCHAR(200) COMMENT '作业流id',
  instance_type VARCHAR(200) COMMENT '实例类型',
  status VARCHAR(200) COMMENT '实例状态',
  run_log LONGTEXT COMMENT '作业流运行日志',
  web_config LONGTEXT COMMENT '前端页面配置信息',
  plan_start_date_time DATETIME COMMENT '计划开始时间',
  next_plan_date_time DATETIME COMMENT '下一次开始时间',
  exec_start_date_time DATETIME COMMENT '执行开始时间',
  exec_end_date_time DATETIME COMMENT '执行结束时间',
  duration INT COMMENT '耗时时间（秒）',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sy_workflow_version
(
  id VARCHAR(200) NOT NULL COMMENT '工作流版本唯一id',
  name VARCHAR(200) NOT NULL COMMENT '作业流版本名称',
  workflow_id VARCHAR(200) NOT NULL COMMENT '作业流id',
  workflow_type VARCHAR(200) COMMENT '作业类型',
  node_mapping LONGTEXT COMMENT '节点映射',
  node_list LONGTEXT COMMENT '节点',
  dag_start_list LONGTEXT COMMENT '开始节点',
  dag_end_list LONGTEXT COMMENT '结束节点',
  work_version_map LONGTEXT COMMENT '作业版本映射',
  cron_config VARCHAR(2000) COMMENT '定时表达式',
  web_config LONGTEXT COMMENT '作业流的dag图',
  create_by VARCHAR(200) NOT NULL COMMENT '创建人',
  create_date_time DATETIME NOT NULL COMMENT '创建时间',
  last_modified_by VARCHAR(200) NOT NULL COMMENT '更新人',
  last_modified_date_time DATETIME NOT NULL COMMENT '更新时间',
  version_number INT NOT NULL COMMENT '版本号',
  deleted INT DEFAULT 0 NOT NULL COMMENT '逻辑删除',
  tenant_id VARCHAR(200) NOT NULL COMMENT '租户id',
  alarm_list LONGTEXT COMMENT '绑定的基线',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
