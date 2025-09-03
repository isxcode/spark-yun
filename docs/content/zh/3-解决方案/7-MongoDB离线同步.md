---
title: "MongoDB离线同步"
---

## MongoDB离线同步

> 实现在mongoDB中结果表向目标表中离线同步数据

#### 案例

> mongoDB中将t_source中的数据离线同步到t_target中

#### 解决方案

> 创建FlinkSql作业类型，添加以下依赖

- [mongodb-driver-core-5.2.1.jar下载](https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-core/5.2.1/mongodb-driver-core-5.2.1.jar)
- [flink-connector-mongodb-1.2.0-1.18.jar下载](https://repo1.maven.org/maven2/org/apache/flink/flink-connector-mongodb/1.2.0-1.18/flink-connector-mongodb-1.2.0-1.18.jar)
- [mongodb-driver-sync-5.2.1.jar下载](https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/5.2.1/mongodb-driver-sync-5.2.1.jar)

![20250109182905](https://img.isxcode.com/picgo/20250109182905.png)

```sql
CREATE TABLE from_table(
    _id STRING,
    name STRING,
    age INT,
    city STRING,
    PRIMARY KEY (_id) NOT ENFORCED
) WITH (
    'connector'='mongodb',
    'uri'='mongodb://root:root123@localhost:27017',
    'collection'='t_source',
    'database'='isxcode_db'
);

CREATE TABLE target_table(
    _id STRING,
    name STRING,
    age INT,
    city STRING,
    PRIMARY KEY (_id) NOT ENFORCED
) WITH (
    'connector'='mongodb',
    'uri'='mongodb://root:root123@localhost:27017',
    'collection'='t_sink',
    'database'='isxcode_db'
); 

INSERT INTO target_table select * from from_table;
```

| 配置项                        | 说明       |
|----------------------------|----------|
| connector                  | 连接方式     |
| uri                        | 数据库连接url |
| collection                     | 表名       |
| database                 | 库名       
