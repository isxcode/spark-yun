---
title: "Flink集成Iceberg"
---

#### 案例

> 使用flink对Iceberg数据进行增删改查

#### 解决方案

> 创建`FlinkSql查询作业`，下载依赖

- [iceberg-flink-runtime-1.18-1.6.1.jar下载](https://repo1.maven.org/maven2/org/apache/iceberg/iceberg-flink-runtime-1.18/1.6.1/iceberg-flink-runtime-1.18-1.6.1.jar)
- [libthrift-0.9.3.jar下载](https://repo1.maven.org/maven2/org/apache/thrift/libthrift/0.9.3/libthrift-0.9.3.jar)
- [libfb303-0.9.3.jar下载](https://repo1.maven.org/maven2/org/apache/thrift/libfb303/0.9.3/libfb303-0.9.3.jar)
- [hive-exec-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-exec/3.1.3/hive-exec-3.1.3.jar)
- [hive-standalone-metastore-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-standalone-metastore/3.1.3/hive-standalone-metastore-3.1.3.jar)
- [hive-common-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-common/3.1.3/hive-common-3.1.3.jar)
- [hadoop-mapreduce-client-core-3.3.5.jar下载](https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-mapreduce-client-core/3.3.5/hadoop-mapreduce-client-core-3.3.5.jar)
- [commons-lang-2.6.jar下载](https://repo1.maven.org/maven2/commons-lang/commons-lang/2.6/commons-lang-2.6.jar)

![20250120182130](https://img.isxcode.com/picgo/20250120182130.png)

#### FlinkSql

```sql
CREATE CATALOG hive_catalog WITH (
  'type'='iceberg',
  'catalog-type'='hive',
  'uri'='thrift://172.19.189.246:9083',
  'clients'='10',
  'property-version'='1',
  'warehouse'='hdfs://172.19.189.246:9000/user/hive/warehouse'
);

CREATE TABLE IF NOT EXISTS  `hive_catalog`.`ispong_db`.`iceberg_table2` (
    id BIGINT COMMENT 'unique id',
    data STRING
);

-- 删除。更新 需要走批处理模式
INSERT INTO `hive_catalog`.`ispong_db`.`iceberg_table2` VALUES (1, 'a');


INSERT INTO `hive_catalog`.`ispong_db`.`iceberg_table2` VALUES (2, 'b');
```
