---
title: "Hadoop部署"
---

## 离线安装hadoop`3.3.5`(HA)集群

#### 部署环境 `centOS 7.9` `64位`

> 服务器个数最好是奇数，isxcode-main1和isxcode-main2为主节点，双节点多活。

| hostname      | 公网ip        | 内网ip         | CPU | 内存 |
| ------------- | ------------- | -------------- | --- | ---- |
| isxcode-main1 | 39.98.65.13   | 172.16.215.101 | 8C  | 16GB |
| isxcode-main2 | 39.98.218.189 | 172.16.215.100 | 8C  | 16GB |
| isxcode-node1 | 39.98.210.253 | 172.16.215.97  | 4C  | 8GB  |
| isxcode-node2 | 39.98.213.46  | 172.16.215.96  | 4C  | 8GB  |
| isxcode-node3 | 39.98.210.230 | 172.16.215.95  | 4C  | 8GB  |


#### 下载hadoop安装包（一台下载即可）

```bash
cd /tmp
nohup wget https://archive.apache.org/dist/hadoop/common/hadoop-3.3.5/hadoop-3.3.5.tar.gz >> download_hadoop.log 2>&1 &
tail -f download_hadoop.log
```

#### 分发安装包

```bash
scp /tmp/hadoop-3.3.5.tar.gz ispong@isxcode-main2:/tmp/
scp /tmp/hadoop-3.3.5.tar.gz ispong@isxcode-node1:/tmp/
scp /tmp/hadoop-3.3.5.tar.gz ispong@isxcode-node2:/tmp/
scp /tmp/hadoop-3.3.5.tar.gz ispong@isxcode-node3:/tmp/
```

#### 解压并安装hadoop（每台都要执行）

```bash
sudo mkdir -p /data/hadoop
sudo chown -R ispong:ispong /data/hadoop
```

```bash
tar -vzxf /tmp/hadoop-3.3.5.tar.gz -C /data/hadoop/
sudo ln -s /data/hadoop/hadoop-3.3.5 /opt/hadoop
```

```bash
sudo tee -a /etc/profile <<-'EOF'
export HADOOP_HOME=/opt/hadoop
export HADOOP_CLASSPATH=$HADOOP_HOME/etc/hadoop:$HADOOP_HOME/share/hadoop/common/lib/*:$HADOOP_HOME/share/hadoop/common/*:$HADOOP_HOME/share/hadoop/hdfs:$HADOOP_HOME/share/hadoop/hdfs/lib/*:$HADOOP_HOME/share/hadoop/hdfs/*:$HADOOP_HOME/share/hadoop/mapreduce/lib/*:$HADOOP_HOME/share/hadoop/mapreduce/*:$HADOOP_HOME/share/hadoop/yarn:$HADOOP_HOME/share/hadoop/yarn/lib/*:$HADOOP_HOME/share/hadoop/yarn/* 
export YARN_CONF_DIR=$HADOOP_HOME/etc/hadoop
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export PATH=$PATH:$HADOOP_HOME/bin 
export PATH=$PATH:$HADOOP_HOME/sbin 
EOF
source /etc/profile
```

#### 配置hadoop-env.sh (最后分发)

```bash
vim /opt/hadoop/etc/hadoop/hadoop-env.sh
```

> 直接新增

```bash
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
export HDFS_NAMENODE_USER=ispong
export HDFS_DATANODE_USER=ispong
export HDFS_SECONDARYNAMENODE_USER=ispong
export YARN_RESOURCEMANAGER_USER=ispong
export YARN_NODEMANAGER_USER=ispong
export HDFS_ZKFC_USER=ispong
export HDFS_JOURNALNODE_USER=ispong
```

#### 配置datanode结点 (最后分发)

> 根据实际情况分配

```bash
vim /opt/hadoop/etc/hadoop/workers
```

```bash
isxcode-main1
isxcode-main2
isxcode-node1
isxcode-node2
isxcode-node3
```

#### 配置core-site.xml (最后分发)

> 注意：名称不要使用`_`

```bash
vim /opt/hadoop/etc/hadoop/core-site.xml
```

```xml
<configuration>

    <!-- 配置临时文件位置 -->
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/data/hadoop/tmp</value>
    </property>
    
    <!-- 配置服务器默认hdfs端口 -->
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://isxcode-hdfs</value>
    </property>

    <!-- 配置zk结点 -->
    <property>
        <name>ha.zookeeper.quorum</name>
        <value>isxcode-main1:2181,isxcode-main2:2181,isxcode-node1:2181,isxcode-node2:2181,isxcode-node3:2181</value>
    </property>

      <!-- 设置${user}ispong用户免权限 -->
    <property>
        <!-- <name>hadoop.proxyuser.${user}.hosts</name> -->
        <name>hadoop.proxyuser.ispong.hosts</name>
        <value>*</value>
    </property>

    <!-- 设置${user}ispong -->
    <property>
        <!-- <name>hadoop.proxyuser.${user}.groups</name> -->
        <name>hadoop.proxyuser.ispong.groups</name>
        <value>*</value>
    </property>

</configuration>
```

#### 配置hdfs-site.xml (最后分发)

> 注意：名称不要使用`_`

```bash
vim /opt/hadoop/etc/hadoop/hdfs-site.xml
```

```xml
<configuration>

    <!-- namenode文件存储位置 -->
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>/data/hadoop/name</value>
    </property>

    <!-- datanode文件存储位置 -->
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>/data/hadoop/data</value>
    </property>

    <!-- 默认切片个数 -->
    <property>
        <name>dfs.replication</name>
        <value>3</value>
    </property>
    
    <!-- 给nameservice起个名字,不要使用下划线 -->
    <!-- hdfs的访问路径，要与core-site.xml保持一致 -->
    <property>
        <name>dfs.nameservices</name> 
        <value>isxcode-hdfs</value>
    </property>

    <!-- 配置三个备用namenode结点，给他们起个名字 -->
    <property>
        <name>dfs.ha.namenodes.isxcode-hdfs</name>
        <value>hdfs-main1,hdfs-main2</value>
    </property>

    <!-- 配置namenode1界面访问端口号 -->
    <property>
        <name>dfs.namenode.rpc-address.isxcode-hdfs.hdfs-main1</name>
        <value>isxcode-main1:8020</value>
    </property>
    <property>
        <name>dfs.namenode.http-address.isxcode-hdfs.hdfs-main1</name>
        <value>isxcode-main1:9870</value>
    </property>

    <!-- 配置namenode2界面访问端口号 -->
    <property>
        <name>dfs.namenode.rpc-address.isxcode-hdfs.hdfs-main2</name>
        <value>isxcode-main2:8020</value>
    </property>
    <property>
        <name>dfs.namenode.http-address.isxcode-hdfs.hdfs-main2</name>
        <value>isxcode-main2:9870</value>
    </property>

    <property>
        <name>dfs.namenode.shared.edits.dir</name>
        <value>qjournal://isxcode-main1:8485;isxcode-main2:8485/isxcode-hdfs</value>
    </property>
  
    <property>
        <name>dfs.client.failover.proxy.provider.isxcode-hdfs</name>
        <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
    </property>
    
    <property>
        <name>dfs.ha.fencing.methods</name>
        <value>sshfence</value>
    </property>
 
    <!-- 配置用户ssh私钥 -->
    <property>
        <name>dfs.ha.fencing.ssh.private-key-files</name>
        <value>/home/ispong/.ssh/id_rsa</value>
    </property>
    
    <!-- journal目录 -->
    <property>
        <name>dfs.journalnode.edits.dir</name>
        <value>/data/hadoop/journal</value>
    </property>

    <property>
        <name>dfs.ha.automatic-failover.enabled</name>
        <value>true</value>
    </property>

    <property>
        <name>dfs.ha.fencing.ssh.connect-timeout</name>
        <value>30000</value>
    </property>

  
</configuration>
```

#### 配置yarn-site.xml (最后分发)

> 注意：名称不要使用`_`
> yarn.log.server.url，配置需要自定义，分发完记得修改

```bash
vim /opt/hadoop/etc/hadoop/yarn-site.xml
```

```xml
<configuration>

    <!-- 配置洗牌引擎 -->
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>

    <!-- 开启ha模式 -->
    <property>
        <name>yarn.resourcemanager.ha.enabled</name>
        <value>true</value>
    </property>

    <!-- 给yarn的集群起个名字 -->
    <property>
        <name>yarn.resourcemanager.cluster-id</name>
        <value>isxcode-yarn</value>
    </property>

    <!-- 给ha模式三个备用rm起名字 -->
    <property>
        <name>yarn.resourcemanager.ha.rm-ids</name>
        <value>rm1,rm2</value>
    </property>

    <!-- 配置rm1属性 -->
    <property>
        <name>yarn.resourcemanager.hostname.rm1</name>
        <value>isxcode-main1</value>
    </property>
    
    <property>
        <name>yarn.resourcemanager.webapp.address.rm1</name>
        <value>isxcode-main1:8088</value>
    </property>

    <property>
        <name>yarn.resourcemanager.scheduler.address.rm1</name>
        <value>isxcode-main1:8030</value>
    </property>

    <property>
        <name>yarn.resourcemanager.address.rm1</name>
        <value>isxcode-main1:8032</value>
    </property>

    <!-- 配置rm2属性 -->
    <property>
        <name>yarn.resourcemanager.hostname.rm2</name>
        <value>isxcode-main2</value>
    </property>
    
    <property>
        <name>yarn.resourcemanager.webapp.address.rm2</name>
        <value>isxcode-main2:8088</value>
    </property>

    <property>
        <name>yarn.resourcemanager.scheduler.address.rm2</name>
        <value>isxcode-main2:8030</value>
    </property>

    <!-- 只有主节点才能启动 -->
    <property>
        <name>yarn.resourcemanager.address.rm2</name>
        <value>isxcode-main2:8032</value>
    </property>

    <!-- 配置zk集群 -->
    <property>
        <name>hadoop.zk.address</name>
        <value>isxcode-main1:2181,isxcode-main2:2181,isxcode-node1:2181,isxcode-node2:2181,isxcode-node3:2181</value>
    </property>

    <!-- 开启日志 -->
    <property>
        <name>yarn.log-aggregation-enable</name>
        <value>true</value>
    </property>
    
    <!-- 开启聚合日志，保存日志 -->
    <property>
        <name>yarn.log-aggregation-enable</name>
        <value>true</value>
    </property>

    <!-- 启动日志严控后，一定要配置url，否则ui无法查看日志 -->
    <!-- 注意：地址需要针对服务器填写自己的hostname -->
    <property>
        <name>yarn.log.server.url</name>
        <value>http://isxcode-main1:19888/jobhistory/logs</value>
    </property>

    <!-- 不配置 hive的mapreduce无法执行 -->
    <property>
        <name>yarn.nodemanager.env-whitelist</name>
        <value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,YARN_CONF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_HOME,PATH,LANG,TZ,HADOOP_MAPRED_HOME</value>
    </property>
    
</configuration>
```

#### 配置mapred-site.xml (最后分发)

```bash
vim /opt/hadoop/etc/hadoop/mapred-site.xml
```

```xml
<configuration>

    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>

    <property>
        <name>mapreduce.jobhistory.webapp.address</name>
        <value>0.0.0.0:19888</value>
    </property>
    
    <property>
        <name>mapreduce.jobhistory.address</name>
        <value>0.0.0.0:10020</value>
    </property>

</configuration>
```

#### 分发配置文件

```bash
scp -r /opt/hadoop/etc/hadoop ispong@isxcode-main2:/opt/hadoop/etc/
scp -r /opt/hadoop/etc/hadoop ispong@isxcode-node1:/opt/hadoop/etc/
scp -r /opt/hadoop/etc/hadoop ispong@isxcode-node2:/opt/hadoop/etc/
scp -r /opt/hadoop/etc/hadoop ispong@isxcode-node3:/opt/hadoop/etc/
```

#### 格式化namenode

> 切记注意启动顺序

```bash
#  isxcode-main1和isxcode-main2提前启动journalnode
hdfs --daemon start journalnode

# 检查是否启动成功
netstat -ntpl | grep 8485

# 格式化namenode isxcode-main1节点执行
hdfs namenode -format
hdfs --daemon start namenode

# 备用节点isxcode-main2执行
hdfs namenode -bootstrapStandby
# 2021-10-18 17:18:04,614 INFO common.Storage: Storage directory /data/hadoop/name has been successfully formatted.

# 格式化zk
# isxcode-main1节点执行
hdfs zkfc -formatZK
# 2021-10-18 17:17:27,754 INFO ha.ActiveStandbyElector: Successfully created /hadoop-ha/ispong_cluster in ZK.

# isxcode-main1节点执行 
# 不可以直接执行stop-all.sh，与spark集群命令冲突
bash /opt/hadoop/sbin/stop-all.sh
```

#### 配置hadoop集群自启

#### namenode

> isxcode-main1和isxcode-main2主节点配置

```bash
sudo vim /usr/lib/systemd/system/hdfs_namenode.service
```

```bash
[Unit]
Description=Hadoop Namenode Service
After=zookeeper.service

[Service]
Environment=JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
Type=forking
WorkingDirectory=/data/hadoop
PermissionsStartOnly=true
ExecStartPre=/bin/sleep 10
ExecStart=/opt/hadoop/bin/hdfs --daemon start namenode
ExecStop=/opt/hadoop/bin/hdfs --daemon stop namenode
ExecReload=/opt/hadoop/bin/hdfs --daemon restart namenode
KillMode=none
Restart=always
User=ispong
Group=ispong

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable hdfs_namenode
```

#### datanode

> 所有datanode服务器配置

```bash
sudo vim /usr/lib/systemd/system/hdfs_datanode.service
```

```bash
[Unit]
Description=Hadoop Datanode Service
After=zookeeper.service

[Service]
Environment=JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
Type=forking
WorkingDirectory=/data/hadoop
PermissionsStartOnly=true
ExecStartPre=/bin/sleep 10
ExecStart=/opt/hadoop/bin/hdfs --daemon start datanode
ExecStop=/opt/hadoop/bin/hdfs --daemon stop datanode
ExecReload=/opt/hadoop/bin/hdfs --daemon restart datanode
KillMode=none
Restart=always
User=ispong
Group=ispong

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable hdfs_datanode
```

#### journalnode

> isxcode-main1和isxcode-main2主节点配置

```bash
sudo vim /usr/lib/systemd/system/hdfs_journalnode.service
```

```bash
[Unit]
Description=Hadoop Journalnode Service
After=zookeeper.service

[Service]
Environment=JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
Type=forking
WorkingDirectory=/data/hadoop
PermissionsStartOnly=true
ExecStartPre=/bin/sleep 10
ExecStart=/opt/hadoop/bin/hdfs --daemon start journalnode
ExecStop=/opt/hadoop/bin/hdfs --daemon stop journalnode
ExecReload=/opt/hadoop/bin/hdfs --daemon restart journalnode
KillMode=none
Restart=always
User=ispong
Group=ispong

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable hdfs_journalnode
```

#### zkfc

> isxcode-main1和isxcode-main2主节点配置

```bash
sudo vim /usr/lib/systemd/system/hdfs_zkfc.service
```

```bash
[Unit]
Description=Hadoop Zkfc Service
After=zookeeper.service

[Service]
Environment=JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
Type=forking
WorkingDirectory=/data/hadoop
PermissionsStartOnly=true
ExecStartPre=/bin/sleep 10
ExecStart=/opt/hadoop/bin/hdfs --daemon start zkfc
ExecStop=/opt/hadoop/bin/hdfs --daemon stop zkfc
ExecReload=/opt/hadoop/bin/hdfs --daemon restart zkfc
KillMode=none
Restart=always
User=ispong
Group=ispong

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable hdfs_zkfc
```

#### yarn-resourcemanager

> isxcode-main1和isxcode-main2主节点配置

```bash
sudo vim /usr/lib/systemd/system/yarn_resourcemanager.service
```

```bash
[Unit]
Description=Yarn Resourcemanager Service
After=zookeeper.service

[Service]
Environment=JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
Type=forking
WorkingDirectory=/data/hadoop
PermissionsStartOnly=true
ExecStartPre=/bin/sleep 10
ExecStart=/opt/hadoop/bin/yarn --daemon start resourcemanager
ExecStop=/opt/hadoop/bin/yarn --daemon stop resourcemanager
ExecReload=/opt/hadoop/bin/yarn --daemon restart resourcemanager
KillMode=none
Restart=always
User=ispong
Group=ispong

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable yarn_resourcemanager
```

#### yarn-nodemanager

> 所有的nodemanager节点都要配置

```bash
sudo vim /usr/lib/systemd/system/yarn_nodemanager.service
```

```bash
[Unit]
Description=Yarn Nodemanager Service
After=zookeeper.service

[Service]
Environment=JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
Type=forking
WorkingDirectory=/data/hadoop
PermissionsStartOnly=true
ExecStartPre=/bin/sleep 10
ExecStart=/opt/hadoop/bin/yarn --daemon start nodemanager
ExecStop=/opt/hadoop/bin/yarn --daemon stop nodemanager
ExecReload=/opt/hadoop/bin/yarn --daemon restart nodemanager
KillMode=none
Restart=always
User=ispong
Group=ispong

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable yarn_nodemanager
```

#### jobhistory

> 所有节点都要配置

```bash
sudo vim /usr/lib/systemd/system/mapred_historyserver.service
```

```bash
[Unit]
Description=Mapred Historyserver Service
After=zookeeper.service

[Service]
Environment=JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
Type=forking
WorkingDirectory=/data/hadoop
PermissionsStartOnly=true
ExecStartPre=/bin/sleep 10
ExecStart=/opt/hadoop/bin/mapred --daemon start historyserver 
ExecStop=/opt/hadoop/bin/mapred --daemon stop historyserver
ExecReload=/opt/hadoop/bin/mapred --daemon restart historyserver
KillMode=none
Restart=always
User=ispong
Group=ispong

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable mapred_historyserver
```

#### 检测集群

```bash
# 查看进程
jps

# 主节点
1457 JournalNode
1411 NodeManager
2355 Jps
1463 NameNode
1400 ResourceManager
1465 DataNode
1404 JobHistoryServer
1996 DFSZKFailoverController
943 QuorumPeerMain

# 从节点
1299 NodeManager
1332 JobHistoryServer
1717 Jps
951 QuorumPeerMain
1325 DataNode

# 查看日志
tail -f /opt/hadoop/logs/hadoop-root-namenode-master.log 
```

#### Hdfs

> 获取主节点状态

```bash
hdfs haadmin -getServiceState hdfs-main1
hdfs haadmin -getServiceState hdfs-main2
```

> 两个地址都可以访问

- main1-namenode: http://39.98.65.13:9870 
- main2-namenode: http://39.98.218.189:9870 
  
#### Yarn

```bash
yarn rmadmin -getServiceState rm1
yarn rmadmin -getServiceState rm2
```

> 注意：只有激活状态的节点才可以访问，另一个节点会自动跳转

- main1-resourceManager: http://39.98.65.13:8088 
- main2-resourceManager: http://39.98.218.189:8088

#### JobHistory

> 所有节点都可以访问

- main1: http://39.98.65.13:19888 
- main2: http://39.98.218.189:19888 
- node1: http://39.98.210.253:19888 
- node2: http://39.98.213.46:19888 
- node3: http://39.98.210.230:19888

#### 相关文档

- [hadoop website](http://hadoop.apache.org/) 
- [core-site.xml官方说明文档](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/core-default.xml) 
- [hdfs-site.xml官方配置说明](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/hdfs-default.xml)