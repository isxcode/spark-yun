?> 至轻云中非企业版本功能将全部**开源**，支持本地代码构建部署。

##### 前提

- **CentOS-7.9**
- [Java-1.8](https://ispong.isxcode.com/spring/java/java%20%E5%AE%89%E8%A3%85/)

```bash
sudo yum install java-1.8.0-openjdk-devel java-1.8.0-openjdk -y 
```

- [Node-16](https://ispong.isxcode.com/react/nodejs/nodejs%20%E5%AE%89%E8%A3%85/)

```bash
sudo yum install node npm -y
```

##### 下载代码

```bash
sudo yum install git -y
git clone https://github.com/isxcode/spark-yun.git
```

##### 下载spark二进制文件

!> 目前只可以使用`spark-3.4.0-bin-hadoop3`版本

```bash
nohup wget https://archive.apache.org/dist/spark/spark-3.4.0/spark-3.4.0-bin-hadoop3.tgz >> download_spark.log 2>&1 &  
tail -f download_spark.log
tar vzxf spark-3.4.0-bin-hadoop3.tgz -C /tmp/
mv /tmp/spark-3.4.0-bin-hadoop3 /tmp/spark-min
```

##### 修改配置文件（可选）

```bash
vim spark-yun/spark-yun-backend/src/main/resources/application-local.yml
```

```yml
spring:

  security:
    user:
      roles: ADMIN
      name: admin
      password: admin123

  jpa:
    database: mysql
    show-sql: false

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:30306/zhiqingyun
    username: root
    password: ispong123

  flyway:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:30306/zhiqingyun
    user: root
    password: ispong123
    locations: classpath:db/migration/mysql

  quartz:
    properties:
      org.quartz.dataSource.quartzDataSource.driver: com.mysql.cj.jdbc.Driver
      org.quartz.dataSource.quartzDataSource.URL: jdbc:mysql://localhost:30306/zhiqingyun
      org.quartz.dataSource.quartzDataSource.user: root
      org.quartz.dataSource.quartzDataSource.password: ispong123
```

##### 启动项目

```bash
cd spark-yun
./gradlew start
```

![img](https://img.isxcode.com/picgo/20230527155307.png)