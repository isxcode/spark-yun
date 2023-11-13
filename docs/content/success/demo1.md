?> 多数据源交叉查询，可以同时处理Mysql、Oracle、SqlServer中的数据，并且生成的新的表数据同步到任意一张表中。

##### 案例

> 客户有三个部门，且三个部门使用不同类型的数据库，此时有个需求，对三个部门的数据做汇总计算。

##### 解决方案

!> 创建SparkSql作业,注意`OPTIONS`中的字符不能有多余空格

```sql
-- 引入部门一的表
CREATE TEMPORARY VIEW table1
USING org.apache.spark.sql.jdbc
OPTIONS (
    driver 'mysql_driver',
    url 'mysql_jdbc_url',
    user 'name',
    password 'passwd',
    dbtable 'table'
);
    
-- 引入部门二的表
CREATE TEMPORARY VIEW table2
USING org.apache.spark.sql.jdbc
OPTIONS (
    driver 'oracle_driver',
    url 'oracle_jdbc_url',
    user 'name',
    password 'passwd',
    dbtable 'table'
);
    
-- 引入部门三的表
CREATE TEMPORARY VIEW table3
USING org.apache.spark.sql.jdbc
OPTIONS (
    driver 'sqlserver_driver',
    url 'sqlserver_jdbc_url',
    user 'name',
    password 'passwd',
    dbtable 'table'
);
    
-- 将部门一和部门二的数据汇总后，写入部门三中
insert into table3 select * from table2 join in table1 on table1.id = table2.id
```

##### 真实案例

> starrocks同步到spark

```sql
CREATE TEMPORARY VIEW table1
USING org.apache.spark.sql.jdbc
OPTIONS (
    driver 'com.mysql.jdbc.Driver',
    url 'jdbc:mysql://39.100.69.68:30191/ispong_db',
    user 'root',
    password '',
    dbtable 'users'
);

insert into ispong_db.users_textfile select username,age from table1;

select * from ispong_db.users_textfile;
```