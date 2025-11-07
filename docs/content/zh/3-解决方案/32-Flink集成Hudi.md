---
title: "Flink集成Hudi"
---

#### 案例

> 使用flink对Hudi数据进行增删改查

#### 解决方案

> 创建`FlinkSql查询作业`，下载依赖

- [hudi-flink1.18-bundle-1.0.2.jar下载](https://repo1.maven.org/maven2/org/apache/hudi/hudi-flink1.18-bundle/1.0.2/hudi-flink1.18-bundle-1.0.2.jar)
- [hive-exec-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-exec/3.1.3/hive-exec-3.1.3.jar)
- [hadoop-mapreduce-client-core-3.3.5.jar下载](https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-mapreduce-client-core/3.3.5/hadoop-mapreduce-client-core-3.3.5.jar)

![20250120182130](https://img.isxcode.com/picgo/20250120182130.png)

#### FlinkSql

```sql
CREATE TABLE hudi_table2(
    username VARCHAR(50) PRIMARY KEY NOT ENFORCED,
    age INT,
    ts BIGINT
)
PARTITIONED BY (`age`)
WITH (
  'connector' = 'hudi',
  'path' = 'hdfs://172.19.189.246:9000/user/hive/warehouse/ispong_db.db/hudi_table2',
  'table.type' = 'MERGE_ON_READ',
  
  -- 主键配置（修正）
  'hoodie.datasource.write.recordkey.field' = 'username',
  'hoodie.datasource.write.partitionpath.field' = 'age',
  'hoodie.datasource.write.keygenerator.class' = 'org.apache.hudi.keygen.ComplexAvroKeyGenerator',
  'hoodie.datasource.write.hive_style_partitioning' = 'true',
  
  -- Hive 同步配置（修正）
  'hive_sync.enable' = 'true',
  'hive_sync.mode' = 'hms',
  'hive_sync.metastore.uris' = 'thrift://172.19.189.246:9083',
  'hive_sync.db' = 'ispong_db',  -- 与路径中的数据库一致
  'hive_sync.table' = 'hudi_table2',
  'hive_sync.partition_fields' = 'age',  -- 与实际分区字段一致
  'hive_sync.partition_extractor_class' = 'org.apache.hudi.hive.MultiPartKeysValueExtractor',
  
  -- 其他必要配置
  'hive_sync.support_timestamp' = 'true',
  'hive_sync.skip_ro_suffix' = 'true'
);

INSERT INTO hudi_table2 VALUES
('user001', 25, 1704067200000),
('user002', 30, 1704067201000);
```
