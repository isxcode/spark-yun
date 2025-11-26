---
title: "Sqlserver实时同步"
---

## Sqlserver实时同步

> 实现在Sqlserver中结果表向目标表中实时同步数据

#### 案例

> Sqlserver中将cdc_source中的数据实时同步到cdc_target中

#### 前提

- 启动Sqlserver的表cdc

#### 解决方案

> 创建FlinkSql作业类型，添加以下依赖

- [flink-sql-connector-sqlserver-cdc-3.2.1.jar下载](https://repo1.maven.org/maven2/org/apache/flink/flink-sql-connector-sqlserver-cdc/3.2.1/flink-sql-connector-sqlserver-cdc-3.2.1.jar)
- [mssql-jdbc-11.2.3.jre8.jar下载](https://repo1.maven.org/maven2/com/microsoft/sqlserver/mssql-jdbc/11.2.3.jre8/mssql-jdbc-11.2.3.jre8.jar)

![20250120162447](https://img.isxcode.com/picgo/20250120162447.png)

```sql
CREATE TABLE from_table
(
    username STRING,
    age      INT,
    PRIMARY KEY (username) NOT ENFORCED
) WITH (
      'connector' = 'sqlserver-cdc',
      'hostname' = '47.92.168.116',
      'port' = '30103',
      'username' = 'sa',
      'password' = 'ispong123',
      'database-name' = 'ispong_db',
      'table-name' = 'dbo.cdc_source2',
      'debezium.database.trustServerCertificate' = 'true'
      );

CREATE TABLE target_table
(
    username STRING,
    age      INT,
    PRIMARY KEY (username) NOT ENFORCED
) WITH (
      'connector' = 'jdbc',
      'url' = 'jdbc:sqlserver://47.92.168.116:30103;database=ispong_db;trustServerCertificate=true',
      'driver' = 'com.microsoft.sqlserver.jdbc.SQLServerDriver',
      'table-name' = 'cdc_sink',
      'username' = 'SA',
      'password' = 'ispong123',
      'sink.buffer-flush.max-rows' = '1'
      );

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
