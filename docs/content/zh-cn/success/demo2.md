#### 纯原生Spark

> 支持一切spark的云原生操作，可以配置官方的所有参数。<br/>
> 配置`hive.metastore.uris`后，可以直接访问hive中的数据。

- 开放式配置参数

```json
{
  "hive.metastore.uris": "",
  "spark.executor.memory":"1g",
  "spark.driver.memory":"1g",
  "spark.sql.storeAssignmentPolicy":"LEGACY",
  "spark.sql.legacy.timeParserPolicy":"LEGACY",
  "spark.hadoop.hive.exec.dynamic.partition":"true",
  "spark.hadoop.hive.exec.dynamic.partition.mode":"nonstrict"
}
```

![img](https://img.isxcode.com/picgo/20230527154421.png)