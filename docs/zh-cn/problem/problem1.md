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