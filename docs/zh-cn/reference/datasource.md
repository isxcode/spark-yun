?> 数据源：负责全局数据源的管理

> 点击添加数据源按钮添加数据源，目前支持Mysql、SqlServer、Oracle数据源。

<img src="https://img.isxcode.com/picgo/20230415165732.png" width="600">

> 点击检测按钮，可以对数据源的信息进行检测。

<img src="https://img.isxcode.com/picgo/20230415165655.png" width="900">

##### 数据源

###### mysql

- com.mysql.cj.jdbc.Driver
- jdbc:mysql://x.x.x.x:x

```sql
show databases;
```

###### doris

- com.mysql.jdbc.Driver
- jdbc:mysql://x.x.x.x:x

```sql
SHOW databases;
```

###### sqlserver

- com.microsoft.sqlserver.jdbc.SQLServerDriver
- jdbc:sqlserver://x.x.x.x:x;trustServerCertificate=true

```sql
SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE'
```

###### oracle

- oracle.jdbc.driver.OracleDriver
- jdbc:oracle:thin:@x.x.x.x:x:XE

```sql
SELECT table_name FROM all_tables;
```

###### hive

- org.apache.hive.jdbc.HiveDriver
- jdbc:hive2://x.x.x.x:x

```sql
show databases;
```

###### postgre

- org.postgresql.Driver
- jdbc:postgresql://x.x.x.x:x/

```sql
SELECT table_name
FROM information_schema.tables
WHERE table_type = 'BASE TABLE'
  AND table_schema = 'public';
```

###### clickhouse

- com.clickhouse.jdbc.ClickHouseDriver
- jdbc:clickhouse://x.x.x.x:x

```sql
SHOW TABLES;
```

###### hana

- com.sap.db.jdbc.Driver
- jdbc:sap://x.x.x.x:x


###### dm

- dm.jdbc.driver.DmDriver
- jdbc:dm://x.x.x.x:x

```sql
select distinct object_name TABLE_SCHEMA from all_objects where object_type = 'SCH';
```

##### 数据同步

- oracle同步到hive

```sql

```