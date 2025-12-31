---
title: "Doris集成SmartBI"
---

### 案例说明

> 将SqlServer数据源中的数据实时同步到Doris数据库中，并开放成Api接口服务集成SmartBi

#### Docker快速安装Doris

- [参考文档:Docker快速安装Doris](https://ispong.isxcode.com/db/doris/doris%20docker%E5%AE%89%E8%A3%85/)

> 创建Doris目标表，可通过创建`JDBC查询作业`完成

```sql
CREATE TABLE users (
  username VARCHAR(255),
  age INT
) UNIQUE KEY(username)
DISTRIBUTED BY HASH(username) BUCKETS 1
PROPERTIES ("replication_allocation" = "tag.location.default: 1");
insert into users values ("zhangsan",13);
select * from users;
```

#### Docker快速安装SqlServer

- [参考文档:Docker快速安装SqlServer](https://ispong.isxcode.com/db/sqlserver/sqlserver%20docker%E5%AE%89%E8%A3%85/)

> 创建SqlServer来源表，可通过创建`JDBC查询作业`完成
> SqlServer来源表必须有主键

```sql
CREATE TABLE users_cdc
(
  username  VARCHAR (32) primary key ,
  age       INT
);
insert into users_cdc values ('zhangsan',13);
select * from users_cdc;
```

#### 开启SqlServer CDC

> 指定users_cdc表开启cdc，可通过创建`JDBC查询作业`完成

```sql
-- 启用数据库的CDC功能
EXEC sys.sp_cdc_enable_db;  

-- 查看数据库CDC是否已经启用，ispong_db库
SELECT name, is_cdc_enabled FROM sys.databases WHERE name = 'ispong_db';

-- 添加cdc监听表
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

#### 上传作业依赖

> 资源中心上传作业运行所需的Jar依赖

![20251230175535](https://img.isxcode.com/picgo/20251230175535.png)

- [flink-doris-connector-1.18-25.0.0.jar下载](https://zhiqingyun-demo.isxcode.com/tools/open/file/flink-doris-connector-1.18-25.0.0.jar)
- [flink-sql-connector-sqlserver-cdc-3.2.1.jar下载](https://repo1.maven.org/maven2/org/apache/flink/flink-sql-connector-sqlserver-cdc/3.2.1/flink-sql-connector-sqlserver-cdc-3.2.1.jar)
- [mssql-jdbc-11.2.3.jre8.jar下载](https://repo1.maven.org/maven2/com/microsoft/sqlserver/mssql-jdbc/11.2.3.jre8/mssql-jdbc-11.2.3.jre8.jar)

#### 创建数据实时同步作业

- [参考文档:Doris官方说明文档](https://doris.apache.org/docs/3.x/ecosystem/flink-doris-connector)

> 新建FlinkSql作业

![20251231135251](https://img.isxcode.com/picgo/20251231135251.png)

> 修改高级配置

![20251231135357](https://img.isxcode.com/picgo/20251231135357.png)

```json
{
  "execution.checkpointing.interval": "10s",
  "jobmanager.memory.process.size": "1g",
  "parallelism.default": "1",
  "taskmanager.memory.process.size": "2g",
  "taskmanager.numberOfTaskSlots": "1"
}
```

> 添加作业依赖，选中三个上传的依赖包

![20251230175625](https://img.isxcode.com/picgo/20251230175625.png)

> 编写FlinkSql
> SqlServer实时插入到Doris

```sql
CREATE TABLE from_table
(
    username STRING,
    age      INT,
    PRIMARY KEY (username) NOT ENFORCED
) WITH (
      'connector' = 'sqlserver-cdc',
      'hostname' = '127.0.0.1',
      'port' = '1433',
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
      'fenodes' = '127.0.0.1:8030',
      'table.identifier' = 'ispong_db.users',
      'username' = 'root',
      'password' = '',
      'sink.properties.format' = 'json',
      'sink.properties.read_json_by_line' = 'true',
      'sink.enable-delete' = 'true',
      'sink.label-prefix' = 'doris_label'
);

INSERT INTO target_table select username, age from from_table;
```

#### 测试数据实时增删改查

> 可通过创建`JDBC查询作业`完成

```sql
-- 插入sqlserver 
insert into users_cdc values ('lisi', 123);
select * from users_cdc;

-- 查询doris，doris中的数据实时变化，支持增删改查
select * from users;
```

#### 添加doris数据源

![20251231140726](https://img.isxcode.com/picgo/20251231140726.png)

#### 创建接口服务

![20251231140856](https://img.isxcode.com/picgo/20251231140856.png)

![20251231141101](https://img.isxcode.com/picgo/20251231141101.png)

> 支持自定义的json请求格式

```json
{
  "username": "${username.string}"
}
```

> 支持自定义的sql处理逻辑

```sql
select username,age from users where username = '${username}'
```

> 支持自定义的json响应格式

```json
{
  "username": "${username.string}",
  "age": "${age.int}"
}
```

#### 测试接口服务

![20251231141155](https://img.isxcode.com/picgo/20251231141155.png)

#### 发布接口接口

![20251231141233](https://img.isxcode.com/picgo/20251231141233.png)

![20251231142215](https://img.isxcode.com/picgo/20251231142215.png)

```wikitext
curl 'http://localhost:8080/sy_2005925214395023360/api/v1/getUser' \
  -H 'Accept: application/json, text/plain, */*' \
  -H 'Content-Type: application/json;charset=UTF-8' \
  --data-raw '{"username":"zhangsan"}'
```

![20251231141441](https://img.isxcode.com/picgo/20251231141441.png)

