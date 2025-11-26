---
title: "Paimon集成"
---

## Paimon集成

#### 案例

> Paimon在本地中建表，插入数据，查询数据

#### 解决方案

> 创建FlinkSql作业类型，添加以下依赖

- [paimon-flink-1.18-1.1-20250124.002530-34.jar下载](https://repository.apache.org/content/groups/snapshots/org/apache/paimon/paimon-flink-1.18/1.1-SNAPSHOT/paimon-flink-1.18-1.1-20250124.002530-34.jar)
- [flink-shaded-hadoop-2-uber-2.8.3-10.0.jar下载](https://repo1.maven.org/maven2/org/apache/flink/flink-shaded-hadoop-2-uber/2.8.3-10.0/flink-shaded-hadoop-2-uber-2.8.3-10.0.jar)

![20250124171455](https://img.isxcode.com/picgo/20250124171455.png)

##### 建表，插入数据

```sql
CREATE CATALOG my_catalog WITH (
    'type'='paimon',
    'warehouse'='file:/Users/ispong/Downloads/paimon'
);

USE CATALOG my_catalog;

CREATE TABLE word_count (
    word STRING PRIMARY KEY NOT ENFORCED,
    cnt BIGINT
) WITH (
    'file.format' = 'csv' 
);

INSERT INTO word_count
values ('张三', 13);
```

##### 查询数据

```sql
CREATE CATALOG my_catalog WITH (
    'type'='paimon',
    'warehouse'='file:/Users/ispong/Downloads/paimon'
);

USE CATALOG my_catalog;

CREATE TEMPORARY TABLE print_sink ( 
    username string not null,
    age bigint
) WITH ( 
    'connector' = 'print' 
);

insert into print_sink select * from word_count ;
```

![20250124171432](https://img.isxcode.com/picgo/20250124171432.png)