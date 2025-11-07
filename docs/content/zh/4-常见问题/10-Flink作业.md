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

#### 问题13  

```log
 Caused by: java.lang.ClassNotFoundException: com.facebook.fb303.FacebookService$Iface
	at java.net.URLClassLoader.findClass(URLClassLoader.java:387) ~[?:1.8.0_412]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:418) ~[?:1.8.0_412]
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352) ~[?:1.8.0_412]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:351) ~[?:1.8.0_412]
	at java.lang.ClassLoader.defineClass1(Native Method) ~[?:1.8.0_412]
	at java.lang.ClassLoader.defineClass(ClassLoader.java:756) ~[?:1.8.0_412]
	at java.security.SecureClassLoader.defineClass(SecureClassLoader.java:142) ~[?:1.8.0_412]
	at java.net.URLClassLoader.defineClass(URLClassLoader.java:473) ~[?:1.8.0_412]
	at java.net.URLClassLoader.access$100(URLClassLoader.java:74) ~[?:1.8.0_412]
	at java.net.URLClassLoader$1.run(URLClassLoader.java:369) ~[?:1.8.0_412]
	at java.net.URLClassLoader$1.run(URLClassLoader.java:363) ~[?:1.8.0_412]
	at java.security.AccessController.doPrivileged(Native Method) ~[?:1.8.0_412]
	at java.net.URLClassLoader.findClass(URLClassLoader.java:362) ~[?:1.8.0_412]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:418) ~[?:1.8.0_412]
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352) ~[?:1.8.0_412]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:351) ~[?:1.8.0_412]
	at java.lang.Class.forName0(Native Method) ~[?:1.8.0_412]
	at java.lang.Class.forName(Class.java:348) ~[?:1.8.0_412]
	at org.apache.hadoop.hive.metastore.utils.JavaUtils.getClass(JavaUtils.java:52) ~[sy_1986612053931524096.jar:3.1.3]
	at org.apache.hadoop.hive.metastore.RetryingMetaStoreClient.getProxy(RetryingMetaStoreClient.java:146) ~[sy_1986612053931524096.jar:3.1.3]
	at org.apache.hadoop.hive.metastore.RetryingMetaStoreClient.getProxy(RetryingMetaStoreClient.java:119) ~[sy_1986612053931524096.jar:3.1.3]
	at org.apache.hadoop.hive.metastore.RetryingMetaStoreClient.getProxy(RetryingMetaStoreClient.java:112) ~[sy_1986612053931524096.jar:3.1.3]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:1.8.0_412]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[?:1.8.0_412]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:1.8.0_412]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[?:1.8.0_412]
	at org.apache.iceberg.common.DynMethods$UnboundMethod.invokeChecked(DynMethods.java:62) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.common.DynMethods$UnboundMethod.invoke(DynMethods.java:74) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.common.DynMethods$StaticMethod.invoke(DynMethods.java:187) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.HiveClientPool.newClient(HiveClientPool.java:63) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.HiveClientPool.newClient(HiveClientPool.java:34) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.ClientPoolImpl.get(ClientPoolImpl.java:143) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.ClientPoolImpl.run(ClientPoolImpl.java:70) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.ClientPoolImpl.run(ClientPoolImpl.java:65) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.CachedClientPool.run(CachedClientPool.java:122) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.HiveTableOperations.doRefresh(HiveTableOperations.java:147) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.BaseMetastoreTableOperations.refresh(BaseMetastoreTableOperations.java:90) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.BaseMetastoreTableOperations.current(BaseMetastoreTableOperations.java:73) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.BaseMetastoreCatalog$BaseMetastoreCatalogTableBuilder.create(BaseMetastoreCatalog.java:191) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.CachingCatalog$CachingTableBuilder.lambda$create$0(CachingCatalog.java:262) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.lambda$doComputeIfAbsent$14(BoundedLocalCache.java:2406) ~[sy_1986723935774375936.jar:?]
	at java.util.concurrent.ConcurrentHashMap.compute(ConcurrentHashMap.java:1853) ~[?:1.8.0_412]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.doComputeIfAbsent(BoundedLocalCache.java:2404) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.computeIfAbsent(BoundedLocalCache.java:2387) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.LocalCache.computeIfAbsent(LocalCache.java:108) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.LocalManualCache.get(LocalManualCache.java:62) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.CachingCatalog$CachingTableBuilder.create(CachingCatalog.java:258) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.catalog.Catalog.createTable(Catalog.java:75) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.flink.FlinkCatalog.createIcebergTable(FlinkCatalog.java:415) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.flink.FlinkCatalog.createTable(FlinkCatalog.java:395) ~[sy_1986723935774375936.jar:?]
	at org.apache.flink.table.catalog.CatalogManager.lambda$createTable$18(CatalogManager.java:957) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.catalog.CatalogManager.execute(CatalogManager.java:1290) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.catalog.CatalogManager.createTable(CatalogManager.java:946) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.operations.ddl.CreateTableOperation.execute(CreateTableOperation.java:84) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.api.internal.TableEnvironmentImpl.executeInternal(TableEnvironmentImpl.java:1092) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.api.internal.TableEnvironmentImpl.executeSql(TableEnvironmentImpl.java:735) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at com.isxcode.spark.plugin.flink.sql.execute.Job.main(Job.java:51) ~[flink-sql-execute-plugin.jar:?]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:1.8.0_412]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[?:1.8.0_412]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:1.8.0_412]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[?:1.8.0_412]
	at org.apache.flink.client.program.PackagedProgram.callMainMethod(PackagedProgram.java:355) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.program.PackagedProgram.invokeInteractiveModeForExecution(PackagedProgram.java:222) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.ClientUtils.executeProgram(ClientUtils.java:105) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.deployment.application.ApplicationDispatcherBootstrap.runApplicationEntryPoint(ApplicationDispatcherBootstrap.java:301) ~[flink-dist-1.18.1.jar:1.18.1]
	... 12 more
```

缺少依赖[libfb303-0.9.3.jar下载](https://repo1.maven.org/maven2/org/apache/thrift/libfb303/0.9.3/libfb303-0.9.3.jar)

#### 问题14  

```log
Caused by: java.lang.ClassNotFoundException: org.apache.thrift.TException
		at java.net.URLClassLoader.findClass(URLClassLoader.java:387) ~[?:1.8.0_412]
		at java.lang.ClassLoader.loadClass(ClassLoader.java:418) ~[?:1.8.0_412]
		at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352) ~[?:1.8.0_412]
		at java.lang.ClassLoader.loadClass(ClassLoader.java:351) ~[?:1.8.0_412]
		... 35 more
```

缺少依赖[libthrift-0.9.3.jar下载](https://repo1.maven.org/maven2/org/apache/thrift/libthrift/0.9.3/libthrift-0.9.3.jar)

#### 问题15

```log
Caused by: java.lang.ClassNotFoundException: org.apache.hadoop.hive.metastore.api.NoSuchObjectException
    at java.net.URLClassLoader.findClass(URLClassLoader.java:387) ~[?:1.8.0_412]
    at java.lang.ClassLoader.loadClass(ClassLoader.java:418) ~[?:1.8.0_412]
    at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352) ~[?:1.8.0_412]
    at java.lang.ClassLoader.loadClass(ClassLoader.java:351) ~[?:1.8.0_412]
    ... 35 more
```

缺少依赖[hive-standalone-metastore-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-standalone-metastore/3.1.3/hive-standalone-metastore-3.1.3.jar)

#### 问题16

```log
Caused by: java.lang.ClassNotFoundException: org.apache.hadoop.hive.conf.HiveConf$ConfVars
	at java.net.URLClassLoader.findClass(URLClassLoader.java:387) ~[?:1.8.0_412]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:418) ~[?:1.8.0_412]
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352) ~[?:1.8.0_412]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:351) ~[?:1.8.0_412]
	at org.apache.iceberg.hive.HiveCatalog.initialize(HiveCatalog.java:95) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.CatalogUtil.loadCatalog(CatalogUtil.java:256) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.flink.CatalogLoader$HiveCatalogLoader.loadCatalog(CatalogLoader.java:128) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.flink.FlinkCatalog.<init>(FlinkCatalog.java:114) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.flink.FlinkCatalogFactory.createCatalog(FlinkCatalogFactory.java:166) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.flink.FlinkCatalogFactory.createCatalog(FlinkCatalogFactory.java:139) ~[sy_1986723935774375936.jar:?]
	at org.apache.flink.table.factories.FactoryUtil.createCatalog(FactoryUtil.java:476) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.catalog.CatalogManager.initCatalog(CatalogManager.java:316) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.catalog.CatalogManager.createCatalog(CatalogManager.java:308) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.operations.ddl.CreateCatalogOperation.execute(CreateCatalogOperation.java:68) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.api.internal.TableEnvironmentImpl.executeInternal(TableEnvironmentImpl.java:1092) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.api.internal.TableEnvironmentImpl.executeSql(TableEnvironmentImpl.java:735) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at com.isxcode.spark.plugin.flink.sql.execute.Job.main(Job.java:51) ~[flink-sql-execute-plugin.jar:?]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:1.8.0_412]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[?:1.8.0_412]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:1.8.0_412]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[?:1.8.0_412]
	at org.apache.flink.client.program.PackagedProgram.callMainMethod(PackagedProgram.java:355) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.program.PackagedProgram.invokeInteractiveModeForExecution(PackagedProgram.java:222) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.ClientUtils.executeProgram(ClientUtils.java:105) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.deployment.application.ApplicationDispatcherBootstrap.runApplicationEntryPoint(ApplicationDispatcherBootstrap.java:301) ~[flink-dist-1.18.1.jar:1.18.1]
	... 12 more
```

缺少依赖[hive-common-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-common/3.1.3/hive-common-3.1.3.jar)

#### 问题17

```log
Caused by: java.lang.ClassNotFoundException: org.apache.hadoop.mapred.JobConf
	at java.net.URLClassLoader.findClass(URLClassLoader.java:387) ~[?:1.8.0_412]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:418) ~[?:1.8.0_412]
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352) ~[?:1.8.0_412]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:351) ~[?:1.8.0_412]
	at org.apache.hadoop.hive.conf.HiveConf.initialize(HiveConf.java:5144) ~[sy_1986612332714328064.jar:3.1.3]
	at org.apache.hadoop.hive.conf.HiveConf.<init>(HiveConf.java:5112) ~[sy_1986612332714328064.jar:3.1.3]
	at org.apache.iceberg.hive.HiveClientPool.<init>(HiveClientPool.java:55) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.CachedClientPool.lambda$clientPool$0(CachedClientPool.java:96) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.lambda$doComputeIfAbsent$14(BoundedLocalCache.java:2406) ~[sy_1986723935774375936.jar:?]
	at java.util.concurrent.ConcurrentHashMap.compute(ConcurrentHashMap.java:1853) ~[?:1.8.0_412]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.doComputeIfAbsent(BoundedLocalCache.java:2404) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.computeIfAbsent(BoundedLocalCache.java:2387) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.LocalCache.computeIfAbsent(LocalCache.java:108) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.LocalManualCache.get(LocalManualCache.java:62) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.CachedClientPool.clientPool(CachedClientPool.java:96) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.CachedClientPool.run(CachedClientPool.java:122) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.HiveTableOperations.doRefresh(HiveTableOperations.java:147) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.BaseMetastoreTableOperations.refresh(BaseMetastoreTableOperations.java:90) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.BaseMetastoreTableOperations.current(BaseMetastoreTableOperations.java:73) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.BaseMetastoreCatalog$BaseMetastoreCatalogTableBuilder.create(BaseMetastoreCatalog.java:191) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.CachingCatalog$CachingTableBuilder.lambda$create$0(CachingCatalog.java:262) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.lambda$doComputeIfAbsent$14(BoundedLocalCache.java:2406) ~[sy_1986723935774375936.jar:?]
	at java.util.concurrent.ConcurrentHashMap.compute(ConcurrentHashMap.java:1853) ~[?:1.8.0_412]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.doComputeIfAbsent(BoundedLocalCache.java:2404) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.computeIfAbsent(BoundedLocalCache.java:2387) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.LocalCache.computeIfAbsent(LocalCache.java:108) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.LocalManualCache.get(LocalManualCache.java:62) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.CachingCatalog$CachingTableBuilder.create(CachingCatalog.java:258) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.catalog.Catalog.createTable(Catalog.java:75) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.flink.FlinkCatalog.createIcebergTable(FlinkCatalog.java:415) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.flink.FlinkCatalog.createTable(FlinkCatalog.java:395) ~[sy_1986723935774375936.jar:?]
	at org.apache.flink.table.catalog.CatalogManager.lambda$createTable$18(CatalogManager.java:957) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.catalog.CatalogManager.execute(CatalogManager.java:1290) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.catalog.CatalogManager.createTable(CatalogManager.java:946) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.operations.ddl.CreateTableOperation.execute(CreateTableOperation.java:84) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.api.internal.TableEnvironmentImpl.executeInternal(TableEnvironmentImpl.java:1092) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.api.internal.TableEnvironmentImpl.executeSql(TableEnvironmentImpl.java:735) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at com.isxcode.spark.plugin.flink.sql.execute.Job.main(Job.java:51) ~[flink-sql-execute-plugin.jar:?]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:1.8.0_412]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[?:1.8.0_412]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:1.8.0_412]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[?:1.8.0_412]
	at org.apache.flink.client.program.PackagedProgram.callMainMethod(PackagedProgram.java:355) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.program.PackagedProgram.invokeInteractiveModeForExecution(PackagedProgram.java:222) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.ClientUtils.executeProgram(ClientUtils.java:105) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.deployment.application.ApplicationDispatcherBootstrap.runApplicationEntryPoint(ApplicationDispatcherBootstrap.java:301) ~[flink-dist-1.18.1.jar:1.18.1]
	... 12 more
```

缺少依赖[hive-standalone-metastore-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-standalone-metastore/3.1.3/hive-standalone-metastore-3.1.3.jar)

#### 问题18

```log
Caused by: java.lang.ClassNotFoundException: org.apache.commons.lang.StringUtils
	at java.net.URLClassLoader.findClass(URLClassLoader.java:387) ~[?:1.8.0_412]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:418) ~[?:1.8.0_412]
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352) ~[?:1.8.0_412]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:351) ~[?:1.8.0_412]
	at org.apache.hadoop.hive.conf.HiveConf.initialize(HiveConf.java:5201) ~[sy_1986612332714328064.jar:3.1.3]
	at org.apache.hadoop.hive.conf.HiveConf.<init>(HiveConf.java:5112) ~[sy_1986612332714328064.jar:3.1.3]
	at org.apache.iceberg.hive.HiveClientPool.<init>(HiveClientPool.java:55) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.CachedClientPool.lambda$clientPool$0(CachedClientPool.java:96) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.lambda$doComputeIfAbsent$14(BoundedLocalCache.java:2406) ~[sy_1986723935774375936.jar:?]
	at java.util.concurrent.ConcurrentHashMap.compute(ConcurrentHashMap.java:1853) ~[?:1.8.0_412]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.doComputeIfAbsent(BoundedLocalCache.java:2404) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.computeIfAbsent(BoundedLocalCache.java:2387) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.LocalCache.computeIfAbsent(LocalCache.java:108) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.LocalManualCache.get(LocalManualCache.java:62) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.CachedClientPool.clientPool(CachedClientPool.java:96) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.CachedClientPool.run(CachedClientPool.java:122) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.HiveTableOperations.doRefresh(HiveTableOperations.java:147) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.BaseMetastoreTableOperations.refresh(BaseMetastoreTableOperations.java:90) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.BaseMetastoreTableOperations.current(BaseMetastoreTableOperations.java:73) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.BaseMetastoreCatalog$BaseMetastoreCatalogTableBuilder.create(BaseMetastoreCatalog.java:191) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.CachingCatalog$CachingTableBuilder.lambda$create$0(CachingCatalog.java:262) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.lambda$doComputeIfAbsent$14(BoundedLocalCache.java:2406) ~[sy_1986723935774375936.jar:?]
	at java.util.concurrent.ConcurrentHashMap.compute(ConcurrentHashMap.java:1853) ~[?:1.8.0_412]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.doComputeIfAbsent(BoundedLocalCache.java:2404) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.computeIfAbsent(BoundedLocalCache.java:2387) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.LocalCache.computeIfAbsent(LocalCache.java:108) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.LocalManualCache.get(LocalManualCache.java:62) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.CachingCatalog$CachingTableBuilder.create(CachingCatalog.java:258) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.catalog.Catalog.createTable(Catalog.java:75) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.flink.FlinkCatalog.createIcebergTable(FlinkCatalog.java:415) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.flink.FlinkCatalog.createTable(FlinkCatalog.java:395) ~[sy_1986723935774375936.jar:?]
	at org.apache.flink.table.catalog.CatalogManager.lambda$createTable$18(CatalogManager.java:957) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.catalog.CatalogManager.execute(CatalogManager.java:1290) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.catalog.CatalogManager.createTable(CatalogManager.java:946) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.operations.ddl.CreateTableOperation.execute(CreateTableOperation.java:84) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.api.internal.TableEnvironmentImpl.executeInternal(TableEnvironmentImpl.java:1092) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.api.internal.TableEnvironmentImpl.executeSql(TableEnvironmentImpl.java:735) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at com.isxcode.spark.plugin.flink.sql.execute.Job.main(Job.java:51) ~[flink-sql-execute-plugin.jar:?]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:1.8.0_412]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[?:1.8.0_412]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:1.8.0_412]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[?:1.8.0_412]
	at org.apache.flink.client.program.PackagedProgram.callMainMethod(PackagedProgram.java:355) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.program.PackagedProgram.invokeInteractiveModeForExecution(PackagedProgram.java:222) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.ClientUtils.executeProgram(ClientUtils.java:105) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.deployment.application.ApplicationDispatcherBootstrap.runApplicationEntryPoint(ApplicationDispatcherBootstrap.java:301) ~[flink-dist-1.18.1.jar:1.18.1]
	... 12 more
```

缺少依赖[commons-lang-2.6.jar下载](https://repo1.maven.org/maven2/commons-lang/commons-lang/2.6/commons-lang-2.6.jar)

#### 问题19

```log
Caused by: java.lang.ClassNotFoundException: org.apache.hadoop.hive.common.ValidWriteIdList
	at java.net.URLClassLoader.findClass(URLClassLoader.java:387) ~[?:1.8.0_412]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:418) ~[?:1.8.0_412]
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352) ~[?:1.8.0_412]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:351) ~[?:1.8.0_412]
	at java.lang.Class.forName0(Native Method) ~[?:1.8.0_412]
	at java.lang.Class.forName(Class.java:348) ~[?:1.8.0_412]
	at org.apache.hadoop.hive.metastore.utils.JavaUtils.getClass(JavaUtils.java:52) ~[sy_1986612371608109056.jar:3.1.3]
	at org.apache.hadoop.hive.metastore.RetryingMetaStoreClient.getProxy(RetryingMetaStoreClient.java:146) ~[sy_1986612371608109056.jar:3.1.3]
	at org.apache.hadoop.hive.metastore.RetryingMetaStoreClient.getProxy(RetryingMetaStoreClient.java:119) ~[sy_1986612371608109056.jar:3.1.3]
	at org.apache.hadoop.hive.metastore.RetryingMetaStoreClient.getProxy(RetryingMetaStoreClient.java:112) ~[sy_1986612371608109056.jar:3.1.3]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:1.8.0_412]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[?:1.8.0_412]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:1.8.0_412]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[?:1.8.0_412]
	at org.apache.iceberg.common.DynMethods$UnboundMethod.invokeChecked(DynMethods.java:62) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.common.DynMethods$UnboundMethod.invoke(DynMethods.java:74) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.common.DynMethods$StaticMethod.invoke(DynMethods.java:187) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.HiveClientPool.newClient(HiveClientPool.java:63) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.HiveClientPool.newClient(HiveClientPool.java:34) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.ClientPoolImpl.get(ClientPoolImpl.java:143) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.ClientPoolImpl.run(ClientPoolImpl.java:70) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.ClientPoolImpl.run(ClientPoolImpl.java:65) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.CachedClientPool.run(CachedClientPool.java:122) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.hive.HiveTableOperations.doRefresh(HiveTableOperations.java:147) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.BaseMetastoreTableOperations.refresh(BaseMetastoreTableOperations.java:90) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.BaseMetastoreTableOperations.current(BaseMetastoreTableOperations.java:73) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.BaseMetastoreCatalog$BaseMetastoreCatalogTableBuilder.create(BaseMetastoreCatalog.java:191) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.CachingCatalog$CachingTableBuilder.lambda$create$0(CachingCatalog.java:262) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.lambda$doComputeIfAbsent$14(BoundedLocalCache.java:2406) ~[sy_1986723935774375936.jar:?]
	at java.util.concurrent.ConcurrentHashMap.compute(ConcurrentHashMap.java:1853) ~[?:1.8.0_412]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.doComputeIfAbsent(BoundedLocalCache.java:2404) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.BoundedLocalCache.computeIfAbsent(BoundedLocalCache.java:2387) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.LocalCache.computeIfAbsent(LocalCache.java:108) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.shaded.com.github.benmanes.caffeine.cache.LocalManualCache.get(LocalManualCache.java:62) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.CachingCatalog$CachingTableBuilder.create(CachingCatalog.java:258) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.catalog.Catalog.createTable(Catalog.java:75) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.flink.FlinkCatalog.createIcebergTable(FlinkCatalog.java:415) ~[sy_1986723935774375936.jar:?]
	at org.apache.iceberg.flink.FlinkCatalog.createTable(FlinkCatalog.java:395) ~[sy_1986723935774375936.jar:?]
	at org.apache.flink.table.catalog.CatalogManager.lambda$createTable$18(CatalogManager.java:957) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.catalog.CatalogManager.execute(CatalogManager.java:1290) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.catalog.CatalogManager.createTable(CatalogManager.java:946) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.operations.ddl.CreateTableOperation.execute(CreateTableOperation.java:84) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.api.internal.TableEnvironmentImpl.executeInternal(TableEnvironmentImpl.java:1092) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at org.apache.flink.table.api.internal.TableEnvironmentImpl.executeSql(TableEnvironmentImpl.java:735) ~[flink-table-api-java-uber-1.18.1.jar:1.18.1]
	at com.isxcode.spark.plugin.flink.sql.execute.Job.main(Job.java:51) ~[flink-sql-execute-plugin.jar:?]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:1.8.0_412]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[?:1.8.0_412]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:1.8.0_412]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[?:1.8.0_412]
	at org.apache.flink.client.program.PackagedProgram.callMainMethod(PackagedProgram.java:355) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.program.PackagedProgram.invokeInteractiveModeForExecution(PackagedProgram.java:222) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.ClientUtils.executeProgram(ClientUtils.java:105) ~[flink-dist-1.18.1.jar:1.18.1]
	at org.apache.flink.client.deployment.application.ApplicationDispatcherBootstrap.runApplicationEntryPoint(ApplicationDispatcherBootstrap.java:301) ~[flink-dist-1.18.1.jar:1.18.1]
	... 12 more
```

缺少依赖[hive-exec-3.1.3.jar下载](https://repo1.maven.org/maven2/org/apache/hive/hive-exec/3.1.3/hive-exec-3.1.3.jar)