---
title: "Doris集成SmartBI"
---

### 案例

> 将SqlServer中的数据实时同步到Doris数据库，并开放成Api接口集成SmartBi

#### Docker快速安装Doris

- [参考文档](https://ispong.isxcode.com/db/doris/doris%20docker%E5%AE%89%E8%A3%85/)

> 创建Doris目标表

```sql
create database ispong_db;
CREATE TABLE ispong_db.users (
  username VARCHAR(255),
  age INT
) UNIQUE KEY(username)
DISTRIBUTED BY HASH(username) BUCKETS 1
PROPERTIES ("replication_allocation" = "tag.location.default: 1");
insert into ispong_db.users values ("zhangsan",13);
select * from ispong_db.users;
```

#### Docker快速安装SqlServer

- [参考文档](https://ispong.isxcode.com/db/sqlserver/sqlserver%20docker%E5%AE%89%E8%A3%85/)

> 创建SqlServer来源表
> SqlServer来源表必须有主键

```sql
create database ispong_db;
CREATE TABLE users_cdc
(
  username  VARCHAR (32) primary key ,
  age       INT
);
insert into users_cdc values ('zhangsan',13);
select * from users_cdc;
```

#### 开启SqlServer CDC

```sql
-- 启用数据库的CDC功能
USE ispong_db;
EXEC sys.sp_cdc_enable_db;  

-- 查看数据库CDC是否已经启用，ispong_db库
SELECT name, is_cdc_enabled
FROM sys.databases
WHERE name = 'ispong_db';

-- 添加cdc监听表
USE ispong_db;
EXEC sys.sp_cdc_enable_table
     @source_schema = N'dbo',
     @source_name = N'users_cdc',
     @role_name = NULL,
     @supports_net_changes = 1;

-- 删除cdc监听表
EXEC sys.sp_cdc_disable_table
    @source_schema    = N'dbo',
    @source_name      = N'users_cdc',
    @capture_instance = 'all';

-- 查询表是否启动CDC功能
SELECT * FROM cdc.change_tables;
```

#### 上传依赖

![20251230175535](https://img.isxcode.com/picgo/20251230175535.png)

![20251230175625](https://img.isxcode.com/picgo/20251230175625.png)

- [flink-doris-connector-1.18-25.0.0.jar下载](https://zhiqingyun-demo.isxcode.com/tools/open/file/flink-doris-connector-1.18-25.0.0.jar)
- [flink-sql-connector-sqlserver-cdc-3.2.1.jar下载](https://repo1.maven.org/maven2/org/apache/flink/flink-sql-connector-sqlserver-cdc/3.2.1/flink-sql-connector-sqlserver-cdc-3.2.1.jar)
- [mssql-jdbc-11.2.3.jre8.jar下载](https://repo1.maven.org/maven2/com/microsoft/sqlserver/mssql-jdbc/11.2.3.jre8/mssql-jdbc-11.2.3.jre8.jar)

#### 数据实时同步配置

- [参考文档](https://doris.apache.org/docs/3.x/ecosystem/flink-doris-connector)

> 测试实时采集数据

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
      'username' = 'SA',
      'password' = 'ispong123',
      'database-name' = 'ispong_db',
      'table-name' = 'dbo.users_cdc',
      'debezium.database.trustServerCertificate' = 'true'
      );

CREATE TABLE target_table
(
    username STRING,
    age      INT
) WITH (
      'connector' = 'print'
);

INSERT INTO target_table select username, age from from_table;
```

> 测试数据插入数据

```sql
CREATE TABLE target_table
(
    username STRING,
    age      INT
) WITH (
      'connector' = 'doris',
      'fenodes' = '47.92.168.116:30129',
      'table.identifier' = 'ispong_db.users',
      'username' = 'root',
      'password' = ''
);

INSERT INTO target_table select 'lisi', 14;
```

> SqlServer实时插入到Doris

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
      'username' = 'SA',
      'password' = 'ispong123',
      'database-name' = 'ispong_db',
      'table-name' = 'dbo.users_cdc',
      'debezium.database.trustServerCertificate' = 'true'
      );

CREATE TABLE target_table
(
    username STRING,
    age      INT
) WITH (
      'connector' = 'doris',
      'fenodes' = '47.92.168.116:30129',
      'table.identifier' = 'ispong_db.users',
      'username' = 'root',
      'password' = ''
);

INSERT INTO target_table select username, age from from_table;
```

#### 测试sqlserver中插入数据

```sql
-- 插入sqlserver 
insert into users_cdc values ('lisi', 123);
select * from users_cdc;

-- 查询doris
select * from ispong_db.users;
```

#### 添加doris数据源

#### 创建数据接口

#### 测试数据接口



