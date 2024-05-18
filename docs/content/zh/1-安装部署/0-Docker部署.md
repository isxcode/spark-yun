---
title: "Docker部署"
---

### [Docker Hub 链接](https://hub.docker.com/r/isxcode/zhiqingyun)

##### 1. 将配置文件和资源文件拷贝出来

```bash
mkdir -p /Users/ispong/zhiqingyun
docker run --name zhiqingyun -d isxcode/zhiqingyun
# 拷贝资源文件
docker cp zhiqingyun:/var/lib/zhiqingyun /Users/ispong/zhiqingyun
# 拷贝配置文件
docker cp zhiqingyun:/etc/zhiqingyun/conf /Users/ispong/zhiqingyun
docker stop zhiqingyun
docker rm zhiqingyun
```

##### 2. 重新启动镜像

▪ `ADMIN_PASSWORD`: admin账号密码，仅初次启动项目时生效,默认密码`admin123`<br/>
▪ `LOG_LEVEL`: 日志级别设置，例如info、debug、error等 <br/>
▪ `ACTIVE_ENV`: 配置文件，默认配置文件`docker` <br/>
▪ `/var/lib/zhiqingyun`: 资源目录 <br/>
▪ `/etc/zhiqingyun/conf`: 配置文件目录 <br/>

```bash
docker run --restart=always \
    --name zhiqingyun\
    -e ADMIN_PASSWORD=admin123 \
    -e LOG_LEVEL=info \
    -e ACTIVE_ENV=docker \
    -v /Users/ispong/zhiqingyun/zhiqingyun:/var/lib/zhiqingyun \
    -v /Users/ispong/zhiqingyun/conf:/etc/zhiqingyun/conf \
    -p 8080:8080 \
    -d isxcode/zhiqingyun
```

##### 3. 访问项目

▪ 访问地址: http://localhost:8080 <br/>
▪ 管理员账号：`admin` <br/>
▪ 管理员密码：`admin123`

#### 配置Mysql数据源 (可选)

> 修改项目配置文件

```bash
vim /Users/ispong/zhiqingyun/conf/application-docker.yml
```

```yml
server:
  port: 8080

spring:

  security:
    user:
      name: admin
      password: admin123

  jpa:
    database: mysql

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://192.168.199.146:30306/zhiqingyun_db
    username: root
    password: ispong123

  flyway:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://192.168.199.146:30306/zhiqingyun_db
    user: root
    password: ispong123
    locations: classpath:db/migration/mysql

logging:
  level:
    root: info

spark-yun:
  default-agent-port: 30177
  tmp-dir: /tmp

isx-app:
  use-port: true
  use-ssl: false
  resources-path: /var/lib/zhiqingyun
  docker-mode: true
```

> 重启docker镜像

```bash
docker restart zhiqingyun
```