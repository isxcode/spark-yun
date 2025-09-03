---
title: "读取Hbase数据"
---

## 读取Hbase数据

#### 案例

> 将hbase2.1.0版本的数据打印出来

#### 解决方案

> 创建FlinkSql作业类型，添加以下依赖  
> 下载hbase对应版本的依赖包  

- [flink-connector-hbase-2.2-4.0.0-1.18.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/flink-connector-hbase-2.2-4.0.0-1.18.jar)
- [flink-connector-hbase-base-4.0.0-1.18.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/flink-connector-hbase-base-4.0.0-1.18.jar)
- [hbase-client-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-client-2.1.0.jar)
- [hbase-common-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-common-2.1.0.jar)
- [hbase-protocol-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-protocol-2.1.0.jar)
- [hbase-protocol-shaded-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-protocol-shaded-2.1.0.jar)
- [hbase-shaded-miscellaneous-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-shaded-miscellaneous-2.1.0.jar)
- [hbase-shaded-netty-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-shaded-netty-2.1.0.jar)
- [hbase-shaded-protobuf-2.1.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/hbase-shaded-protobuf-2.1.0.jar)

![20250325144851](https://img.isxcode.com/picgo/20250325144851.png)

#### 示例sql如下

```sql
CREATE TABLE hTable (
   rowkey string,
   cf1 ROW<username STRING, age STRING>
) WITH (
  'connector' = 'hbase-2.2',
  'table-name' = 'ispong_namespace:users',
  'zookeeper.quorum' = '127.0.0.1:2181'
);

CREATE TABLE print_sink ( 
  rowid string,
  username string,
  age string
) WITH ( 
    'connector' = 'print' 
);

INSERT INTO print_sink (rowid,username,age)
SELECT rowkey,cf1.username,cf1.age FROM hTable;
```
