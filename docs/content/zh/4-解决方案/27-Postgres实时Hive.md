---
title: "Postgres实时Hive"
---

#### 案例

> 基于Paimon将Postgres实时到Hive，支持数据实时增删改查。

#### 前提

1. 参考Postgresql实时同步文档，下载依赖并上传。

#### 注意

> hive client访问paimon数据需要下载paimon-hive-connector包，不需要重启hive服务，重启hive会话即可。

- [paimon-hive-connector-3.1-0.9.0.jar下载](https://repo1.maven.org/maven2/org/apache/paimon/paimon-hive-connector-3.1/0.9.0/paimon-hive-connector-3.1-0.9.0.jar)

```bash
mv paimon-hive-connector-3.1-0.9.0.jar /opt/hive/lib/
```

#### 方案

> 创建`FlinkSql作业`类型，下载Postgresql相关额外依赖包和以下依赖

- [paimon-flink-1.18-1.4-20251105.003055-51.jar下载](https://repository.apache.org/content/groups/snapshots/org/apache/paimon/paimon-flink-1.18/1.4-SNAPSHOT/paimon-flink-1.18-1.4-20251108.002925-53.jar)
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

#### postgresql实时同步数据到hive

> 注意：一定要开启checkpoint，否则可能无法写入数据
> 需要修改flinkConfig高级配置如下

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
-- 创建 Hive Catalog
CREATE CATALOG hive_catalog WITH (
    'type' = 'paimon',
    'warehouse' = 'hdfs://106.15.56.126:9000/user/hive/warehouse',
    'metastore' = 'hive',
    'uri' = 'thrift://106.15.56.126:9083',
    'hive-conf-dir' = '/opt/hive/conf'
);

-- 选择 Hive Catalog
USE CATALOG hive_catalog;

-- 选择数据库
USE ispong_db;

-- 创建 PostgreSQL CDC 源表
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

-- 创建 Paimon 表
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

-- 执行数据同步
INSERT INTO users_paimon 
SELECT username, age FROM postgres_source
```