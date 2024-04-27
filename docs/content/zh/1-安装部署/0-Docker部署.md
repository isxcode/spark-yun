---
title: "Docker部署"
---

Docker hub官网: https://hub.docker.com/r/isxcode/zhiqingyun

##### 第一步,将配置文件和资源文件拷贝出来

```bash
docker run --name zhiqingyun -d isxcode/zhiqingyun
docker cp zhiqingyun:/var/lib/zhiqingyun /Users/ispong/zhiqingyun
docker cp zhiqingyun:/etc/zhiqingyun/conf /Users/ispong/zhiqingyun
docker stop zhiqingyun
docker rm zhiqingyun
```

##### 第二步,重新启动镜像

`ADMIN_PASSWORD`: 启动后，密码会存入数据库中，再次启动容器以数据库为准，配置不生效。<div></div>
`LOG_LEVEL`: 设置项目日志级别info、debug </br>
`ACTIVE_ENV`: 设置项目启动环境配置文件 </br> 
`/var/lib/zhiqingyun`: 项目资源目录 </br>
`/etc/zhiqingyun/conf`: 配置文件目录 </br>

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

访问项目: http://localhost:8080 <div></div>
默认管理员账号：`admin` </br>
默认管理员密码：`admin123`

##### 配置文件支持mysql数据源

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