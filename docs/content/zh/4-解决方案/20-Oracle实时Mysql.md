---
title: "Oracle实时Mysql"
---

## Oracle实时Mysql

> 实现在Oracle中结果表向目标表中实时同步到Mysql数据

#### 案例

> Oracle中将cdc_source中的数据实时同步到cdc_target中

#### 前提

- 启用Oracle的logminer
- 赋权

> 登录sysdba

```sql
GRANT SELECT ON SYS.V_$DATABASE TO ispong;
GRANT create session, alter session, execute_catalog_role, select any dictionary, select any transaction, select any table, create any table, create any index, unlimited tablespace to ispong;
SELECT supplemental_log_data_min, supplemental_log_data_pk, supplemental_log_data_all
FROM v$database;
```

#### 解决方案

> 创建FlinkSql作业类型，添加以下依赖

- [flink-sql-connector-oracle-cdc-3.2.1.jar下载](https://repo1.maven.org/maven2/org/apache/flink/flink-sql-connector-oracle-cdc/3.2.1/flink-sql-connector-oracle-cdc-3.2.1.jar)

![20250113160944](https://img.isxcode.com/picgo/20250113160944.png)

```sql
CREATE TABLE from_table
(
    USERNAME STRING,
    AGE      INT,
    PRIMARY KEY (USERNAME) NOT ENFORCED
) WITH ( 'connector' = 'oracle-cdc',
      'hostname' = 'localhost',
      'port' = '15201',
      'username' = 'ispong',
      'password' = 'ispong123',
      'database-name' = 'helowin',
      'schema-name' = 'ispong',
      'table-name' = 'CDC_SOURCE',
      'scan.startup.mode' = 'latest-offset'
      );

CREATE TABLE target_table
(
    username STRING,
    age      INT,
    PRIMARY KEY (username) NOT ENFORCED
) WITH ( 'connector' = 'jdbc',
      'url' = 'jdbc:mysql://localhost:30306/isxcode_db?useSSL=false&allowPublicKeyRetrieval=true',
      'driver' = 'com.mysql.cj.jdbc.Driver',
      'table-name' = 'cdc_source',
      'username' = 'ispong',
      'password' = 'ispong123');

INSERT INTO target_table (username, age)
select USERNAME, AGE
from from_table;
```

| 配置项           | 说明         |
|---------------|------------|
| connector     | 连接方式       |
| uri           | 数据库连接url   |
| table-name    | 表名         |
| database-name | oracle的sid |
| schema-name   | 用户名        |
