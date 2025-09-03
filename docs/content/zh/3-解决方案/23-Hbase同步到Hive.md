---
title: "Hbase同步到Hive"
---

## Hbase同步到Hive

#### 案例

> 将hbase2.1.0版本的数据库同步到hive3.1.3数据库中

#### 前提

> 原生flink，仅支持hive2.3.0版本以上

将zhiliuyun-agent的lib目录下的hadoop-hdfs-client-3.3.5.jar包换成当前依赖的hadoop版本      
例如： 当前用户hadoop版本为3.0.0，则需要删除默认的3.3.5包，下载3.0.0的依赖包  

```bash
rm ~/zhiliuyun-agent/lib/hadoop-hdfs-client-3.3.5.jar   
cd ~/zhiliuyun-agent/lib/  
wget https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hadoop-hdfs-client-3.0.0.jar
```

#### 解决方案

> 创建FlinkSql作业类型，添加以下依赖  
> 下载对应的hbase、hive依赖版本和hadoop对应的版本  

- [flink-connector-hbase-2.2-4.0.0-1.18.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/flink-connector-hbase-2.2-4.0.0-1.18.jar)
- [flink-connector-hbase-base-4.0.0-1.18.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/flink-connector-hbase-base-4.0.0-1.18.jar)
- [hbase-client-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-client-2.1.0.jar)
- [hbase-common-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-common-2.1.0.jar)
- [hbase-protocol-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-protocol-2.1.0.jar)
- [hbase-protocol-shaded-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-protocol-shaded-2.1.0.jar)
- [hbase-shaded-miscellaneous-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-shaded-miscellaneous-2.1.0.jar)
- [hbase-shaded-netty-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-shaded-netty-2.1.0.jar)
- [hbase-shaded-protobuf-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-shaded-protobuf-2.1.0.jar)
- [flink-connector-hive_2.12-1.18.1.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/flink-connector-hive_2.12-1.18.1.jar)
- [hadoop-common-3.0.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hadoop-common-3.0.0.jar)
- [hadoop-mapreduce-client-core-3.0.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hadoop-mapreduce-client-core-3.0.0.jar)
- [hive-common-3.1.3.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hive-common-3.1.3.jar)
- [hive-exec-3.1.3.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hive-exec-3.1.3.jar)
- [hive-metastore-3.1.3.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hive-metastore-3.1.3.jar)
- [libfb303-0.9.3.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/libfb303-0.9.3.jar)

![20250325145253](https://img.isxcode.com/picgo/20250325145253.png)

#### 示例sql如下

```sql
CREATE CATALOG my_hive_catalog WITH (
  'type' = 'hive',
  'hive-version' = '3.1.3', 
  'default-database' = 'ispong_db2',
  'hive-conf-dir' = '/data/cloudera/parcels/CDH/lib/hive/conf'
);

USE CATALOG my_hive_catalog;
DROP table hTable;

CREATE TABLE hTable (
   rowkey string,
   cf1 ROW<username STRING, age STRING>
) WITH (
  'connector' = 'hbase-2.2',
  'table-name' = 'ispong_namespace:users',
  'zookeeper.quorum' = '127.0.0.1:2181'
);

INSERT INTO users (username)
SELECT cf1.username FROM hTable;
```
