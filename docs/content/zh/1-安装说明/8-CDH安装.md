---
title: "CDH安装"
---

## 离线安装CDH6.2.0

#### 前提

> 安装Rancher  
> 安装Mysql

#### 上传cdh资源

> 需要资源邮箱咨询

```bash
scp -r /Users/ispong/OneDrive/Downloads/linux/cdh/cdh.zip zhiqingyun@39.100.75.11:/tmp
cd /tmp
unzip cdh.zip

scp -r /Users/ispong/OneDrive/Downloads/docker/cdh-httpd-1.0-amd64.tar zhiqingyun@39.100.75.11:/tmp
cd /tmp 
docker -i cdh-httpd-1.0-amd64.tar
```

#### 安装基础软件

```bash
# 安装postgres 
cd /tmp/cdh/rpm/postgres
# 冲突的包删除
rm -rf /tmp/cdh/rpm/postgres/systemd-sysv-219-78.el7.x86_64.rpm
sudo rpm -ivh ./* --nosignature --force

# 安装openjdk
sudo yum remove java-1.8.0-openjdk-headless -y
cd /tmp/cdh/rpm/openjdk
# 冲突的包删除
rm -rf /tmp/cdh/rpm/openjdk/cups-libs-1.6.3-51.el7.x86_64.rpm
rm -rf /tmp/cdh/rpm/openjdk/freetype-2.8-14.el7.x86_64.rpm
sudo rpm -ivh ./* --nosignature --force

# 安装ntp
cd /tmp/cdh/rpm/ntp
sudo rpm -ivh ./* --nosignature --force
```

#### 安装时区同步

```bash
sudo systemctl enable ntpd
sudo systemctl start ntpd
sudo vim /etc/ntp.conf
```

> 使用内网ip

```bash
# server 0.centos.pool.ntp.org iburst
# server 1.centos.pool.ntp.org iburst
# server 2.centos.pool.ntp.org iburst
# server 3.centos.pool.ntp.org iburst
server 172.16.215.83 iburst
```

```bash
sudo systemctl restart ntpd
sudo systemctl status ntpd
timedatectl status
```

#### 安装mysql驱动

```bash
sudo cp /tmp/cdh/jdbc/mysql-connector-java-8.0.23.jar /usr/share/java/mysql-connector-java.jar
sudo chmod 777 /usr/share/java/mysql-connector-java.jar
```

#### 安装scm

> 主节点安装 按顺序运行

```bash
cd /tmp/cdh/cdh6.2.0/cloudera-repos-6.2.0
sudo yum install -y cloudera-manager-daemons-6.2.0-968826.el7.x86_64.rpm 
sudo yum install -y cloudera-manager-server-6.2.0-968826.el7.x86_64.rpm
sudo yum install -y cloudera-manager-server-db-2-6.2.0-968826.el7.x86_64.rpm
```

#### Mysql创建数据库

```bash
CREATE DATABASE cdh_scm DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE DATABASE cdh_hive DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE DATABASE cdh_monitor DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
```

#### 初始化数据库

> /opt/cloudera/cm/schema/scm_prepare_database.sh -h ${host} -P ${port} mysql ${dbName} ${username} ${password}

```bash
# 使用root执行
sudo su 
/opt/cloudera/cm/schema/scm_prepare_database.sh -h isxcode -P 30002 mysql cdh_scm root Mysql123..
sudo su zhiqingyun
```

#### 安装httpd

```bash
docker run -itd \
  --privileged=true \
  --name=cdh-httpd \
  -p 30008:30108 \
  -e TZ=Asia/Shanghai \
  cdh-httpd:1.0 /sbin/init
```

- http://39.100.75.11:30008/cdh6_parcel
- http://39.100.75.11:30008/cdh6_parcel/cm6

#### 添加软件源

> 主/从服务器都要执行

```bash
cd /etc/yum.repos.d
sudo vim cloudera-manager.repo
```

```bash
[cloudera-manager]
name=Cloudera Manager 6.2.0
baseurl=http://isxcode:30008/cdh6_parcel/cm6
gpgcheck=0
enabled=1
```

```bash
sudo yum-config-manager --enable cloudera-manager
sudo yum clean all && yum makecache
```

#### 启动scm

```bash
sudo systemctl enable cloudera-scm-server
sudo systemctl start cloudera-scm-server
sudo systemctl status cloudera-scm-server
```

#### 查看scm日志

```bash
sudo tail -f /var/log/cloudera-scm-server/cloudera-scm-server.log
```

#### 修改scm访问端口

```sql
-- INSERT INTO CONFIGS (CONFIG_ID, ATTR, VALUE, CONFIG_CONTAINER_ID) VALUES (4, 'http_port', '${customPort}', 2);
INSERT INTO cdh_scm.CONFIGS (CONFIG_ID, ATTR, VALUE, CONFIG_CONTAINER_ID)
VALUES (4, 'http_port', '30007', 2);
```

```bash
sudo systemctl restart cloudera-scm-server
```

#### 访问cdh

> 默认端口号: 7180    
> 访问地址: http://39.100.75.11:30007    
> 管理员账号: admin    
> 账号密码: admin

![20250116123701](https://img.isxcode.com/picgo/20250116123701.png)

#### 安装集群

![20240730145812](https://img.isxcode.com/picgo/20240730145812.png)

![20250116140008](https://img.isxcode.com/picgo/20250116140008.png)

![20240730145840](https://img.isxcode.com/picgo/20240730145840.png)

![20250116140120](https://img.isxcode.com/picgo/20250116140120.png)

![20240730145947](https://img.isxcode.com/picgo/20240730145947.png)

![20240730150015](https://img.isxcode.com/picgo/20240730150015.png)

> 使用内网地址  
> Cloudera Manager Agent: http://isxcode:30008/cdh6_parcel/cm6  
> CDH and other software: http://isxcode:30008/cdh6_parcel

![20240730150250](https://img.isxcode.com/picgo/20240730150250.png)

> 注意：Parcel Directory 一定要修改为/data/cloudera/parcels ，挂载到磁盘  
> 注意： /data/cloudera/parcels 目录一定不要提前创建

![20250116140307](https://img.isxcode.com/picgo/20250116140307.png)

![20250116140541](https://img.isxcode.com/picgo/20250116140541.png)

![20240730150424](https://img.isxcode.com/picgo/20240730150424.png)

![20250116140641](https://img.isxcode.com/picgo/20250116140641.png)

![20240730150541](https://img.isxcode.com/picgo/20240730150541.png)

![20240730150806](https://img.isxcode.com/picgo/20240730150806.png)

![20240730150855](https://img.isxcode.com/picgo/20240730150855.png)

#### 安装服务

> 安装hive、hdfs、yarn、zookeeper服务

![20250116141158](https://img.isxcode.com/picgo/20250116141158.png)

![20240730151209](https://img.isxcode.com/picgo/20240730151209.png)

> 使用mysql中创建的cdh_hive和cdh_monitor库  
> 使用内网方式连接  
> isxcode:30002  
> cdh_hive  
> cdh_monitor  
> root   
> Mysql123..

![20250116141530](https://img.isxcode.com/picgo/20250116141530.png)

> 修改安装路径，否则会安装到系统盘  
> 一定不要提前创建目录！！！ 会有权限问题

| 配置项                             | 默认值                               | 修改值                            |
|:--------------------------------|:----------------------------------|:-------------------------------|
| dfs.datanode.data.dir           | /dfs/dn                           | /data/dfs/dn                   |
| dfs.namenode.name.dir           | /dfs/nn                           | /data/dfs/nn                   |
| dfs.namenode.checkpoint.dir     | /dfs/snn                          | /data/dfs/snn                  |
| firehose.storage.base.directory | /var/lib/cloudera-host-monitor    | /data/cloudera-host-monitor    |
| firehose.storage.base.directory | /var/lib/cloudera-service-monitor | /data/cloudera-service-monitor | 
| yarn.nodemanager.local-dirs     | /yarn/nm                          | /data/yarn/nm                  |
| dataDir                         | /var/lib/zookeeper                | /data/zookeeper                |
| dataLogDir                      | /var/lib/zookeeper                | /data/zookeeper                |

![20250116142443](https://img.isxcode.com/picgo/20250116142443.png)

> 根据cdh提示，优化系统

![20250116143015](https://img.isxcode.com/picgo/20250116143015.png)

#### 配置Yarn的可用资源

> 根据自己的服务器资源动态调整，调整完后重启yarn服务

![20250116143142](https://img.isxcode.com/picgo/20250116143142.png)

![20250116143216](https://img.isxcode.com/picgo/20250116143216.png)

| 配置项                                      | 配置值  | 
|:-----------------------------------------|:-----|
| yarn.nodemanager.resource.cpu-vcores     | 16   |
| yarn.nodemanager.resource.memory-mb      | 32GB |
| yarn.scheduler.minimum-allocation-vcores | 1    |
| yarn.scheduler.minimum-allocation-mb     | 1GB  |
| yarn.scheduler.maximum-allocation-vcores | 4    |
| yarn.scheduler.maximum-allocation-mb     | 4GB  |

#### 配置Hadoop环境变量

```bash
tee -a /etc/profile <<-'EOF'
export HADOOP_HOME=/data/cloudera/parcels/CDH/lib/hadoop
export HADOOP_CONF_DIR=/data/cloudera/parcels/CDH/lib/hadoop/etc/hadoop
EOF
source /etc/profile
```

#### 设置hive访问权限

```bash
groupadd supergroup
usermod -a -G supergroup zhiqingyun
newgrp supergroup
```

#### hive连接信息

url: jdbc:hive2://39.100.75.11:10000    
hive.metastore.uris: thrift://172.16.215.84:9083     
username: zhiqingyun  
password: <空>  

![20250116144024](https://img.isxcode.com/picgo/20250116144024.png)

#### Hadoop访问地址

hdfs: http://39.100.75.11:9870

![20250116143806](https://img.isxcode.com/picgo/20250116143806.png)

yarn: http://39.100.75.11:8088

![20250116143748](https://img.isxcode.com/picgo/20250116143748.png)

#### 相关文档

- [hadoop docs](https://hadoop.apache.org/docs/stable/index.html)
- [ali rpm 下载中心](https://mirrors.aliyun.com/centos/7/os/x86_64/Packages/)