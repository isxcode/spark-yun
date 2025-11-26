---
title: "Kafka多层级解析"
---

## Kafka多层级解析

#### 案例1

> 使用JSON_VALUE解析json

```json
{
  "name": "zhangsan"
}
```

```sql
CREATE TABLE from_table
(
    data string
) WITH (
      'connector' = 'kafka',
      'topic' = 'ispong-topic',
      'properties.bootstrap.servers' = '127.0.0.1:30122',
      'scan.startup.mode' = 'latest-offset',
      'format' = 'raw'
);

CREATE TABLE target_table ( 
     username STRING
) WITH ( 
    'connector' = 'print' 
);

INSERT INTO target_table (username)
select JSON_VALUE(data, '$.name') AS name
from from_table;
```

#### 案例2

> 使用flinkSql解析json

```json
{
  "details": {
    "username": "zhangsan",
    "age": 12
  }
}
```

```sql
CREATE TABLE from_table
(
  details ROW<username String, age int> 
) WITH (
      'connector' = 'kafka',
      'topic' = 'ispong-topic',
      'properties.bootstrap.servers' = '127.0.0.1:30122',
      'scan.startup.mode' = 'latest-offset',
      'format' = 'json',          
      'json.fail-on-missing-field' = 'false',
      'json.ignore-parse-errors' = 'true'
);

CREATE TABLE target_table ( 
     username STRING,
     age INT 
) WITH ( 
    'connector' = 'print' 
);

INSERT INTO target_table (username, age)
select details.username,details.age
from from_table;
```

#### 案例3

> 使用UNNEST解析数组

```json
{
  "username": "zhangsan",
  "hobbies": [
    1,
    2,
    3,
    4
  ]
}
```

```sql
CREATE TABLE from_table (
    username STRING,
    hobbies ARRAY<INT>
) WITH (
    'connector' = 'kafka',
    'topic' = 'ispong-topic',
    'properties.bootstrap.servers' = '127.0.0.1:30122',
    'scan.startup.mode' = 'latest-offset',
    'format' = 'json',          
    'json.fail-on-missing-field' = 'false',
    'json.ignore-parse-errors' = 'true'
);

CREATE TABLE target_table (
    username STRING,
    age INT
) WITH (
    'connector' = 'print'
);

INSERT INTO target_table (username, age)
SELECT 
    username,
    hobby AS age
FROM from_table,
UNNEST(hobbies) AS t(hobby);
```