---
title: "Hdfs小文件问题"
---

### 案例说明

> 用户执行一段时间sparkSql作业后，产生大量小文件，导致计算速度变慢。

### 案例分析

> 原始表数据

```wikitext
[isxcode@isxcode-qa ~]$ hadoop fs -ls -h /user/hive/warehouse/ispong_db.db/customer
Found 2 items
-rwxrwxrwt   3 isxcode hive    136.6 M 2024-11-20 17:42 /user/hive/warehouse/ispong_db.db/customer/part-00000-97073513-bdec-4674-a0ad-4625f335230d-c000
-rwxrwxrwt   3 isxcode hive    136.6 M 2024-11-20 17:44 /user/hive/warehouse/ispong_db.db/customer/part-00001-97073513-bdec-4674-a0ad-4625f335230d-c000
```

> 创建目标表

```sql
CREATE TABLE IF NOT EXISTS `customer_bak` (
  `c_custkey` int  COMMENT "",
  `c_name` string  COMMENT "",
  `c_address` string  COMMENT "",
  `c_city` string  COMMENT "",
  `c_nation` string  COMMENT "",
  `c_region` string  COMMENT "",
  `c_phone` string  COMMENT "",
  `c_mktsegment` string  COMMENT ""
);
```

> 用户sql

```sql
insert overwrite customer_bak (
    select c2.* from customer c1 left join customer c2 on c1.c_custkey = c2.c_custkey 
    union all 
    select c4.* from customer c3 left join customer_bak c4 on c3.c_custkey = c4.c_custkey 
)
```

> 查询文件结构，文件被拆分了，属于正常情况

```wikitext
[isxcode@isxcode-qa root]$ hadoop fs -ls -h /user/hive/warehouse/ispong_db.db/customer_bak
Found 6 items
-rwxr-xr-x   3 isxcode hive     61.2 M 2025-06-04 10:12 /user/hive/warehouse/ispong_db.db/customer_bak/part-00000-5c1a3bb3-22ab-4bb6-9b42-eed5a6792e8e-c000
-rwxr-xr-x   3 isxcode hive     61.3 M 2025-06-04 10:12 /user/hive/warehouse/ispong_db.db/customer_bak/part-00001-5c1a3bb3-22ab-4bb6-9b42-eed5a6792e8e-c000
-rwxr-xr-x   3 isxcode hive     61.1 M 2025-06-04 10:12 /user/hive/warehouse/ispong_db.db/customer_bak/part-00002-5c1a3bb3-22ab-4bb6-9b42-eed5a6792e8e-c000
-rwxr-xr-x   3 isxcode hive     61.3 M 2025-06-04 10:12 /user/hive/warehouse/ispong_db.db/customer_bak/part-00003-5c1a3bb3-22ab-4bb6-9b42-eed5a6792e8e-c000
-rwxr-xr-x   3 isxcode hive     21.4 M 2025-06-04 10:12 /user/hive/warehouse/ispong_db.db/customer_bak/part-00004-5c1a3bb3-22ab-4bb6-9b42-eed5a6792e8e-c000
-rwxr-xr-x   3 isxcode hive     68.7 M 2025-06-04 10:12 /user/hive/warehouse/ispong_db.db/customer_bak/part-00005-5c1a3bb3-22ab-4bb6-9b42-eed5a6792e8e-c000
```

#### 解决方案

```sql
-- 添加以下参数，可以合并小文件，但是大文件执行速度会变慢
SET spark.sql.shuffle.partitions=2;

truncate table customer_bak;
insert overwrite customer_bak (
    select c2.* from customer c1 left join customer c2 on c1.c_custkey = c2.c_custkey 
    union all 
    select c4.* from customer c3 left join customer_bak c4 on c3.c_custkey = c4.c_custkey 
);
select count(1) from customer_bak;
```

> 查询文件结构，文件被合并成了大文件

```wikitext
[isxcode@isxcode-qa root]$ hadoop fs -ls -h /user/hive/warehouse/ispong_db.db/customer_bak
Found 3 items
-rwxr-xr-x   3 isxcode hive    133.1 M 2025-06-04 10:23 /user/hive/warehouse/ispong_db.db/customer_bak/part-00000-3f2d528f-6246-4d69-ac31-1cd0f1a8bb1a-c000
-rwxr-xr-x   3 isxcode hive    133.0 M 2025-06-04 10:23 /user/hive/warehouse/ispong_db.db/customer_bak/part-00001-3f2d528f-6246-4d69-ac31-1cd0f1a8bb1a-c000
-rwxr-xr-x   3 isxcode hive     68.7 M 2025-06-04 10:23 /user/hive/warehouse/ispong_db.db/customer_bak/part-00002-3f2d528f-6246-4d69-ac31-1cd0f1a8bb1a-c000
```