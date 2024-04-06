---
title: "Hadoop单节点安装"
---

##### 阿里云服务器

- 8核心
- 32内存
- 40 + 200GB存储
- 开放端口号30177 40000~40050

###### 安装hadoop

> 参考Hadoop单节点安装手册

###### 下载hive安装包

```bash
su
mkdir -p /data/hive
chown -R zhiqingyun:zhiqingyun /data/hive

su zhiqingyun
cd /tmp
nohup wget https://archive.apache.org/dist/hive/hive-3.1.3/apache-hive-3.1.3-bin.tar.gz >> download_hive.log 2>&1 &
tail -f download_hive.log
tar -vzxf /tmp/apache-hive-3.1.3-bin.tar.gz -C /data/hive/

su
ln -s /data/hive/apache-hive-3.1.3-bin /opt/hive
```

###### 配置环境文件

```bash
su
tee -a /etc/profile <<-'EOF'
export HIVE_HOME=/opt/hive 
export PATH=$PATH:$HIVE_HOME/bin 
EOF
source /etc/profile
```

###### 检测安装

```bash
hive --version
```

###### 下载mysql驱动

```bash
su zhiqingyun
cd /tmp
wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-8.0.22.tar.gz
tar vzxf mysql-connector-java-8.0.22.tar.gz
cp mysql-connector-java-8.0.22/mysql-connector-java-8.0.22.jar /opt/hive/lib/
```

##### 创建mysql数据库

```bash
docker exec -it isxcode-mysql /bin/bash
mysql -h localhost -u root -pxxxx -P 40004
```

```sql
CREATE USER IF NOT EXISTS 'hive_admin'@'%' identified by 'xxx';
CREATE DATABASE IF NOT EXISTS hive_db DEFAULT CHARSET utf8mb4;
GRANT ALL PRIVILEGES on hive_db.* to 'hive_admin'@'%';
FLUSH PRIVILEGES;
exit;
```

##### 修改日志路径

```bash
su zhiqingyun
mkdir -p /data/hive/logs
cp /opt/hive/conf/hive-log4j2.properties.template /opt/hive/conf/hive-log4j2.properties
vim /opt/hive/conf/hive-log4j2.properties
```

```bash
property.hive.log.dir=/data/hive/logs
```

##### 修改hive-env.sh

```bash
su zhiqingyun
cp /opt/hive/conf/hive-env.sh.template /opt/hive/conf/hive-env.sh
tee -a /opt/hive/conf/hive-env.sh <<-'EOF'
export HADOOP_HOME=/opt/hadoop
export HIVE_CONF_DIR=/opt/hive/conf
EOF
```

##### 创建临时文件夹

```bash
mkdir -p /data/hive/tmp
```

###### 修改hive-site.xml

```bash
cp /opt/hive/conf/hive-default.xml.template /opt/hive/conf/hive-site.xml
vim /opt/hive/conf/hive-site.xml
# 3.1.3版本修改文件前需要删除 [3215,9] 3215行删掉描述
```

```xml
<configuration>
  
    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://isxcode:40004/hive_db</value>
    </property>
  
    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.cj.jdbc.Driver</value>
    </property>
  
    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>hive_admin</value>
    </property>
  
    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>xxxx</value>
    </property>
  
    <property>
        <name>hive.server2.thrift.client.user</name>
        <value>zhiqingyun</value>
    </property>
  
    <property>
        <name>hive.server2.thrift.client.password</name>
        <value>zhiqingyun</value>
    </property>
  
    <property>
      <name>system:java.io.tmpdir</name>
      <value>/data/hive/tmp</value>
    </property>
  
    <property>
      <name>system:user.name</name>
      <value>zhiqingyun</value>
    </property>
  
    <property>
        <name>hive.metastore.port</name>
        <value>9083</value>
    </property>
  
    <property>
        <name>hive.server2.thrift.port</name>
        <value>40005</value>
    </property>
  
    <property>
        <name>hive.server2.webui.port</name>
        <value>40006</value>
    </property>
     
</configuration>
```

##### 升级guava版本

```bash
rm /opt/hive/lib/guava-*.jar
cp /opt/hadoop/share/hadoop/hdfs/lib/guava-*.jar /opt/hive/lib/
```

##### 初始化mysql

```bash
schematool -dbType mysql -initSchema
```

##### 启动meta-store

```bash
nohup hive --service metastore >> /data/hive/logs/metastore.log 2>&1 &      
tail -f /data/hive/logs/metastore.log
```

##### 启动hive

```bash
nohup hive --service hiveserver2 >> /data/hive/logs/hiveserver2.log 2>&1 &   
tail -f /data/hive/logs/hiveserver2.log
```

##### 访问

- http://47.92.152.18:40006

##### hive链接

- jdbcUrl : jdbc:hive2://47.92.152.18:40005/isxcode_db
- username: zhiqingyun
- password: 无
- hive.metastore.uris: thrift://isxcode:9083