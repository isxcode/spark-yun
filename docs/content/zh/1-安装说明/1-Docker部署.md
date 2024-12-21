---
title: "Docker部署"
---

## 使用Docker快速部署至轻云

#### 镜像地址

> 国内用户可以选择以下镜像，arm64多用于macOS用户，x86服务器架构选择`latest-amd64`版本 

```bash
registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64 
registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-arm64
```

#### 快速启动至轻云

```bash
docker run \
    --restart=always \
    --name zhiqingyun\
    -e ADMIN_PASSWORD=admin123 \
    -p 8088:8080 \
    -d registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64
```

- 访问地址: http://localhost:8088 
- 管理员账号: `admin` 
- 管理员密码: `admin123`

#### 参数说明

- `ADMIN_PASSWORD`: 启动后，密码会存入数据库中，再次启动容器以数据库为准，配置不生效。若想更改，需要删除数据库中的管理员密码重新登录即可。
- `LOG_LEVEL`: 设置项目日志级别，例如info、debug。
- `ACTIVE_ENV`: 设置项目启动环境配置文件，默认值docker。
- `PARAMS`: spring项目相关配置。
- `/var/lib/zhiqingyun`: /var/lib/zhiqingyun: 项目资源目录。
- `/etc/zhiqingyun/conf`: /etc/zhiqingyun/conf: 配置文件目录。

#### 修改配置

默认配置文件模版参考链接:  
> https://raw.githubusercontent.com/isxcode/spark-yun/refs/heads/main/spark-yun-backend/spark-yun-main/src/main/resources/application-docker.yml

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