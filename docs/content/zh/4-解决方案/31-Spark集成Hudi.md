---
title: "Spark集成Hudi"
---

#### 案例

> 使用spark对Hudi数据进行增删改查

#### 注意

> hive client访问hudi数据需要下载hudi-hadoop-mr-bundle-1.0.2.jar包，不需要重启hive服务，重启hive会话即可。

- [hudi-hadoop-mr-bundle-1.0.2.jar下载](https://repo1.maven.org/maven2/org/apache/hudi/hudi-hadoop-mr-bundle/1.0.2/hudi-hadoop-mr-bundle-1.0.2.jar)

```bash
mv hudi-hadoop-mr-bundle-1.0.2.jar /opt/hive/lib/
mv hudi-hadoop-mr-bundle-1.0.2.jar /opt/hadoop/share/hadoop/yarn/lib/
```

#### 解决方案

> 创建`SparkSql查询作业`，下载依赖

- [hudi-spark3.4-bundle_2.12-1.0.2.jar下载](https://repo1.maven.org/maven2/org/apache/hudi/hudi-spark3.4-bundle_2.12/1.0.2/hudi-spark3.4-bundle_2.12-1.0.2.jar)

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
  "spark.hadoop.hive.metastore.uris": "thrift://172.19.189.246:9083",
  "spark.kryo.registrator": "org.apache.spark.HoodieSparkKryoRegistrar",
  "spark.serializer": "org.apache.spark.serializer.KryoSerializer",
  "spark.sql.autoBroadcastJoinThreshold": "-1",
  "spark.sql.catalog.spark_catalog": "org.apache.spark.sql.hudi.catalog.HoodieCatalog",
  "spark.sql.catalog.spark_catalog.type": "hudi",
  "spark.sql.catalogImplementation": "hive",
  "spark.sql.extensions": "org.apache.spark.sql.hudi.HoodieSparkSessionExtension",
  "spark.sql.hive.convertMetastoreOrc": "false",
  "spark.sql.legacy.timeParserPolicy": "LEGACY",
  "spark.sql.parquet.datetimeRebaseModeInRead": "LEGACY",
  "spark.sql.parquet.datetimeRebaseModeInWrite": "LEGACY",
  "spark.sql.parquet.enableVectorizedReader": "false",
  "spark.sql.parquet.int96RebaseModeInRead": "LEGACY",
  "spark.sql.parquet.int96RebaseModeInWrite": "LEGACY",
  "spark.sql.parquet.writeLegacyFormat": "true",
  "spark.sql.storeAssignmentPolicy": "LEGACY"
}
```

#### sql增删改查数据

```sql
-- 选择catalog
use spark_catalog;

-- 建hudi表
CREATE TABLE IF NOT EXISTS ispong_db.hudi_table (
    username STRING,
    age BIGINT
) USING HUDI;

-- 增
insert into ispong_db.hudi_table values ('zhangsan',13);
insert into ispong_db.hudi_table values ('lisi',14);

-- 删
delete from ispong_db.hudi_table where username = 'lisi';
    
-- 改
update ispong_db.hudi_table set age = 133 where username ='zhangsan'; 

-- 查
select username ,age  from ispong_db.hudi_table;
```
