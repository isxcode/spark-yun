?> 数据源：负责租户数据源的管理

> 点击添加数据源按钮添加数据源。

<img src="https://img.isxcode.com/picgo/20230527162046.png" width="600">

> 点击检测按钮，可以对数据源的信息进行检测。

<img src="https://img.isxcode.com/picgo/20230527162309.png" width="900">

#### 数据源说明

<br/>

##### Mysql

- com.mysql.cj.jdbc.Driver
- jdbc:mysql://x.x.x.x:x

```sql
show databases;
```

##### Doris

- com.mysql.jdbc.Driver
- jdbc:mysql://x.x.x.x:x

```sql
SHOW databases;
```

##### SqlServer

- com.microsoft.sqlserver.jdbc.SQLServerDriver
- jdbc:sqlserver://x.x.x.x:x;trustServerCertificate=true

```sql
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE'
```

##### Oracle

- oracle.jdbc.driver.OracleDriver
- jdbc:oracle:thin:@x.x.x.x:0/x

```sql
SELECT table_name FROM all_tables;
```

##### Hive

- org.apache.hive.jdbc.HiveDriver
- jdbc:hive2://x.x.x.x:x

```sql
show databases;
```

##### Postgre

- org.postgresql.Driver
- jdbc:postgresql://x.x.x.x:x/

```sql
SELECT table_name FROM information_schema.tables WHERE table_type = 'BASE TABLE' AND table_schema = 'public';
```

##### Clickhouse

- com.clickhouse.jdbc.ClickHouseDriver
- jdbc:clickhouse://x.x.x.x:x

```sql
SHOW TABLES;
```

##### Hana

- com.sap.db.jdbc.Driver
- jdbc:sap://x.x.x.x:x

##### 达梦

- dm.jdbc.driver.DmDriver
- jdbc:dm://x.x.x.x:x

```sql
select distinct object_name TABLE_SCHEMA from all_objects where object_type = 'SCH';
```

##### OceanBase

- jdbc:oceanbase://x.x.x.x:x
- com.oceanbase.jdbc.Driver

```sql
show databases;
```

##### Tidb

- com.mysql.cj.jdbc.Driver
- jdbc:mysql://x.x.x.x:x

```sql
show databases;
```

##### StarRocks

- com.mysql.jdbc.Driver
- jdbc:mysql://x.x.x.x:x

```sql
show databases;
```
