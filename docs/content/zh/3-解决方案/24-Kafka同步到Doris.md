---
title: "Kafka同步到Doris"
---

## Kafka同步到Doris

#### 案例

> 将kafka(3.1.2)同步到doris(2.0.13)数据库中

#### 解决方案

- [kafka-clients-3.1.2.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/kafka-clients-3.1.2.jar)
- [flink-doris-connector-1.18-25.0.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/flink-doris-connector-1.18-25.0.0.jar)
- [flink-json-1.18.1.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/flink-json-1.18.1.jar)
- [flink-connector-kafka-3.2.0-1.18.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/flink-connector-kafka-3.2.0-1.18.jar)

![20250415180208](https://img.isxcode.com/picgo/20250415180208.png)

#### 示例sql如下

```sql
CREATE TABLE from_table
(
  username string,
  age int 
) WITH (
      'connector' = 'kafka',
      'topic' = 'ispong-topic',
      'properties.bootstrap.servers' = '127.0.0.1:9092',
      'scan.startup.mode' = 'latest-offset',
      'format' = 'json',          
      'json.fail-on-missing-field' = 'false',
      'json.ignore-parse-errors' = 'true'
);

CREATE TABLE target_table
(
    username string,
    age      int
) WITH (
      'connector' = 'doris',
      'fenodes' = '127.0.0.1:9083',
      'table.identifier' = 'ispong_db.users',
      'username' = 'admin',
      'password' = '',
      'sink.enable.batch-mode' = 'true',
      'sink.buffer-flush.max-bytes' = '50MB'
  );

INSERT INTO target_table (username, age)
select username, age
from from_table;
```

- 官方文档： https://doris.apache.org/docs/ecosystem/flink-doris-connector