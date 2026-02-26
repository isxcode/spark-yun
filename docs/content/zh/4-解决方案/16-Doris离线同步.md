---
title: "Doris离线同步"
---

## Doris离线同步

> 实现在Doris中结果表向目标表中离线同步数据

#### 案例

> Doris中将t_source中的数据离线同步到t_target中

#### 注意

> doris的相关端口号需要开放，否则同步会失败

| 实例名称   | 端口名称                     | 默认端口     | 通信方向                     | 说明                                                    |
|:-------|:-------------------------|:---------|:-------------------------|:------------------------------------------------------|
| **FE** | `http_port`              | **8030** | Client <-> FE, FE <-> FE | **HTTP 服务器端口**，用于提交任务、获取元数据等。CDC 任务主要依赖此端口。           |
|        | `query_port`             | 9030     | Client <-> FE            | **MySQL 服务器端口**，用于通过 MySQL 协议连接 FE 执行 SQL 命令（如建表、查询）。 |
|        | `rpc_port`               | 9020     | BE -> FE, FE <-> FE      | Thrift 服务器端口，用于 FE 与 BE 的内部通信。                        |
|        | `edit_log_port`          | 9010     | FE <-> FE                | FE 集群内部通信端口（bdbje）。                                   |
| **BE** | `webserver_port`         | **8040** | Client <-> BE, BE <-> BE | **HTTP 服务器端口**，用于数据导入和 Tablet 上报等。CDC 任务写入数据依赖此端口。    |
|        | `be_port`                | 9060     | FE -> BE                 | Thrift 服务器端口，用于接收来自 FE 的请求。                           |
|        | `brpc_port`              | 8060     | FE <-> BE, BE <-> BE     | BRPC 通信端口，用于 BE 之间的内部通信。                              |
|        | `heartbeat_service_port` | 9050     | FE -> BE                 | 心跳服务端口。                                               |

#### 解决方案

> 创建FlinkSql作业类型，添加以下依赖

- [flink-doris-connector-1.18-25.0.0.jar下载](https://zhiqingyun-demo.isxcode.com/tools/open/file/flink-doris-connector-1.18-25.0.0.jar)

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