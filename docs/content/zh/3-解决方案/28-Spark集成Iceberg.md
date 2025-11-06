---
title: "Spark集成Iceberg"
---

#### 案例

> 使用spark对Iceberg数据进行增删改查

#### 方案

> 创建`SparkSql查询作业`，下载依赖

- [iceberg-spark-runtime-3.4_2.13-1.7.0.jar下载](https://repo1.maven.org/maven2/org/apache/iceberg/iceberg-spark-runtime-3.4_2.13/1.7.0/iceberg-spark-runtime-3.4_2.13-1.7.0.jar)

![20250120182130](https://img.isxcode.com/picgo/20250120182130.png)

#### 配置SparkConfig

```json
{
  "spark.cores.max": "1",
  "spark.driver.cores": "1",
  "spark.driver.extraJavaOptions": "-Dfile.encoding=utf-8",
  "spark.driver.memory": "1g",
  "spark.executor.cores": "1",
  "spark.executor.extraJavaOptions": "-Dfile.encoding=utf-8",
  "spark.executor.instances": "1",
  "spark.executor.memory": "2g",
  "spark.sql.autoBroadcastJoinThreshold": "-1",
  "spark.sql.catalog.hive_catalog": "org.apache.iceberg.spark.SparkCatalog",
  "spark.sql.catalog.hive_catalog.clients": "10",
  "spark.sql.catalog.hive_catalog.type": "hive",
  "spark.sql.catalog.hive_catalog.uri": "thrift://172.19.189.247:9083",
  "spark.sql.catalog.hive_catalog.warehouse": "hdfs://172.19.189.247:9000/user/hive/warehouse",
  "spark.sql.catalog.spark_catalog": "org.apache.iceberg.spark.SparkSessionCatalog",
  "spark.sql.extensions": "org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions",
  "spark.sql.legacy.timeParserPolicy": "LEGACY",
  "spark.sql.parquet.datetimeRebaseModeInRead": "LEGACY",
  "spark.sql.parquet.datetimeRebaseModeInWrite": "LEGACY",
  "spark.sql.parquet.enableVectorizedReader": "false",
  "spark.sql.parquet.int96RebaseModeInRead": "LEGACY",
  "spark.sql.parquet.int96RebaseModeInWrite": "LEGACY",
  "spark.sql.parquet.writeLegacyFormat": "true",
  "spark.sql.storeAssignmentPolicy": "STRICT"
}
```

#### sql增删改查数据

```sql
-- 建iceberg表
CREATE TABLE hive_catalog.ispong_db.users2 (
     username string,
     age bigint
) USING iceberg;

-- 增
insert into hive_catalog.ispong_db.users2 values ('zhangsan',13);
insert into hive_catalog.ispong_db.users2 values ('lisi',14);

-- 删
delete from hive_catalog.ispong_db.users2 where username = 'zhangsan';
    
-- 改
update hive_catalog.ispong_db.users2 set username='lisi' where age = 12;

-- 查
select * from hive_catalog.ispong_db.users2;
```