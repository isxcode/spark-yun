?> 至轻云平台镜像由Docker Hub官方镜像仓库管理

<br/>

<h5>
  <a href="https://hub.docker.com/r/isxcode/zhiqingyun/tags">点击跳转Docker Hub</a>
</h5>

<br/>

<img src="https://img.isxcode.com/picgo/20230527155542.png" width="700">

### 版本说明

- `alpha`：内测版
- `latest`：稳定版
- 已发布版本: `0.0.2`

### 如何快速部署？

管理员默认账号：`admin` </br>
管理员默认密码：`admin123`

```bash
docker run -p 8080:8080 -d isxcode/zhiqingyun
```

### 如何初始化管理员密码？

`ADMIN_PASSWORD`: 系统管理员密码，注意: 首次运行后，密码会存入数据库中，再次启动容器以数据库为准，配置不生效。

```bash
docker run -p 8080:8080 -e ADMIN_PASSWORD=123456 -d isxcode/zhiqingyun
```

### 如何挂载资源目录？

```bash
docker run -v /zhiqingyun/resources:/var/lib/spark-yun -p 8080:8080 -d isxcode/zhiqingyun
```

### 如何自定义配置？

注意：需要提前创建配置文件，目前只支持`h2`和`mysql`数据库

```bash
mkdir -p  /zhiqingyun/config
vim /zhiqingyun/config/application-demo.yml
```

```yml
spring:
  
  jpa:
    database: h2 # 可配置h2或者mysql

  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:file:/var/lib/spark-yun/h2/data
    username: root
    password: root123

  flyway:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:file:/var/lib/spark-yun/h2/data
    user: root
    password: root123
    locations: classpath:db/migration/h2  #  可配置classpath:db/migration/h2或者classpath:db/migration/mysql

  quartz:
    properties:
      org.quartz.dataSource.quartzDataSource.driver: org.h2.Driver
      org.quartz.dataSource.quartzDataSource.URL: jdbc:h2:file:/var/lib/spark-yun/h2/data
      org.quartz.dataSource.quartzDataSource.user: root
      org.quartz.dataSource.quartzDataSource.password: root123
```

```bash
docker run -v /zhiqingyun/config:/etc/spark-yun/conf -e ACTIVE_ENV=demo -p 8080:8080 -d isxcode/zhiqingyun
```