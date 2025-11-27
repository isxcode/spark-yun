---
title: "Mysql实时同步"
---

## 实时同步

> 实现Mysql中结果表向目标表中实时同步数据

#### 案例

> Mysql中将cdc_source中的数据实时同步到cdc_target中

#### 前提

- 需要开启binlog服务
- 用户授权

```sql
GRANT SELECT, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'ispong'@'%';  
GRANT RELOAD ON *.* TO 'ispong'@'%';  
FLUSH PRIVILEGES;  
```

#### 解决方案

> 创建FlinkSql作业类型，添加flink-sql-connector-mysql-cdc依赖

- [flink-sql-connector-mysql-cdc-3.2.1.jar下载](https://repo1.maven.org/maven2/org/apache/flink/flink-sql-connector-mysql-cdc/3.2.1/flink-sql-connector-mysql-cdc-3.2.1.jar)

![20241230183511](https://img.isxcode.com/picgo/20241230183511.png)

```sql
CREATE TABLE from_table
(
    username STRING,
    age      INT
) WITH ( 'connector' = 'mysql-cdc',
      'hostname' = 'localhost',
      'port' = '30306',
      'username' = 'ispong',
      'password' = 'ispong123',
      'database-name' = 'isxcode_db',
      'table-name' = 'cdc_source',
      'scan.incremental.snapshot.enabled' = 'false',
      'server-time-zone' = 'UTC',
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
      'table-name' = 'cdc_target',
      'username' = 'ispong',
      'password' = 'ispong123');

INSERT INTO target_table
select *
from from_table;
```

| 配置项               | 说明                 |
|-------------------|--------------------|
| connector         | 连接方式               |
| url               | 数据库连接url           |
| driver            | 数据源驱动              |
| table-name        | 表名                 |
| username          | 数据源账号              |
| password          | 账号密码               |
| server-time-zone  | 服务器的时区，两个表时区不一致会报错 |
| scan.startup.mode | 从什么地方开始同步          |
