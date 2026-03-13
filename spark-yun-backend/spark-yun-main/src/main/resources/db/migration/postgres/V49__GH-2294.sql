-- 数据源添加高级配置字段
ALTER TABLE SY_DATASOURCE ADD COLUMN connect_config text;
COMMENT ON COLUMN SY_DATASOURCE.connect_config IS '高级配置';

