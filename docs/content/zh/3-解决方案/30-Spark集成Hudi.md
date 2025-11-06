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
```

#### 解决方案

> 创建`SparkSql查询作业`，下载依赖

- [hudi-spark3.4-bundle_2.12-1.0.2.jar下载](https://repo1.maven.org/maven2/org/apache/hudi/hudi-spark3.4-bundle_2.12/1.0.2/hudi-spark3.4-bundle_2.12-1.0.2.jar)

![20250120182130](https://img.isxcode.com/picgo/20250120182130.png)

#### 配置SparkConfig

```json
{
  "hive.metastore.uris": "thrift://172.19.189.247:9083",
  "spark.cores.max": "1",
  "spark.driver.cores": "1",
  "spark.driver.extraJavaOptions": "-Dfile.encoding=utf-8",
  "spark.driver.memory": "1g",
  "spark.executor.cores": "1",
  "spark.executor.extraJavaOptions": "-Dfile.encoding=utf-8",
  "spark.executor.instances": "1",
  "spark.executor.memory": "2g",
  "spark.hadoop.hive.metastore.uris": "thrift://172.19.189.247:9083",
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
CREATE TABLE hudi_table (
    ts BIGINT,
    uuid STRING,
    rider STRING,
    driver STRING,
    fare DOUBLE,
    city STRING
) USING HUDI
PARTITIONED BY (city);

-- 增
INSERT INTO ispong_db.hudi_table
VALUES
(1695159649087,'334e26e9-8355-45cc-97c6-c31daf0df330','rider-A','driver-K',19.10,'san_francisco'),
(1695091554788,'e96c4396-3fad-413a-a942-4cb36106d721','rider-C','driver-M',27.70 ,'san_francisco'),
(1695046462179,'9909a8b1-2d15-4d3d-8ec9-efc48c536a00','rider-D','driver-L',33.90 ,'san_francisco'),
(1695332066204,'1dced545-862b-4ceb-8b43-d2a568f6616b','rider-E','driver-O',93.50,'san_francisco'),
(1695516137016,'e3cf430c-889d-4015-bc98-59bdce1e530c','rider-F','driver-P',34.15,'sao_paulo'    ),
(1695376420876,'7a84095f-737f-40bc-b62f-6b69664712d2','rider-G','driver-Q',43.40 ,'sao_paulo'    ),
(1695173887231,'3eeb61f7-c2b0-4636-99bd-5d7a5a1d2c04','rider-I','driver-S',41.06 ,'chennai'      ),
(1695115999911,'c8abbe79-8d89-47ea-b4ce-4d224bae5bfa','rider-J','driver-T',17.85,'chennai');

-- 查
SELECT ts, fare, rider, driver, city FROM  ispong_db.hudi_table WHERE fare > 20.0;
```
