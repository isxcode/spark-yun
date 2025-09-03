---
title: "Postgresql实时同步"
---

## Postgresql实时同步

> 实现在Postgresql中结果表向目标表中实时同步数据

#### 案例

> Postgresql中将cdc_source中的数据实时同步到cdc_target中

#### 前提

- 启动Postgresql的logical

#### 解决方案

> 创建FlinkSql作业类型，添加以下依赖

- [flink-connector-postgres-cdc-3.2.1.jar下载](https://repo1.maven.org/maven2/org/apache/flink/flink-connector-postgres-cdc/3.2.1/flink-connector-postgres-cdc-3.2.1.jar)
- [postgresql-42.7.2.jar下载](https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.2/postgresql-42.7.2.jar)
- [flink-cdc-base-3.2.1.jar下载](https://repo1.maven.org/maven2/org/apache/flink/flink-cdc-base/3.2.1/flink-cdc-base-3.2.1.jar)
- [flink-connector-debezium-3.2.1.jar下载](https://repo1.maven.org/maven2/org/apache/flink/flink-connector-debezium/3.2.1/flink-connector-debezium-3.2.1.jar)
- [debezium-connector-postgres-1.9.8.Final.jar下载](https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/1.9.8.Final/debezium-connector-postgres-1.9.8.Final.jar)
- [debezium-core-1.9.8.Final.jar下载](https://repo1.maven.org/maven2/io/debezium/debezium-core/1.9.8.Final/debezium-core-1.9.8.Final.jar)
- [connect-api-3.2.0.jar下载](https://repo1.maven.org/maven2/org/apache/kafka/connect-api/3.2.0/connect-api-3.2.0.jar)
- [debezium-api-1.9.8.Final.jar下载](https://repo1.maven.org/maven2/io/debezium/debezium-api/1.9.8.Final/debezium-api-1.9.8.Final.jar)
- [jackson-databind-2.13.5.jar下载](https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.13.5/jackson-databind-2.13.5.jar)
- [jackson-core-2.13.5.jar下载](https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.13.5/jackson-core-2.13.5.jar)
- [jackson-annotations-2.13.5.jar下载](https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.13.5/jackson-annotations-2.13.5.jar)
- [kafka-storage-3.2.0.jar下载](https://repo1.maven.org/maven2/org/apache/kafka/kafka-storage/3.2.0/kafka-storage-3.2.0.jar)
- [connect-runtime-3.2.0.jar下载](https://repo1.maven.org/maven2/org/apache/kafka/connect-runtime/3.2.0/connect-runtime-3.2.0.jar)
- [kafka-clients-3.2.0.jar下载](https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/3.2.0/kafka-clients-3.2.0.jar)
- [debezium-embedded-1.9.8.Final.jar下载](https://repo1.maven.org/maven2/io/debezium/debezium-embedded/1.9.8.Final/debezium-embedded-1.9.8.Final.jar)
- [connect-json-3.2.0.jar下载](https://repo1.maven.org/maven2/org/apache/kafka/connect-json/3.2.0/connect-json-3.2.0.jar)
- [HikariCP-4.0.3.jar下载](https://repo1.maven.org/maven2/com/zaxxer/HikariCP/4.0.3/HikariCP-4.0.3.jar)
- [protobuf-java-3.21.9.jar下载](https://repo1.maven.org/maven2/com/google/protobuf/protobuf-java/3.21.9/protobuf-java-3.21.9.jar)

![20250120182130](https://img.isxcode.com/picgo/20250120182130.png)

```sql
CREATE TABLE from_table
(
    username STRING,
    age      INT,
    PRIMARY KEY (username) NOT ENFORCED
) WITH (
      'connector' = 'postgres-cdc',
      'hostname' = 'localhost',
      'port' = '54302',
      'username' = 'root',
      'password' = 'root123',
      'database-name' = 'isxcode_db',
      'schema-name' = 'public',
      'table-name' = 'cdc_source',
      'slot.name' = 'flink',
      'scan.incremental.snapshot.enabled' = 'true'
      );

CREATE TABLE target_table
(
    username STRING,
    age      INT,
    PRIMARY KEY (username) NOT ENFORCED
) WITH (
      'connector' = 'jdbc',
      'url' = 'jdbc:postgresql://localhost:54302/isxcode_db',
      'driver' = 'org.postgresql.Driver',
      'table-name' = 'cdc_sink',
      'username' = 'root',
      'password' = 'root123',
      'sink.buffer-flush.max-rows' = '1');

INSERT INTO target_table
select *
from from_table;
```

| 配置项                        | 说明       |
|----------------------------|----------|
| connector                  | 连接方式     |
| uri                        | 数据库连接url |
| table-name                     | 表名       |
| database-name                 | 库名****       
