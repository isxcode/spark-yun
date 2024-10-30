---
title: "Docker部署"
---

### Docker快速部署

##### 国内用户

> 国内用户选择以下地址，arm64多用于macOS系统用户，普通服务器x86选择latest-amd64版本 <br/>

```bash
docker run --name zhiqingyun -d registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64 
docker run --name zhiqingyun -d registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-arm64
```

##### 启动至轻云

▪ `ADMIN_PASSWORD`: 启动后，密码会存入数据库中，再次启动容器以数据库为准，配置不生效。若想更改，需要删除数据库中的管理员密码重新登录即可。<br/>
▪ `LOG_LEVEL`: 设置项目日志级别，例如info、debug。<br/>
▪ `ACTIVE_ENV`: 设置项目启动环境配置文件，默认值docker。<br/>
▪ `PARAMS`: spring项目相关配置。<br/>
▪ `/var/lib/zhiqingyun`: /var/lib/zhiqingyun: 项目资源目录。<br/>
▪ `/etc/zhiqingyun/conf`: /etc/zhiqingyun/conf: 配置文件目录。 

```bash
docker run \
    --restart=always \
    --name zhiqingyun\
    -e ADMIN_PASSWORD=admin123 \
    -v /Users/ispong/zhiqingyun/zhiqingyun:/var/lib/zhiqingyun \
    -p 8080:8080 \
    -d registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64
```

▪ 访问地址: http://localhost:8080 <br/>
▪ 管理员账号：`admin` <br/>
▪ 管理员密码：`admin123`

##### 修改配置

默认配置文件模版 <br/>
https://raw.githubusercontent.com/isxcode/spark-yun/refs/heads/main/spark-yun-backend/spark-yun-main/src/main/resources/application-docker.yml

```bash
vim /Users/ispong/zhiqingyun/conf/application-docker.yml
```

```bash
docker run \
    --restart=always \
    --name zhiqingyun\
    -e ADMIN_PASSWORD=admin123 \
    -e LOG_LEVEL=info \
    -e ACTIVE_ENV=docker \
    -v /Users/ispong/zhiqingyun/zhiqingyun:/var/lib/zhiqingyun \
    -v /Users/ispong/zhiqingyun/conf:/etc/zhiqingyun/conf \
    -p 8080:8080 \
    -d isxcode/zhiqingyun
```

##### 上传许可证

[下载许可证链接](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/license.lic)