---
title: "Flink集成Paimon"
---

#### 案例

> 使用flink对Paimon数据进行增删改查

#### 解决方案

> 创建`FlinkSql查询作业`，下载依赖

- [paimon-flink-1.18-1.4-20251105.003055-51.jar下载](https://repository.apache.org/content/groups/snapshots/org/apache/paimon/paimon-flink-1.18/1.4-SNAPSHOT/paimon-flink-1.18-1.4-20251105.003055-51.jar)
- [hive-standalone-metastore-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-standalone-metastore/3.1.3/hive-standalone-metastore-3.1.3.jar)
- [hive-common-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-common/3.1.3/hive-common-3.1.3.jar)
- [hadoop-mapreduce-client-core-3.3.5.jar下载](https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-mapreduce-client-core/3.3.5/hadoop-mapreduce-client-core-3.3.5.jar)
- [commons-lang-2.6.jar下载](https://repo1.maven.org/maven2/commons-lang/commons-lang/2.6/commons-lang-2.6.jar)
- [hive-storage-api-4.0.0.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-storage-api/4.0.0/hive-storage-api-4.0.0.jar)
- [libthrift-0.9.3.jar下载](https://repo1.maven.org/maven2/org/apache/thrift/libthrift/0.9.3/libthrift-0.9.3.jar)
- [libfb303-0.9.3.jar下载](https://repo1.maven.org/maven2/org/apache/thrift/libfb303/0.9.3/libfb303-0.9.3.jar)
- [hive-exec-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-exec/3.1.3/hive-exec-3.1.3.jar)

![20250120182130](https://img.isxcode.com/picgo/20250120182130.png)

#### FlinkSql

```sql
-- 创建Catalog
CREATE CATALOG hive_catalog WITH (
    'type' = 'paimon',
    'warehouse' = 'hdfs://106.15.56.126:9000/user/hive/warehouse',
    'metastore' = 'hive',
    'uri' = 'thrift://106.15.56.126:9083',
    'hive-conf-dir' = '/opt/hive/conf' 
);

-- 选择Catalog
USE CATALOG hive_catalog;

-- 选择数据源
USE ispong_db;

-- 创建Paimon表
CREATE TABLE IF NOT EXISTS users_paimon (
    username STRING,
    age INT,
    PRIMARY KEY (username) NOT ENFORCED
) WITH (
    'bucket' = '2',
    'bucket-key' = 'username',
    'changelog-producer' = 'lookup'
);

-- 插入数据
INSERT INTO users_paimon VALUES 
('user1', 25),
('user2', 30);
```
