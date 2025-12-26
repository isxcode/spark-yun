---
title: "Doris集成SmartBI"
---

#### 案例

> 将sqlserver中的数据实时同步到doris数据库，并开放成Api接口集成SmartBi

#### Docker安装Doris

> 初始化表

```sql
create database ispong_db;
CREATE TABLE ispong_db.users
(
  username  VARCHAR (32) ,
  age       INT
)
UNIQUE KEY (username)
DISTRIBUTED BY HASH(username) BUCKETS 10
PROPERTIES("replication_num" = "1");
insert into ispong_db.users values ("zhangsan",13);
select * from ispong_db.users;
```

#### Docker安装SqlServer

> SqlServer表需要有主键

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

#### 开启sqlserver cdc

```sql
-- 启用数据库的CDC功能
EXEC sys.sp_cdc_enable_db;

-- 查看数据库CDC是否已经启用，ispong_db库
SELECT name, is_cdc_enabled
FROM sys.databases
WHERE name = 'ispong_db';

-- 启用表的CDC功能
EXEC sys.sp_cdc_enable_table
     @source_schema = N'dbo',           
     @source_name = N'users_cdc2',        
     @role_name = NULL;                   

-- 查看表CDC是否已经启用
SELECT * FROM cdc.change_tables;
```

#### 上传依赖

![20251226180914](https://img.isxcode.com/picgo/20251226180914.png)

- [flink-doris-connector-1.18-25.0.0.jar下载](https://zhiqingyun-demo.isxcode.com/tools/open/file/flink-doris-connector-1.18-25.0.0.jar)
- [flink-sql-connector-sqlserver-cdc-3.2.1.jar下载](https://repo1.maven.org/maven2/org/apache/flink/flink-sql-connector-sqlserver-cdc/3.2.1/flink-sql-connector-sqlserver-cdc-3.2.1.jar)
- [mssql-jdbc-11.2.3.jre8.jar下载](https://repo1.maven.org/maven2/com/microsoft/sqlserver/mssql-jdbc/11.2.3.jre8/mssql-jdbc-11.2.3.jre8.jar)

#### 创建k8s计算引擎

![20251226175830](https://img.isxcode.com/picgo/20251226175830.png)

#### 数据实时同步配置

> 新建flinkSql

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
      'table-name' = 'dbo.users_cdc2',
      'debezium.database.trustServerCertificate' = 'true'
      );

CREATE TABLE print_sink ( 
    print_date STRING 
) WITH ( 
    'connector' = 'print' 
);

INSERT INTO print_sink select username from from_table;
```


-- CREATE TABLE target_table
-- (
--     username STRING,
--     age      INT
-- ) WITH (
--       'connector' = 'doris',
--       'fenodes' = '192.168.35.201:8030',
--       'table.identifier' = 'ispong_db.users',
--       'username' = 'admin',
--       'password' = '',
--       'sink.buffer-flush.max-rows' = '1'
-- );

#### 自定义接口服务配置

> 新建自定义数据

#### 测试sqlserver中插入数据

```sql
-- 插入sqlserver 
insert into users_cdc2 values ('lisi', 123);
select * from users_cdc2;

-- 查询doris
select * from users;
```
