---
title: "Mysql安装"
---

## Rancher2.8.5离线安装Mysql

#### 前提

> 安装Rancher

#### 上传mysql资源

> 需要资源邮箱咨询

```bash
scp -r /Users/ispong/OneDrive/Downloads/docker/mysql-8.0-amd64.tar zhiqingyun@39.100.75.11:/tmp
# 上传本地镜像
cd /tmp
docker load -i /tmp/mysql-8.0-amd64.tar
docker tag mysql:8.0 isxcode:8443/library/mysql:8.0
docker push isxcode:8443/library/mysql:8.0
```

#### 添加harbor凭证

> 将harbor镜像仓库添加到rancher中

![20250116112505](https://img.isxcode.com/picgo/20250116112505.png)

Type: Custom  
Registry Domain name: isxcode:8443  
Username: admin  
Password: Harbor12345  

![20250116112623](https://img.isxcode.com/picgo/20250116112623.png)

#### 创建mysql资源目录

```bash
sudo mkdir -p /mysql/data
```

#### 创建Department

![20250116112752](https://img.isxcode.com/picgo/20250116112752.png)

#### 挂载本地路径

> 将/mysql/data目录挂载到rancher中

![20250116112902](https://img.isxcode.com/picgo/20250116112902.png)

![20250116113006](https://img.isxcode.com/picgo/20250116113006.png)

#### 配置镜像

Name: isxcode-mysql  
Image: docker.io/library/mysql:8.0  
Pull Policy: IfNotPresent  
Pull Secrets: isxcode-harbor

![20250116113234](https://img.isxcode.com/picgo/20250116113234.png)

#### 配置端口号

> 选择`NodePort`模式，将3306映射到30002

![20250116113357](https://img.isxcode.com/picgo/20250116113357.png)

#### 配置mysql root密码

> 密码`MYSQL_ROOT_PASSWORD`设置为`Mysql123..`

#### 映射目录

| Mount Point          | Sub Path in Volume |
|:---------------------|:-------------------|
| /var/lib/mysql       | data               |     
| /etc/mysql/conf.d    | conf               |  
| /var/lib/mysql-files | mysql-files        |

#### Mysql连接信息

jdbcUrl:  jdbc:mysql://39.100.75.11:30002   
username:  root  
password:  Mysql123..