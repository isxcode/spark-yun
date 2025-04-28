---
title: "计算mysql中的数据"
---

### 案例说明

> 计算mysql中的数据

### 案例实现

```sql
CREATE TABLE table1
USING JDBC
OPTIONS (
    driver 'com.mysql.cj.jdbc.Driver',
    url 'jdbc:mysql://47.92.168.116:30102/ispong_db',
    user 'ispong',
    password 'ispong123',
    dbtable 'zqy_users_jdbc'
);

select * from table1;
```