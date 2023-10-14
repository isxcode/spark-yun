ALTER TABLE SY_WORK_CONFIG ADD bash_script text NULL;
ALTER TABLE SY_WORK_CONFIG CHANGE bash_script bash_script text NULL AFTER sql_script;

ALTER TABLE SY_WORK_CONFIG ADD cluster_node_id varchar(200) NULL;
ALTER TABLE SY_WORK_CONFIG CHANGE cluster_node_id cluster_node_id varchar(200) NULL AFTER cluster_id;

