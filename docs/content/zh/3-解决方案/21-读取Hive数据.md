---
title: "读取Hive数据"
---

## 读取Hive数据

#### 案例

> 将hive3.1.3版本的数据打印出来

#### 注意

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
> 下载hive对应的依赖版本和hadoop对应的依赖版本  

- [flink-connector-hive_2.12-1.18.1.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/flink-connector-hive_2.12-1.18.1.jar)
- [hadoop-common-3.0.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hadoop-common-3.0.0.jar)
- [hadoop-mapreduce-client-core-3.0.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hadoop-mapreduce-client-core-3.0.0.jar)
- [hive-common-3.1.3.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hive-common-3.1.3.jar)
- [hive-exec-3.1.3.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hive-exec-3.1.3.jar)
- [hive-metastore-3.1.3.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hive-metastore-3.1.3.jar)
- [libfb303-0.9.3.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/libfb303-0.9.3.jar)

![20250325113341](https://img.isxcode.com/picgo/20250325113341.png)

#### 示例sql如下

```sql
CREATE CATALOG my_hive_catalog WITH (
  'type' = 'hive',
  'hive-version' = '3.1.3', 
  'default-database' = 'ispong_db2',
  'hive-conf-dir' = '/data/cloudera/parcels/CDH/lib/hive/conf'
);

USE CATALOG my_hive_catalog;

CREATE TABLE print_sink ( 
    print_date string 
) WITH ( 
    'connector' = 'print' 
);

INSERT INTO print_sink SELECT username from users;
```
