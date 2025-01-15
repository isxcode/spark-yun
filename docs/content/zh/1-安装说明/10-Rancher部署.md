---
title: "Rancher部署"
---

## Rancher部署至轻云

#### 创建资源目录

```bash
sudo mkdir -p /data/zhiqingyun
sudo chown -R zhiqingyun:zhiqingyun /data/zhiqingyun 
```

#### 下载配置文件

```bash
mkdir -p /data/zhiqingyun/conf
cd /data/zhiqingyun/conf
wget https://gitee.com/isxcode/spark-yun/raw/main/spark-yun-backend/spark-yun-main/src/main/resources/application-docker.yml
```

#### 编辑配置文件

```bash
vim /data/zhiqingyun/conf/application-docker.yml
```

```yml
server:
  port: 8080

spring:
  security:
    user:
      name: admin
      password: admin123

  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:file:/var/lib/zhiqingyun/h2/data;AUTO_SERVER=TRUE
    username: root
    password: root123

jasypt:
  encryptor:
    password: zhiqingyun

logging:
  file:
    name: /var/lib/zhiqingyun/logs/spark-yun.log

# 应用配置
isx-app:
  resources-path: /var/lib/zhiqingyun
  use-ssl: false
  docker-mode: true
```

#### 新建department

![20250115175052](https://img.isxcode.com/picgo/20250115175052.png)

#### 挂载磁盘

> 挂载目录`/data/zhiqingyun`

![20250115175633](https://img.isxcode.com/picgo/20250115175633.png)

#### 配置镜像地址

- name: zhiqingyun
- image: registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64

![20250115175850](https://img.isxcode.com/picgo/20250115175850.png)

#### 配置端口号

> 将8080端口号映射到30001

![20250115180128](https://img.isxcode.com/picgo/20250115180128.png)

#### 配置管理员登录密码

> ADMIN_PASSWORD: admin123 (可自定义)

![20250115180002](https://img.isxcode.com/picgo/20250115180002.png)

#### 映射容器路径

 > `/data/zhiqingyun/zhiqingyun`映射到`/var/lib/zhiqingyun`  
 > `/data/zhiqingyun/conf`映射到`/etc/zhiqingyun/conf`

![20250115180053](https://img.isxcode.com/picgo/20250115180053.png)

#### 访问服务

访问地址：http://39.100.75.11:30001   
管理员账号：admin  
账号密码：admin23  

![20250115181005](https://img.isxcode.com/picgo/20250115181005.png)

#### 查看系统资源

```bash
cd /data/zhiqingyun/zhiqingyun

drwxr-xr-x 3 root       root       4096 Jan 15 18:11 file    # 用户上传的资源中心文件目录
drwxr-xr-x 2 root       root       4096 Jan 15 18:08 h2      # 系统数据库h2数据
drwxr-xr-x 2 root       root       4096 Jan 15 18:11 license # 许可证路径
drwxr-xr-x 2 root       root       4096 Jan 15 18:04 logs    # 系统运行日志
```