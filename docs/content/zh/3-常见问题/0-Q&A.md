---
title: "Q&A"
---

#### 问题1

> 本地spark和hive版本不兼容问题

```log
Caused by: org.apache.thrift.TApplicationException: Invalid method name: 'get_table_req'
	at org.apache.thrift.TServiceClient.receiveBase(TServiceClient.java:79)
	at org.apache.hadoop.hive.metastore.api.ThriftHiveMetastore$Client.recv_get_table_req(ThriftHiveMetastore.java:1567)
	at org.apache.hadoop.hive.metastore.api.ThriftHiveMetastore$Client.get_table_req(ThriftHiveMetastore.java:1554)
	at org.apache.hadoop.hive.metastore.HiveMetaStoreClient.getTable(HiveMetaStoreClient.java:1350)
	at org.apache.hadoop.hive.ql.metadata.SessionHiveMetaStoreClient.getTable(SessionHiveMetaStoreClient.java:127)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.hadoop.hive.metastore.RetryingMetaStoreClient.invoke(RetryingMetaStoreClient.java:173)
	at com.sun.proxy.$Proxy33.getTable(Unknown Source)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.hadoop.hive.metastore.HiveMetaStoreClient$SynchronizedHandler.invoke(HiveMetaStoreClient.java:2336)
	at com.sun.proxy.$Proxy33.getTable(Unknown Source)
	at org.apache.hadoop.hive.ql.metadata.Hive.getTable(Hive.java:1274)
	... 82 more
```

###### 解决方案

```txt
在sparksql作业配置的时候添加配，指定本地的spark地址路径
```

```json
{
  "spark.sql.hive.metastore.version": "2.1.1",
  "spark.sql.hive.metastore.jars": "/data/cloudera/parcels/CDH-6.2.0-1.cdh6.2.0.p0.967373/lib/hive/lib/*"  
}
```

#### 问题2

> k8s 启动作业，无法访问域名

```log
Caused by: java.lang.IllegalArgumentException: java.net.UnknownHostException: isxcode
	at org.apache.hadoop.security.SecurityUtil.buildTokenService(SecurityUtil.java:447)
	at org.apache.hadoop.hdfs.NameNodeProxiesClient.createProxyWithClientProtocol(NameNodeProxiesClient.java:131)
	at org.apache.hadoop.hdfs.DFSClient.<init>(DFSClient.java:355)
	at org.apache.hadoop.hdfs.DFSClient.<init>(DFSClient.java:289)
	at org.apache.hadoop.hdfs.DistributedFileSystem.initialize(DistributedFileSystem.java:176)
	at org.apache.hadoop.fs.FileSystem.createFileSystem(FileSystem.java:3303)
	at org.apache.hadoop.fs.FileSystem.access$200(FileSystem.java:124)
	at org.apache.hadoop.fs.FileSystem$Cache.getInternal(FileSystem.java:3352)
	at org.apache.hadoop.fs.FileSystem$Cache.get(FileSystem.java:3320)
	at org.apache.hadoop.fs.FileSystem.get(FileSystem.java:479)
	at org.apache.hadoop.fs.FileSystem.get(FileSystem.java:227)
	at org.apache.hadoop.mapred.JobConf.getWorkingDirectory(JobConf.java:672)
	at org.apache.hadoop.mapred.FileInputFormat.setInputPaths(FileInputFormat.java:463)
	at org.apache.spark.sql.hive.HadoopTableReader$.initializeLocalJobConfFunc(TableReader.scala:396)
	at org.apache.spark.sql.hive.HadoopTableReader.$anonfun$createOldHadoopRDD$1(TableReader.scala:314)
	at org.apache.spark.sql.hive.HadoopTableReader.$anonfun$createOldHadoopRDD$1$adapted(TableReader.scala:314)
	at org.apache.spark.rdd.HadoopRDD.$anonfun$getJobConf$8(HadoopRDD.scala:181)
	at org.apache.spark.rdd.HadoopRDD.$anonfun$getJobConf$8$adapted(HadoopRDD.scala:181)
	at scala.Option.foreach(Option.scala:407)
	at org.apache.spark.rdd.HadoopRDD.$anonfun$getJobConf$6(HadoopRDD.scala:181)
	at scala.Option.getOrElse(Option.scala:189)
	at org.apache.spark.rdd.HadoopRDD.getJobConf(HadoopRDD.scala:178)
	at org.apache.spark.rdd.HadoopRDD$$anon$1.<init>(HadoopRDD.scala:247)
	at org.apache.spark.rdd.HadoopRDD.compute(HadoopRDD.scala:243)
	at org.apache.spark.rdd.HadoopRDD.compute(HadoopRDD.scala:96)
	at org.apache.spark.rdd.RDD.computeOrReadCheckpoint(RDD.scala:373)
	at org.apache.spark.rdd.RDD.iterator(RDD.scala:337)
	at org.apache.spark.rdd.MapPartitionsRDD.compute(MapPartitionsRDD.scala:52)
	at org.apache.spark.rdd.RDD.computeOrReadCheckpoint(RDD.scala:373)
	at org.apache.spark.rdd.RDD.iterator(RDD.scala:337)
	at org.apache.spark.rdd.MapPartitionsRDD.compute(MapPartitionsRDD.scala:52)
	at org.apache.spark.rdd.RDD.computeOrReadCheckpoint(RDD.scala:373)
	at org.apache.spark.rdd.RDD.iterator(RDD.scala:337)
	at org.apache.spark.rdd.UnionRDD.compute(UnionRDD.scala:106)
	at org.apache.spark.rdd.RDD.computeOrReadCheckpoint(RDD.scala:373)
	at org.apache.spark.rdd.RDD.iterator(RDD.scala:337)
	at org.apache.spark.rdd.MapPartitionsRDD.compute(MapPartitionsRDD.scala:52)
	at org.apache.spark.rdd.RDD.computeOrReadCheckpoint(RDD.scala:373)
	at org.apache.spark.rdd.RDD.iterator(RDD.scala:337)
	at org.apache.spark.rdd.MapPartitionsRDD.compute(MapPartitionsRDD.scala:52)
	at org.apache.spark.rdd.RDD.computeOrReadCheckpoint(RDD.scala:373)
	at org.apache.spark.rdd.RDD.iterator(RDD.scala:337)
	at org.apache.spark.scheduler.ResultTask.runTask(ResultTask.scala:90)
	at org.apache.spark.scheduler.Task.run(Task.scala:131)
	at org.apache.spark.executor.Executor$TaskRunner.$anonfun$run$3(Executor.scala:498)
	at org.apache.spark.util.Utils$.tryWithSafeFinally(Utils.scala:1439)
	at org.apache.spark.executor.Executor$TaskRunner.run(Executor.scala:501)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
	at java.base/java.lang.Thread.run(Unknown Source)
Caused by: java.net.UnknownHostException: isxcode
	... 50 more
```

##### 解决方案

```json
{
  "qing.host1.name": "host1",
  "qing.host1.value": "12.0.0.1",
  "qing.host2.name": "host2",
  "qing.host2.value": "12.0.0.1",
  "qing.host3.name": "host3",
  "qing.host3.value": "12.0.0.1"
}
```

#### 问题3

> 无法访问本地hive数据库

```json
{
  "hive.metastore.uris": "thrift://isxcode:9083"
}
```

#### 问题4

> standalone 执行sparksql报错

```log
2023-06-16 18:05:12,769 WARN server.TransportChannelHandler: Exception in connection from /172.16.215.105:7077
java.io.InvalidClassException: org.apache.spark.rpc.RpcEndpointRef; local class incompatible: stream classdesc serialVersionUID = -2184441956866814275, local class serialVersionUID = -3992716321891270988
	at java.io.ObjectStreamClass.initNonProxy(ObjectStreamClass.java:699)
	at java.io.ObjectInputStream.readNonProxyDesc(ObjectInputStream.java:2004)
	at java.io.ObjectInputStream.readClassDesc(ObjectInputStream.java:1851)
	at java.io.ObjectInputStream.readNonProxyDesc(ObjectInputStream.java:2004)
	at java.io.ObjectInputStream.readClassDesc(ObjectInputStream.java:1851)
	at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2185)
	at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1668)
	at java.io.ObjectInputStream.defaultReadFields(ObjectInputStream.java:2430)
	at java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2354)
	at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2212)
	at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1668)
	at java.io.ObjectInputStream.readObject(ObjectInputStream.java:502)
	at java.io.ObjectInputStream.readObject(ObjectInputStream.java:460)
	at org.apache.spark.serializer.JavaDeserializationStream.readObject(JavaSerializer.scala:76)
	at org.apache.spark.serializer.JavaSerializerInstance.deserialize(JavaSerializer.scala:109)
	at org.apache.spark.rpc.netty.NettyRpcEnv.$anonfun$deserialize$2(NettyRpcEnv.scala:299)
	at scala.util.DynamicVariable.withValue(DynamicVariable.scala:62)
	at org.apache.spark.rpc.netty.NettyRpcEnv.deserialize(NettyRpcEnv.scala:352)
	at org.apache.spark.rpc.netty.NettyRpcEnv.$anonfun$deserialize$1(NettyRpcEnv.scala:298)
	at scala.util.DynamicVariable.withValue(DynamicVariable.scala:62)
	at org.apache.spark.rpc.netty.NettyRpcEnv.deserialize(NettyRpcEnv.scala:298)
	at org.apache.spark.rpc.netty.NettyRpcEnv.$anonfun$askAbortable$7(NettyRpcEnv.scala:246)
	at org.apache.spark.rpc.netty.NettyRpcEnv.$anonfun$askAbortable$7$adapted(NettyRpcEnv.scala:246)
	at org.apache.spark.rpc.netty.RpcOutboxMessage.onSuccess(Outbox.scala:90)
	at org.apache.spark.network.client.TransportResponseHandler.handle(TransportResponseHandler.java:194)
	at org.apache.spark.network.server.TransportChannelHandler.channelRead0(TransportChannelHandler.java:142)
	at org.apache.spark.network.server.TransportChannelHandler.channelRead0(TransportChannelHandler.java:53)
	at io.netty.channel.SimpleChannelInboundHandler.channelRead(SimpleChannelInboundHandler.java:99)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)
	at io.netty.handler.timeout.IdleStateHandler.channelRead(IdleStateHandler.java:286)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)
	at io.netty.handler.codec.MessageToMessageDecoder.channelRead(MessageToMessageDecoder.java:103)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)
	at org.apache.spark.network.util.TransportFrameDecoder.channelRead(TransportFrameDecoder.java:102)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)
	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1410)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
	at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:919)
	at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:163)
	at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:714)
	at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:650)
	at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:576)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:493)
	at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:989)
	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:750)
```

###### 解决方案

```text
这是因为spark的standalone集群版本，和spark-yun中spark-min中自带的版本冲突，需要将两边的版本保持一致
```

```bash
cp -r /opt/spark/* /home/ispong/spark-yun/spark-min/
rm /home/ispong/spark-yun/spark-min/conf/spark-env.sh
rm /home/ispong/spark-yun/spark-min/conf/spark-defaults.conf
```

#### 问题5

> 提交standalone作业报错

```log
Exception in thread "main" java.lang.NoSuchMethodError: org.apache.spark.network.util.JavaUtils.createDirectory(Ljava/lang/String;Ljava/lang/String;)Ljava/io/File;
	at org.apache.spark.util.Utils$.createDirectory(Utils.scala:323)
	at org.apache.spark.util.Utils$.createTempDir(Utils.scala:340)
	at org.apache.spark.util.Utils$.createTempDir(Utils.scala:331)
	at org.apache.spark.deploy.SparkSubmit.prepareSubmitEnvironment(SparkSubmit.scala:370)
	at org.apache.spark.deploy.SparkSubmit.org$apache$spark$deploy$SparkSubmit$$runMain(SparkSubmit.scala:955)
	at org.apache.spark.deploy.SparkSubmit.doRunMain$1(SparkSubmit.scala:192)
	at org.apache.spark.deploy.SparkSubmit.submit(SparkSubmit.scala:215)
	at org.apache.spark.deploy.SparkSubmit.doSubmit(SparkSubmit.scala:91)
	at org.apache.spark.deploy.SparkSubmit$$anon$2.doSubmit(SparkSubmit.scala:1111)
	at org.apache.spark.deploy.SparkSubmit$.main(SparkSubmit.scala:1120)
	at org.apache.spark.deploy.SparkSubmit.main(SparkSubmit.scala)
```

###### 解决方案

> spark-min与本地的spark版本不一致

#### 问题6

> k8s容器中默认的用户，没有操作hdfs的权限

```log
Caused by: org.apache.hadoop.ipc.RemoteException(org.apache.hadoop.security.AccessControlException): Permission denied: user=185, access=EXECUTE, inode="/tmp":zhiqingyun:supergroup:drwxrwx---
	at org.apache.hadoop.hdfs.server.namenode.FSPermissionChecker.check(FSPermissionChecker.java:506)
	at org.apache.hadoop.hdfs.server.namenode.FSPermissionChecker.checkTraverse(FSPermissionChecker.java:422)
	at org.apache.hadoop.hdfs.server.namenode.FSPermissionChecker.checkPermission(FSPermissionChecker.java:333)
	at org.apache.hadoop.hdfs.server.namenode.FSPermissionChecker.checkPermissionWithContext(FSPermissionChecker.java:370)
	at org.apache.hadoop.hdfs.server.namenode.FSPermissionChecker.checkPermission(FSPermissionChecker.java:240)
	at org.apache.hadoop.hdfs.server.namenode.FSPermissionChecker.checkTraverse(FSPermissionChecker.java:713)
	at org.apache.hadoop.hdfs.server.namenode.FSDirectory.checkTraverse(FSDirectory.java:1892)
	at org.apache.hadoop.hdfs.server.namenode.FSDirectory.checkTraverse(FSDirectory.java:1910)
	at org.apache.hadoop.hdfs.server.namenode.FSDirectory.resolvePath(FSDirectory.java:727)
	at org.apache.hadoop.hdfs.server.namenode.FSDirStatAndListingOp.getFileInfo(FSDirStatAndListingOp.java:112)
	at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.getFileInfo(FSNamesystem.java:3379)
	at org.apache.hadoop.hdfs.server.namenode.NameNodeRpcServer.getFileInfo(NameNodeRpcServer.java:1215)
	at org.apache.hadoop.hdfs.protocolPB.ClientNamenodeProtocolServerSideTranslatorPB.getFileInfo(ClientNamenodeProtocolServerSideTranslatorPB.java:1044)
	at org.apache.hadoop.hdfs.protocol.proto.ClientNamenodeProtocolProtos$ClientNamenodeProtocol$2.callBlockingMethod(ClientNamenodeProtocolProtos.java)
	at org.apache.hadoop.ipc.ProtobufRpcEngine2$Server$ProtoBufRpcInvoker.call(ProtobufRpcEngine2.java:621)
	at org.apache.hadoop.ipc.ProtobufRpcEngine2$Server$ProtoBufRpcInvoker.call(ProtobufRpcEngine2.java:589)
	at org.apache.hadoop.ipc.ProtobufRpcEngine2$Server$ProtoBufRpcInvoker.call(ProtobufRpcEngine2.java:573)
	at org.apache.hadoop.ipc.RPC$Server.call(RPC.java:1213)
	at org.apache.hadoop.ipc.Server$RpcCall.run(Server.java:1089)
	at org.apache.hadoop.ipc.Server$RpcCall.run(Server.java:1012)
	at java.security.AccessController.doPrivileged(Native Method)
	at javax.security.auth.Subject.doAs(Subject.java:422)
	at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1899)
	at org.apache.hadoop.ipc.Server$Handler.run(Server.java:3026)
```

##### 解决方案

```json
{
  "qing.hive.username": "ispong"
}
```

#### 问题7

> 数据同步动态分区报错，修改sparkConfig

![20240824181823](https://img.isxcode.com/picgo/20240824181823.png)

#### 问题8

> 数据同步添加静态值

![20240824181909](https://img.isxcode.com/picgo/20240824181909.png)


#### 问题9

> 麒麟V10系统兼容

```log
Caused by: java.io.IOException: ObjectIdentifier() -- data isn't an object ID (tag = 48)
        at sun.security.util.ObjectIdentifier.<init>(ObjectIdentifier.java:285) ~[na:1.8.0_272]
        at sun.security.util.DerInputStream.getOID(DerInputStream.java:320) ~[na:1.8.0_272]
        at com.sun.crypto.provider.PBES2Parameters.engineInit(PBES2Parameters.java:267) ~[sunjce_provider.jar:1.8.0_272]
        at java.security.AlgorithmParameters.init(AlgorithmParameters.java:293) ~[na:1.8.0_272]
        at global.namespace.truelicense.v4.V4Encryption.param(V4Encryption.java:64) ~[truelicense-v4-4.0.3.jar!/:na]
        at global.namespace.truelicense.v4.V4Encryption.lambda$input$1(V4Encryption.java:52) ~[truelicense-v4-4.0.3.jar!/:na]
        at global.namespace.fun.io.api.Socket.lambda$map$0(Socket.java:138) ~[fun-io-api-2.3.0.jar!/:2.3.0]
        at global.namespace.fun.io.api.Socket.lambda$map$0(Socket.java:136) ~[fun-io-api-2.3.0.jar!/:2.3.0]
        at global.namespace.fun.io.api.Socket.lambda$map$0(Socket.java:136) ~[fun-io-api-2.3.0.jar!/:2.3.0]
        at global.namespace.fun.io.api.Socket.lambda$map$0(Socket.java:136) ~[fun-io-api-2.3.0.jar!/:2.3.0]
        at global.namespace.truelicense.core.Filters$1.lambda$input$1(Filters.java:51) ~[truelicense-core-4.0.3.jar!/:na]
        at global.namespace.fun.io.api.Socket.apply(Socket.java:123) ~[fun-io-api-2.3.0.jar!/:2.3.0]
        at global.namespace.fun.io.jackson.JsonCodec$1.decode(JsonCodec.java:44) ~[fun-io-jackson-2.3.0.jar!/:2.3.0]
        at global.namespace.truelicense.core.TrueLicenseManagementContext$TrueLicenseManagerParameters$TrueLicenseManager.repositoryModel(TrueLicenseManagementContext.java:810) ~[truelicense-core-4.0.3.jar!/:na]
        at global.namespace.truelicense.core.TrueLicenseManagementContext$TrueLicenseManagerParameters$TrueLicenseManager.repositoryController(TrueLicenseManagementContext.java:804) ~[truelicense-core-4.0.3.jar!/:na]
        at global.namespace.truelicense.core.TrueLicenseManagementContext$TrueLicenseManagerParameters$TrueLicenseManager.authenticate(TrueLicenseManagementContext.java:800) ~[truelicense-core-4.0.3.jar!/:na]
        at global.namespace.truelicense.core.TrueLicenseManagementContext$TrueLicenseManagerParameters$CachingLicenseManager.authenticate(TrueLicenseManagementContext.java:673) ~[truelicense-core-4.0.3.jar!/:na]
        at global.namespace.truelicense.core.TrueLicenseManagementContext$TrueLicenseManagerParameters$TrueLicenseManager.decodeLicense(TrueLicenseManagementContext.java:796) ~[truelicense-core-4.0.3.jar!/:na]
        at global.namespace.truelicense.core.TrueLicenseManagementContext$TrueLicenseManagerParameters$TrueLicenseManager.lambda$install$1(TrueLicenseManagementContext.java:750) ~[truelicense-core-4.0.3.jar!/:na]
        at global.namespace.truelicense.core.TrueLicenseManagementContext.callChecked(TrueLicenseManagementContext.java:79) ~[truelicense-core-4.0.3.jar!/:na]
```

```text
麒麟系统本地不兼容java版本，本地的java版本不匹配，重新指定一个JAVA_HOME，推荐使用java1.8
```

```bash
vim /opt/zhiqingyun/conf/zhiqingyun-env.sh
```

```bash
export JAVA_HOME=/zulu/java
```

#### 问题10

```log
stop.sh:行2: $'\r'：未找到命令
stop.sh: 第 5 行：cd: $'/tmp/zhiqingyun/bin\r': 没有那个文件或目录
stop.sh:行5: $'exit\r'：未找到命令
stop.sh:行7: $'\r'：未找到命令
stop.sh:行21: 语法错误: 未预期的文件结尾
```

```text
使用windows打的包，无法直接部署到linux系统，需要对bash脚本中的/r做兼容
```

```bash
sed -i 's/\r//g' start.sh
```

#### 问题11

> 界面访问空白，日志报错

```log
2024-10-22 10:03:24.402  INFO 1776  --- [nio-8080-exec-6] o.apache.coyote.http11.Http11Processor   : Error parsing HTTP request header
 Note: further occurrences of HTTP request parsing errors will be logged at DEBUG level.

java.lang.IllegalArgumentException: Invalid character found in method name [0x160x030x010x060xa00x010x000x060x9c0x030x030xa80xd80xa7u0xd2(0x8dJ0x1c0xa9P0xe9'"G0x150xc8Jil0x940x860x14[0x100x810xdd^0x90bZ4 ]. HTTP method names must be tokens
        at org.apache.coyote.http11.Http11InputBuffer.parseRequestLine(Http11InputBuffer.java:419) ~[tomcat-embed-core-9.0.71.jar!/:na]
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:271) ~[tomcat-embed-core-9.0.71.jar!/:na]
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65) [tomcat-embed-core-9.0.71.jar!/:na]
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:891) [tomcat-embed-core-9.0.71.jar!/:na]
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1784) [tomcat-embed-core-9.0.71.jar!/:na]
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49) [tomcat-embed-core-9.0.71.jar!/:na]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191) [tomcat-embed-core-9.0.71.jar!/:na]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) [tomcat-embed-core-9.0.71.jar!/:na]
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) [tomcat-embed-core-9.0.71.jar!/:na]
        at java.lang.Thread.run(Thread.java:750) [na:1.8.0_412]
```

```text
使用 http://127.0.0.1:8080，检查使用http访问，或者配置ssl证书
```

#### 问题12

> 接口413

```text
Request failed with status code 413
```

```text
出现这种错误，一般是nginx配置错误导致的，添加一下配置
```

```nginx
http {
  client_max_body_size 50M;
}
```

#### 问题13

> 项目启动无法下载gradle

```log
手动下载gradle安装包，解压到gradle默认路径 <USER_HOME>/.gradle/wrapper/dists
```

![20241022103809](https://img.isxcode.com/picgo/20241022103809.png)
