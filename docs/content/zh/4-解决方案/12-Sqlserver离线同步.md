---
title: "Sqlserver离线同步"
---

## Sqlserver离线同步

> 实现在sqlserver(2017)中结果表向目标表中离线同步数据

#### 案例

> sqlserver中将t_source中的数据离线同步到t_target中

#### 解决方案

> 创建FlinkSql作业类型，添加以下依赖

- [mssql-jdbc-11.2.3.jre8.jar下载](https://repo1.maven.org/maven2/com/microsoft/sqlserver/mssql-jdbc/11.2.3.jre8/mssql-jdbc-11.2.3.jre8.jar)

![20250120154153](https://img.isxcode.com/picgo/20250120154153.png)

```sql
CREATE TABLE from_table
(
    username STRING,
    age      INT
) WITH (
      'connector' = 'jdbc',
      'url' = 'jdbc:sqlserver://127.0.0.1:3030;database=ispong_db',
      'driver' = 'com.microsoft.sqlserver.jdbc.SQLServerDriver',
      'table-name' = 't_source',
      'username' = 'SA',
      'password' = 'ispong123',
      'scan.fetch-size' = '1'
      );

CREATE TABLE target_table
(
    username STRING,
    age      INT
) WITH (
      'connector' = 'jdbc',
      'url' = 'jdbc:sqlserver://127.0.0.1:3030;database=ispong_db',
      'driver' = 'com.microsoft.sqlserver.jdbc.SQLServerDriver',
      'table-name' = 't_sink',
      'username' = 'SA',
      'password' = 'ispong123',
      'sink.buffer-flush.max-rows' = '1');

INSERT INTO target_table
select *
from from_table;
```

| 配置项                        | 说明            |
|----------------------------|---------------|
| connector                  | 连接方式          |
| url                        | 数据库连接url      |
| driver                     | 数据源驱动         |
| table-name                 | 表名            |
| username                   | 数据源账号         |
| password                   | 账号密码          |
| scan.fetch-size            | 每次操作多少数据      |
| sink.buffer-flush.max-rows | 多少条数据合并在一个事务里 |