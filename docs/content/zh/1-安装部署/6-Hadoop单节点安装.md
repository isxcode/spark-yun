---
title: "Hadoop单节点安装"
---

##### 阿里云服务器

- 8核心
- 32内存
- 40 + 200GB存储
- 开放端口号30177 40000~40050

##### 使用root用户挂载磁盘

- [挂载磁盘](https://ispong.isxcode.com/os/linux/linux%20%E7%A3%81%E7%9B%98/)

##### 创建安装用户

- [创建用户](https://ispong.isxcode.com/os/linux/linux%20%E7%94%A8%E6%88%B7/)

##### 使用root用户创建安装目录授权

```bash
mkdir -p /data/hadoop
chown -R zhiqingyun:zhiqingyun /data/hadoop 
```

##### 修改hostname

- [修改hostname](https://ispong.isxcode.com/os/linux/linux%20Host/)

##### 安装java环境

```bash
sudo yum install java-1.8.0-openjdk-devel java-1.8.0-openjdk -y
```

##### 下载hadoop

```bash
# 使用zhiqingyun用户下载解压hadoop
su zhiqingyun
cd /tmp
nohup wget https://archive.apache.org/dist/hadoop/common/hadoop-3.3.5/hadoop-3.3.5.tar.gz >> download_hadoop.log 2>&1 &
tail -f download_hadoop.log
tar -vzxf /tmp/hadoop-3.3.5.tar.gz -C /data/hadoop/

# 切换root添加软链接
su
ln -s /data/hadoop/hadoop-3.3.5 /opt/hadoop
```

##### 修改环境配置

```bash
su
tee -a /etc/profile <<-'EOF'
export HADOOP_HOME=/opt/hadoop
export HADOOP_CLASSPATH=$HADOOP_HOME/etc/hadoop:$HADOOP_HOME/share/hadoop/common/lib/*:$HADOOP_HOME/share/hadoop/common/*:$HADOOP_HOME/share/hadoop/hdfs:$HADOOP_HOME/share/hadoop/hdfs/lib/*:$HADOOP_HOME/share/hadoop/hdfs/*:$HADOOP_HOME/share/hadoop/mapreduce/lib/*:$HADOOP_HOME/share/hadoop/mapreduce/*:$HADOOP_HOME/share/hadoop/yarn:$HADOOP_HOME/share/hadoop/yarn/lib/*:$HADOOP_HOME/share/hadoop/yarn/* 
export YARN_CONF_DIR=$HADOOP_HOME/etc/hadoop
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export PATH=$PATH:$HADOOP_HOME/bin 
export PATH=$PATH:$HADOOP_HOME/sbin 
EOF
source /etc/profile
```

```bash
su zhiqingyun
tee -a /opt/hadoop/etc/hadoop/hadoop-env.sh <<-'EOF'
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
export HDFS_NAMENODE_USER=zhiqingyun
export HDFS_DATANODE_USER=zhiqingyun
export HDFS_SECONDARYNAMENODE_USER=zhiqingyun
export YARN_RESOURCEMANAGER_USER=zhiqingyun
export YARN_NODEMANAGER_USER=zhiqingyun
export HDFS_ZKFC_USER=zhiqingyun
export HDFS_JOURNALNODE_USER=zhiqingyun
EOF
```

##### 检测安装

```bash
hadoop version
```

##### 配置core-site.xml

```bash
vim /opt/hadoop/etc/hadoop/core-site.xml
```

```xml
<configuration>
  
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://isxcode:9000</value>
    </property>
  
    <property>
        <name>hadoop.proxyuser.zhiqingyun.hosts</name>
        <value>*</value>
    </property>
  
    <property>
        <name>hadoop.proxyuser.zhiqingyun.groups</name>
        <value>*</value>
    </property>

</configuration>
```

##### 配置hdfs-site.xml

```bash
mkdir -p /data/hadoop/name
mkdir -p /data/hadoop/data
vim /opt/hadoop/etc/hadoop/hdfs-site.xml
```

```xml
<configuration>
  
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>/data/hadoop/name</value>
    </property>
  
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>/data/hadoop/data</value>
    </property>
  
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
  
    <property>
        <name>dfs.namenode.http-address</name>
        <value>isxcode:40000</value> 
    </property>
  
    <property>
        <name>dfs.datanode.address</name>
        <value>isxcode:9866</value> 
    </property>

</configuration>
```

##### 格式化hdfs

```bash
hdfs namenode -format
```

##### 启动hdfs

```bash
start-dfs.sh
```

##### 访问接口

- http://47.92.152.18:40000

##### 开启防火墙

> hadoop有网页漏洞会被攻击

```bash
su
systemctl start firewalld
systemctl status firewalld
```

##### 配置白名单

> 注意代理会导致无法访问
> 210.87.110.108

```bash
su
# 对固定ip添加白名单
firewall-cmd --permanent --add-rich-rule='rule family="ipv4" source address="101.80.203.74" accept'
# 对固定ip的固定端口号添加白名单
firewall-cmd --permanent --add-rich-rule='rule family="ipv4" source address="101.80.203.74" port protocol="tcp" port="40000" accept'
# 删除白名单
firewall-cmd --permanent --zone=public --remove-rich-rule='rule family="ipv4" source address="101.80.203.74" accept'
firewall-cmd --reload
firewall-cmd --list-all
```

##### 配置mapred-site.xml

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
    <name>mapreduce.application.classpath</name>
    <value>$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/*:$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/lib/*</value>
  </property>
  
  <property>
    <name>mapreduce.jobhistory.webapp.address</name>
    <value>isxcode:40001</value>
  </property>

  <property>
    <name>mapreduce.jobhistory.address</name>
    <value>isxcode:10020</value>
  </property>
</configuration>
```

##### 配置yarn-site.xml

```bash
vim /opt/hadoop/etc/hadoop/yarn-site.xml
```

```xml
<configuration>
  
     <property>
        <name>yarn.resourcemanager.webapp.address</name>
        <value>isxcode:40002</value>
    </property>
    
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.nodemanager.env-whitelist</name>
        <value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,YARN_CONF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_HOME,PATH,LANG,TZ,HADOOP_MAPRED_HOME</value>
    </property>
    <property>
	    <name>yarn.log-aggregation-enable</name>
	    <value>true</value>
    </property>
  
    <property>
	    <name>yarn.log.server.url</name>
	    <value>http://47.92.152.18:40001/jobhistory/logs</value>
    </property>
  
    <property>
        <name>yarn.nodemanager.resource.memory-mb</name>
        <value>20480</value>
    </property>
  
    <property>
        <name>yarn.nodemanager.resource.cpu-vcores</name>
        <value>8</value>
    </property>
  
    <property>
        <name>yarn.scheduler.minimum-allocation-vcores</name>
        <value>1</value>
    </property>
  
    <property>
        <name>yarn.scheduler.maximum-allocation-vcores</name>
        <value>2</value>
    </property>
  
    <property>
        <name>yarn.scheduler.maximum-allocation-mb</name>
        <value>5120</value>
    </property>
  
    <property>
        <name>yarn.scheduler.minimum-allocation-mb</name>
        <value>2048</value>
    </property>
  
    <property>
        <name>yarn.nodemanager.webapp.address</name>
        <value>isxcode:40003</value>
    </property>

    <property>
        <name>yarn.resourcemanager.address</name>
        <value>isxcode:8032</value>
    </property>
  
    <property>
        <name>yarn.resourcemanager.scheduler.class</name>
        <value>org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler</value>
    </property>

</configuration>
```

##### 启动yarn

```bash
start-yarn.sh
```

##### 访问地址

- http://47.92.152.18:40002

##### 服务器开放30177端口号

> 留给代理器访问

```bash
firewall-cmd --permanent --add-rich-rule='rule family="ipv4" source address="101.132.135.228" accept'
firewall-cmd --reload
```