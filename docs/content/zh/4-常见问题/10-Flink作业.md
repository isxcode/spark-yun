---
title: "Flink作业"
---

#### 问题1

```wikitext
standalone模式无法执行mysql同步
```

> 下载flink的cdc驱动，上传资源中心依赖

https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiliuyun/install/flink-connector-jdbc-3.1.2-1.18.jar

![20241225180640](https://img.isxcode.com/picgo/20241225180640.png)


#### 问题2

```log
'scan.incremental.snapshot.chunk.key-column' is required for table without primary key when 'scan.incremental.snapshot.enabled' enabled.
```

##### 解决方案

```wikitext
在来源表添加
'scan.incremental.snapshot.enabled'='false'
```

#### 问题3

```log
The main method caused an error: please declare primary key for sink table when query contains update/delete record.
```

##### 解决方案

```wikitext
目标表添加
PRIMARY KEY(username) NOT ENFORCED
```

#### 问题4

```log
The MySQL server has a timezone offset (0 seconds ahead of UTC) which does not match the configured timezone Asia/Shanghai. Specify the right server-time-zone to avoid inconsistencies for time-related fields.
```
##### 解决方案

```sql
-- 时区问题
show variables like '%time_zone%';
set time_zone='+8:00';

-- 或者配置
来源表
'server-time-zone'='UTC'
```

#### 问题5

```bash
Caused by: java.sql.SQLSyntaxErrorException: Access denied; you need (at least one of) the RELOAD or FLUSH_TABLES privilege(s) for this operation
```

##### 解决方案

```sql
-- 权限不足
SHOW GRANTS for ispong;
GRANT SELECT,RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'ispong'@'%';
FLUSH PRIVILEGES;
SHOW GRANTS for ispong;
```

#### 问题5

```log
Caused by: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'MASTER STATUS' at line 1
	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:121)
	at com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping.translateException(SQLExceptionsMapping.java:122)
	at com.mysql.cj.jdbc.StatementImpl.executeQuery(StatementImpl.java:1200)
	at io.debezium.jdbc.JdbcConnection.query(JdbcConnection.java:553)
	at io.debezium.jdbc.JdbcConnection.query(JdbcConnection.java:496)
	at io.debezium.connector.mysql.MySqlSnapshotChangeEventSource.determineSnapshotOffset(MySqlSnapshotChangeEventSource.java:276)
	at io.debezium.connector.mysql.MySqlSnapshotChangeEventSource.determineSnapshotOffset(MySqlSnapshotChangeEventSource.java:46)
	at io.debezium.relational.RelationalSnapshotChangeEventSource.doExecute(RelationalSnapshotChangeEventSource.java:113)
	at io.debezium.pipeline.source.AbstractSnapshotChangeEventSource.execute(AbstractSnapshotChangeEventSource.java:76)
	... 8 more
```

##### 解决方案

```bash
不支持8.4以上的版本，换8.0的mysql版本
```

#### 问题6

```log
来源表没有更新
```

##### 解决方案

```log
来源表必须要有个主键，否则只会增加不会更新
```

#### 问题6

```log
Caused by: org.apache.flink.table.api.TableException: Column 'username' is NOT NULL, however, a null value is being written into it. You can set job configuration 'table.exec.sink.not-null-enforcer'='DROP' to suppress this exception and drop such records silently.
	at org.apache.flink.table.runtime.operators.sink.ConstraintEnforcer.processNotNullConstraint(ConstraintEnforcer.java:261)
	at org.apache.flink.table.runtime.operators.sink.ConstraintEnforcer.processElement(ConstraintEnforcer.java:241)
	at org.apache.flink.streaming.runtime.tasks.CopyingChainingOutput.pushToOperator(CopyingChainingOutput.java:75)
	at org.apache.flink.streaming.runtime.tasks.CopyingChainingOutput.collect(CopyingChainingOutput.java:50)
	at org.apache.flink.streaming.runtime.tasks.CopyingChainingOutput.collect(CopyingChainingOutput.java:29)
	at org.apache.flink.streaming.api.operators.StreamSourceContexts$ManualWatermarkContext.processAndCollect(StreamSourceContexts.java:425)
	at org.apache.flink.streaming.api.operators.StreamSourceContexts$WatermarkContext.collect(StreamSourceContexts.java:520)
	at org.apache.flink.streaming.api.operators.StreamSourceContexts$SwitchingOnClose.collect(StreamSourceContexts.java:110)
	at org.apache.flink.cdc.debezium.internal.DebeziumChangeFetcher.emitRecordsUnderCheckpointLock(DebeziumChangeFetcher.java:273)
	at org.apache.flink.cdc.debezium.internal.DebeziumChangeFetcher.handleBatch(DebeziumChangeFetcher.java:258)
	at org.apache.flink.cdc.debezium.internal.DebeziumChangeFetcher.runFetchLoop(DebeziumChangeFetcher.java:155)
	at org.apache.flink.cdc.debezium.DebeziumSourceFunction.run(DebeziumSourceFunction.java:447)
	at org.apache.flink.streaming.api.operators.StreamSource.run(StreamSource.java:114)
	at org.apache.flink.streaming.api.operators.StreamSource.run(StreamSource.java:71)
	at org.apache.flink.streaming.runtime.tasks.SourceStreamTask$LegacySourceFunctionThread.run(SourceStreamTask.java:338)
```

##### 解决方案

> 在flinkConfig中添加配置
> 数据中包含null 直接跳过

```json
{
  "table.exec.sink.not-null-enforcer": "DROP"
}
```

#### 问题7

```wikitext
k8s集群无法找到镜像
```

> 可能k8s指定了固定镜像前缀，需要手动修改镜像名，推送到指定路径

```bash
docker tag flink:1.18.1-scala_2.12 isxcode:8443/library/flink:1.18.1-scala_2.12
docker push isxcode:8443/library/flink:1.18.1-scala_2.12
```

#### 问题8

```log
Caused by: com.mysql.cj.exceptions.UnableToConnectException: Public Key Retrieval is not allowed
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
	at com.mysql.cj.exceptions.ExceptionFactory.createException(ExceptionFactory.java:62)
	at com.mysql.cj.exceptions.ExceptionFactory.createException(ExceptionFactory.java:86)
	at com.mysql.cj.protocol.a.authentication.CachingSha2PasswordPlugin.nextAuthenticationStep(CachingSha2PasswordPlugin.java:130)
	at com.mysql.cj.protocol.a.authentication.CachingSha2PasswordPlugin.nextAuthenticationStep(CachingSha2PasswordPlugin.java:49)
	at com.mysql.cj.protocol.a.NativeAuthenticationProvider.proceedHandshakeWithPluggableAuthentication(NativeAuthenticationProvider.java:443)
	at com.mysql.cj.protocol.a.NativeAuthenticationProvider.connect(NativeAuthenticationProvider.java:213)
	at com.mysql.cj.protocol.a.NativeProtocol.connect(NativeProtocol.java:1430)
	at com.mysql.cj.NativeSession.connect(NativeSession.java:134)
	at com.mysql.cj.jdbc.ConnectionImpl.connectOneTryOnly(ConnectionImpl.java:939)
	at com.mysql.cj.jdbc.ConnectionImpl.createNewIO(ConnectionImpl.java:809)
	... 23 more
```

##### 解决方案

> jdbc连接配置问题

```json
jdbc:mysql://localhost:3306/your_database?useSSL=false&allowPublicKeyRetrieval=trues
```

#### 问题9

```log
Caused by: io.debezium.DebeziumException: java.sql.SQLSyntaxErrorException: Access denied; you need (at least one of) the SUPER, REPLICATION CLIENT privilege(s) for this operation
	at io.debezium.pipeline.source.AbstractSnapshotChangeEventSource.execute(AbstractSnapshotChangeEventSource.java:85)
	at io.debezium.pipeline.ChangeEventSourceCoordinator.doSnapshot(ChangeEventSourceCoordinator.java:155)
	at io.debezium.pipeline.ChangeEventSourceCoordinator.executeChangeEventSources(ChangeEventSourceCoordinator.java:137)
	at io.debezium.pipeline.ChangeEventSourceCoordinator.lambda$start$0(ChangeEventSourceCoordinator.java:109)
	... 5 more
Caused by: java.sql.SQLSyntaxErrorException: Access denied; you need (at least one of) the SUPER, REPLICATION CLIENT privilege(s) for this operation
	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:121)
	at com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping.translateException(SQLExceptionsMapping.java:122)
	at com.mysql.cj.jdbc.StatementImpl.executeQuery(StatementImpl.java:1200)
	at io.debezium.jdbc.JdbcConnection.query(JdbcConnection.java:553)
	at io.debezium.jdbc.JdbcConnection.query(JdbcConnection.java:496)
	at io.debezium.connector.mysql.MySqlSnapshotChangeEventSource.determineSnapshotOffset(MySqlSnapshotChangeEventSource.java:276)
	at io.debezium.connector.mysql.MySqlSnapshotChangeEventSource.readTableStructure(MySqlSnapshotChangeEventSource.java:315)
	at io.debezium.connector.mysql.MySqlSnapshotChangeEventSource.readTableStructure(MySqlSnapshotChangeEventSource.java:46)
	at io.debezium.relational.RelationalSnapshotChangeEventSource.doExecute(RelationalSnapshotChangeEventSource.java:116)
	at io.debezium.pipeline.source.AbstractSnapshotChangeEventSource.execute(AbstractSnapshotChangeEventSource.java:76)
	... 8 more
```

> 权限不足

```sql
GRANT SELECT, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'ispong'@'%';  
FLUSH PRIVILEGES;
```

#### 问题10

```log
Caused by: io.debezium.DebeziumException: java.sql.SQLSyntaxErrorException: Access denied; you need (at least one of) the RELOAD or FLUSH_TABLES privilege(s) for this operation
	at io.debezium.pipeline.source.AbstractSnapshotChangeEventSource.execute(AbstractSnapshotChangeEventSource.java:85)
	at io.debezium.pipeline.ChangeEventSourceCoordinator.doSnapshot(ChangeEventSourceCoordinator.java:155)
	at io.debezium.pipeline.ChangeEventSourceCoordinator.executeChangeEventSources(ChangeEventSourceCoordinator.java:137)
	at io.debezium.pipeline.ChangeEventSourceCoordinator.lambda$start$0(ChangeEventSourceCoordinator.java:109)
	... 5 more
Caused by: java.sql.SQLSyntaxErrorException: Access denied; you need (at least one of) the RELOAD or FLUSH_TABLES privilege(s) for this operation
	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:121)
	at com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping.translateException(SQLExceptionsMapping.java:122)
	at com.mysql.cj.jdbc.StatementImpl.executeInternal(StatementImpl.java:763)
	at com.mysql.cj.jdbc.StatementImpl.execute(StatementImpl.java:648)
	at io.debezium.jdbc.JdbcConnection.executeWithoutCommitting(JdbcConnection.java:1446)
	at io.debezium.connector.mysql.MySqlSnapshotChangeEventSource.tableLock(MySqlSnapshotChangeEventSource.java:450)
	at io.debezium.connector.mysql.MySqlSnapshotChangeEventSource.readTableStructure(MySqlSnapshotChangeEventSource.java:314)
	at io.debezium.connector.mysql.MySqlSnapshotChangeEventSource.readTableStructure(MySqlSnapshotChangeEventSource.java:46)
	at io.debezium.relational.RelationalSnapshotChangeEventSource.doExecute(RelationalSnapshotChangeEventSource.java:116)
	at io.debezium.pipeline.source.AbstractSnapshotChangeEventSource.execute(AbstractSnapshotChangeEventSource.java:76)
	... 8 more
```

> 权限不足

```sql
GRANT RELOAD ON *.* TO 'ispong'@'%';  
FLUSH PRIVILEGES;
```

#### 问题11 依赖缺少

```log
Caused by: java.lang.ClassNotFoundException: org.apache.hadoop.mapred.JobConf
hadoop-mapreduce-client-core-3.0.0.jar

Caused by: java.lang.NoClassDefFoundError: org/apache/hadoop/fs/BatchListingOperations
hadoop-common-3.4.1.jar

Caused by: java.lang.ClassNotFoundException: org.apache.hadoop.hive.metastore.api.NoSuchObjectException
hive-metastore.jar
```

#### 问题12 

```log
java.util.concurrent.CompletionException: org.apache.flink.client.deployment.application.ApplicationExecutionException: The application contains no execute() calls.
	at java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:292) ~[?:1.8.0_202]
	at java.util.concurrent.CompletableFuture.completeThrowable(CompletableFuture.java:308) ~[?:1.8.0_202]
	at java.util.concurrent.CompletableFuture.uniCompose(CompletableFuture.java:943) ~[?:1.8.0_202]
	at java.util.concurrent.CompletableFuture$UniCompose.tryFire(CompletableFuture.java:926) ~[?:1.8.0_202]
	at java.util.concurrent.CompletableFuture.postComplete(CompletableFuture.java:474) ~[?:1.8.0_202]
	at java.util.concurrent.CompletableFuture.completeExceptionally(CompletableFuture.java:1977) ~[?:1.8.0_202]
	at org.apache.flink.client.deployment.application.ApplicationDispatcherBootstrap.runApplicationEntryPoint(ApplicationDispatcherBootstrap.java:309) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.deployment.application.ApplicationDispatcherBootstrap.lambda$runApplicationAsync$2(ApplicationDispatcherBootstrap.java:254) ~[flink-dist-1.18.1.jar:1.18.1]
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) ~[?:1.8.0_202]
	at java.util.concurrent.FutureTask.run(FutureTask.java:266) ~[?:1.8.0_202]
	at org.apache.flink.runtime.concurrent.pekko.ActorSystemScheduledExecutorAdapter$ScheduledFutureTask.run(ActorSystemScheduledExecutorAdapter.java:172) ~[?:?]
	at org.apache.flink.runtime.concurrent.ClassLoadingUtils.runWithContextClassLoader(ClassLoadingUtils.java:68) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.runtime.concurrent.ClassLoadingUtils.lambda$withContextClassLoader$0(ClassLoadingUtils.java:41) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.pekko.dispatch.TaskInvocation.run(AbstractDispatcher.scala:59) [flink-rpc-akka878eb79e-f52f-45fd-948c-efa59f8e9145.jar:1.18.1]
	at org.apache.pekko.dispatch.ForkJoinExecutorConfigurator$PekkoForkJoinTask.exec(ForkJoinExecutorConfigurator.scala:57) [flink-rpc-akka878eb79e-f52f-45fd-948c-efa59f8e9145.jar:1.18.1]
	at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289) [?:1.8.0_202]
	at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056) [?:1.8.0_202]
	at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692) [?:1.8.0_202]
	at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157) [?:1.8.0_202]
Caused by: org.apache.flink.client.deployment.application.ApplicationExecutionException: The application contains no execute() calls.
	... 13 more
```

```text
FlinkSql内容异常
```