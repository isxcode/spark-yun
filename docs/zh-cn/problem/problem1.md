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

```log
因为k8s容器内没有做域名映射,
在服务器中创建pod-init.yml文件，并配置sparksql作业
```

```json
{
  "spark.kubernetes.driver.podTemplateFile":"/home/ispong/pod-init.yaml",
  "spark.kubernetes.executor.podTemplateFile":"/home/ispong/pod-init.yaml"
}
```

```bash
vim /home/ispong/pod-init.yml
```

```yml
apiVersion: v1
kind: Pod
metadata:
  name: host-pod
spec:
  restartPolicy: never
  securityContext:
    privileged: true
    runAsUser: 1000 
    runAsGroup: 1001
  hostAliases:  # 域名映射
    - ip: "172.16.215.105"
      hostnames:
        - "isxcode"
  volumes:
    - name: users
      hostPath:
        path: /etc/passwd
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