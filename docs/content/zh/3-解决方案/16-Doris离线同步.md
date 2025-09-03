---
title: "Doris离线同步"
---

## Doris离线同步

> 实现在Doris中结果表向目标表中离线同步数据

#### 案例

> Doris中将t_source中的数据离线同步到t_target中

#### 解决方案

> 创建FlinkSql作业类型，添加以下依赖

- [flink-doris-connector-1.18-25.0.0.jar下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/jars/flink-doris-connector-1.18-25.0.0.jar)

![20250124154311](https://img.isxcode.com/picgo/20250124154311.png)

```sql
CREATE TABLE from_table
(
    username STRING,
    age      INT
) WITH (
      'connector' = 'doris',
      'fenodes' = '127.0.0.1:8030',
      'table.identifier' = 'ispong_db.t_source',
      'username' = 'root',
      'password' = 'root123'
);

CREATE TABLE target_table
(
    username STRING,
    age      INT
) WITH (
      'connector' = 'doris',
      'fenodes' = '127.0.0.1:8030',
      'table.identifier' = 'ispong_db.t_target',
      'username' = 'root',
      'password' = 'root123'
);

INSERT INTO target_table
select *
from from_table;
```

| 配置项              | 说明                    |
|------------------|-----------------------|
| connector        | 连接方式                  |
| fenodes          | doris的fe连接，默认端口`8030` |
| table.identifier | 库名.表名                 |
| username         | 数据源账号                 |
| password         | 账号密码                  |