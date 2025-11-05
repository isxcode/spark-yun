---
title: "Postgresql实时同步到Paimon"
---

#### 案例

> 实现将Postgresql中数据实时同步到hive中

#### 前提

参考Postgresql实时同步文档

> hive访问paimon数据需要下载链接包，不需要重启hive服务

- [paimon-hive-connector-3.1-0.9.0.jar下载](https://repo1.maven.org/maven2/org/apache/paimon/paimon-hive-connector-3.1/0.9.0/paimon-hive-connector-3.1-0.9.0.jar)

```bash
mv paimon-hive-connector-3.1-0.9.0.jar /opt/hive/lib/
```

#### 解决方案

> 创建FlinkSql作业类型，postgresql包额外还要添加以下依赖

- [hive-standalone-metastore-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-standalone-metastore/3.1.3/hive-standalone-metastore-3.1.3.jar)
- [hive-common-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-common/3.1.3/hive-common-3.1.3.jar)
- [hadoop-mapreduce-client-core-3.3.5.jar下载](https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-mapreduce-client-core/3.3.5/hadoop-mapreduce-client-core-3.3.5.jar)
- [commons-lang-2.6.jar下载](https://repo1.maven.org/maven2/commons-lang/commons-lang/2.6/commons-lang-2.6.jar)
- [hive-storage-api-4.0.0.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-storage-api/4.0.0/hive-storage-api-4.0.0.jar)
- [libthrift-0.9.3.jar下载](https://repo1.maven.org/maven2/org/apache/thrift/libthrift/0.9.3/libthrift-0.9.3.jar)
- [libfb303-0.9.3.jar下载](https://repo1.maven.org/maven2/org/apache/thrift/libfb303/0.9.3/libfb303-0.9.3.jar)
- [hive-exec-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-exec/3.1.3/hive-exec-3.1.3.jar)

![20250120182130](https://img.isxcode.com/picgo/20250120182130.png)

#### 手动插入数据

```sql
-- 重新创建 Catalog 确保配置正确
CREATE CATALOG hive_catalog WITH (
    'type' = 'paimon',
    'warehouse' = 'hdfs://106.15.56.126:9000/user/hive/warehouse',
    'metastore' = 'hive',
    'uri' = 'thrift://106.15.56.126:9083',
    'hive-conf-dir' = '/opt/hive/conf' 
);

USE CATALOG hive_catalog;
USE ispong_db;

-- 重新创建 Paimon 表
CREATE TABLE IF NOT EXISTS users_paimon (
    username STRING,
    age INT,
    PRIMARY KEY (username) NOT ENFORCED
) WITH (
    'bucket' = '2',
    'bucket-key' = 'username',
    'changelog-producer' = 'lookup'
);

INSERT INTO users_paimon VALUES 
('user1', 25),
('user2', 30);
```

#### postgresql实时同步

> 注意，需要修改flinkConfig高级配置，一定要开启checkpointing

```json
{
  "execution.checkpointing.interval": "10s",
  "execution.runtime-mode": "streaming",
  "jobmanager.memory.process.size": "1g",
  "parallelism.default": "1",
  "pipeline.name": "Postgres CDC to Paimon",
  "taskmanager.memory.process.size": "2g",
  "taskmanager.numberOfTaskSlots": "1"
}
```

```sql
-- 1. 创建 Hive Catalog
CREATE CATALOG hive_catalog WITH (
    'type' = 'paimon',
    'warehouse' = 'hdfs://106.15.56.126:9000/user/hive/warehouse',
    'metastore' = 'hive',
    'uri' = 'thrift://106.15.56.126:9083',
    'hive-conf-dir' = '/opt/hive/conf'
);

USE CATALOG hive_catalog;

-- 2. 选择数据库
USE ispong_db;

-- 3. 创建 PostgreSQL CDC 源表
CREATE TEMPORARY TABLE postgres_source (
    username STRING,
    age INT,
    PRIMARY KEY (username) NOT ENFORCED
) WITH (
    'connector' = 'postgres-cdc',
    'hostname' = '106.15.56.126',
    'port' = '54302',
    'username' = 'root',
    'password' = 'root123',
    'database-name' = 'ispong_db',
    'schema-name' = 'public',
    'table-name' = 'users',
    'slot.name' = 'flink',
    'scan.incremental.snapshot.enabled' = 'true'
);


-- 4. 创建 Paimon 表（自动注册到 Hive）
CREATE TABLE IF NOT EXISTS users_paimon (
    username STRING,
    age INT,
    PRIMARY KEY (username) NOT ENFORCED
) WITH (
    'bucket' = '2',
    'bucket-key' = 'username',
    'changelog-producer' = 'input',
    'changelog-producer.compaction-interval' = '10s'
);


-- 5. 执行数据同步
INSERT INTO users_paimon 
SELECT username, age FROM postgres_source
```