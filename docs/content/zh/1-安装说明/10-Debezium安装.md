---
title: "Debezium安装"
---

## Rancher离线安装Debezium

#### 上传镜像资源

> 需要资源邮箱咨询

```bash
scp /Users/ispong/OneDrive/Downloads/docker/debezium-kafka-3.0.0.Final-amd64.tar zhiqingyun@39.100.75.11:/tmp
scp /Users/ispong/OneDrive/Downloads/docker/debezium-connect-3.0.0.Final-amd64.tar zhiqingyun@39.100.75.11:/tmp
scp /Users/ispong/OneDrive/Downloads/docker/debezium-ui-2.1.2.Final-amd64.tar zhiqingyun@39.100.75.11:/tmp
scp /Users/ispong/OneDrive/Downloads/docker/debezium-zookeeper-3.0.0.Final-amd64.tar zhiqingyun@39.100.75.11:/tmp

# 上传本地镜像
docker load -i /tmp/debezium-kafka-3.0.0.Final-amd64.tar
docker load -i /tmp/debezium-connect-3.0.0.Final-amd64.tar
docker load -i /tmp/debezium-ui-2.1.2.Final-amd64.tar
docker load -i /tmp/debezium-zookeeper-3.0.0.Final-amd64.tar

docker tag debezium/zookeeper:3.0.0.Final isxcode:8443/library/debezium-zookeeper:3.0.0.Final
docker push isxcode:8443/library/debezium-zookeeper:3.0.0.Final

docker tag debezium/kafka:3.0.0.Final isxcode:8443/library/debezium-kafka:3.0.0.Final
docker push isxcode:8443/library/debezium-kafka:3.0.0.Final

docker tag debezium/connect:3.0.0.Final isxcode:8443/library/debezium-connect:3.0.0.Final
docker push isxcode:8443/library/debezium-connect:3.0.0.Final

docker tag debezium/debezium-ui:2.1.2.Final isxcode:8443/library/debezium-ui:2.1.2.Final
docker push isxcode:8443/library/debezium-ui:2.1.2.Final
```

#### rancher安装zookeeper

> 创建资源目录

```bash
sudo mkdir -p /data/debezium/zookeeper
sudo chmod -R 777 /data/debezium/zookeeper
```

> 磁盘映射

| Mount Point     | Sub Path in Volume |
|:----------------|:-------------------|
| /zookeeper/logs | logs               |     
| /zookeeper/data | data               |  
| /zookeeper/txns | txns               |

Name：debezium-zookeeper  
Image：docker.io/debezium-zookeeper:3.0.0.Final    
Port：2181 -> 30151

![20250116153314](https://img.isxcode.com/picgo/20250116153314.png)

#### rancher安装kafka

> 创建资源目录

```bash
sudo mkdir -p /data/debezium/kafka
sudo chmod -R 777 /data/debezium/kafka
```

> 磁盘映射

| Mount Point | Sub Path in Volume |
|:------------|:-------------------|
| /kafka/data | data               |     
| /kafka/logs | logs               |

Name：debezium-kafka  
Image：docker.io/debezium-kafka:3.0.0.Final  
Port：9092 -> 30152 30155 -> 30155

| 变量                                   | 参数值                                                          | 说明                                                                       |
|:-------------------------------------|:-------------------------------------------------------------|:-------------------------------------------------------------------------|
| ZOOKEEPER_CONNECT                    | 172.16.215.84:30151                                          | zk的内网ip链接信息                                                              | 
| KAFKA_INTER_BROKER_LISTENER_NAME     | INTERNAL                                                     |                                                                          |
| KAFKA_LISTENER_SECURITY_PROTOCOL_MAP | EXTERNAL:PLAINTEXT,INTERNAL:PLAINTEXT                        |                                                                          |
| KAFKA_ADVERTISED_LISTENERS           | EXTERNAL://39.100.75.11:30155,INTERNAL://debezium-kafka:9092 | EXTERNAL://${外网ip}:30155,INTERNAL://${service-name(debezium-kafka)}:9092 |
| KAFKA_LISTENERS                      | EXTERNAL://0.0.0.0:30155,INTERNAL://0.0.0.0:9092             |                                                                          |
| KAFKA_MAX_REQUEST_SIZE               | 200000000                                                    |                                                                          |

![20250116154227](https://img.isxcode.com/picgo/20250116154227.png)

#### kafka链接信息

外网：39.100.75.11:30155  
内网：isxcode:9092

#### rancher安装debezium-connect

Name：debezium-connect  
Image：docker.io/debezium-connect:3.0.0.Final  
Port：8083 -> 30153

| 变量                   | 参数值                      | 说明          |
|:---------------------|:-------------------------|:------------|
| GROUP_ID             | 1                        | 集群分组        | 
| BOOTSTRAP_SERVERS    | 172.16.215.84:30152      | 内网kafka连接信息 |
| ZOOKEEPER_CONNECT    | 172.16.215.84:30151      | 内网zk连接信息    |
| CONFIG_STORAGE_TOPIC | isxcode_connect_configs  |             |
| OFFSET_STORAGE_TOPIC | isxcode_connect_offsets  |             |
| STATUS_STORAGE_TOPIC | isxcode_connect_statuses |             |

![20250116160831](https://img.isxcode.com/picgo/20250116160831.png)

#### rancher安装debezium-ui

Name：debezium-ui
Image：docker.io/debezium-ui:2.1.2.Final
Port：8080 -> 30154

| 变量                 | 参数值                       | 说明           |
|:-------------------|:--------------------------|:-------------|
| KAFKA_CONNECT_URIS | http://39.100.75.11:30153 | connect的外网地址 |

![20250116160933](https://img.isxcode.com/picgo/20250116160933.png)

#### debezium访问地址

http://39.100.75.11:30154

![20250116161839](https://img.isxcode.com/picgo/20250116161839.png)

#### 使用接口方式监听mysql数据库

```bash
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" 39.100.75.11:30153/connectors/ -d '{
  "name": "test-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "172.16.215.84",
    "database.port": "30002",
    "database.user": "root",
    "database.password": "Mysql123..",
    "database.server.id": "1",
    "topic.prefix": "test-connector-prefix",
    "database.include.list": "ispong_db",
    "schema.history.internal.kafka.bootstrap.servers": "172.16.215.84:30152",
    "schema.history.internal.kafka.topic": "schemahistory.inventory"
  }
}'
```

![20250116162857](https://img.isxcode.com/picgo/20250116162857.png)

#### 添加kafka连接

> topic规则：{topic.prefix}.{databaseName}.{tableName}   
> 举例： test-connector-prefix.ispong_db.users

![20250116162824](https://img.isxcode.com/picgo/20250116162824.png)

#### 新建kafka的topic

```bash
kafka-topics.sh --create --bootstrap-server debezium-kafka:9092 --topic test-topic --replication-factor 1 --partitions 1 
kafka-topics.sh --bootstrap-server debezium-kafka:9092 --list
```