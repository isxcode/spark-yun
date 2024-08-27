---
title: "Docker部署"
---

### Docker快速部署

##### 1. 创建至轻云存储目录（可选）

```bash
mkdir -p /Users/ispong/zhiqingyun
```

##### 2. 启动至轻云（可选）

> 国内用户选择以下地址，arm64多用于macOS系统用户，普通服务器选择latest-amd64版本 <br/>
> docker run --name zhiqingyun -d registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64 <br/>
> docker run --name zhiqingyun -d registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-arm64

```bash
docker run --name zhiqingyun -d isxcode/zhiqingyun
```

##### 3. 将docker中的资源文件拷贝出来（可选）

```bash
docker cp zhiqingyun:/var/lib/zhiqingyun /Users/ispong/zhiqingyun
```

##### 4.将docker中的配置文件拷贝出来（可选）

```bash
docker cp zhiqingyun:/etc/zhiqingyun/conf /Users/ispong/zhiqingyun
```

##### 5.删除docker容器（可选）

```bash
docker stop zhiqingyun
docker rm zhiqingyun
```

##### 6.重新启动至轻云

> 参数说明

▪ `ADMIN_PASSWORD`: admin账号密码，仅初次启动项目时生效,默认密码`admin123`<br/>
▪ `LOG_LEVEL`: 日志级别设置，例如info、debug、error等 <br/>
▪ `ACTIVE_ENV`: 配置文件，默认配置文件`docker` <br/>
▪ `/var/lib/zhiqingyun`: 资源目录 <br/>
▪ `/etc/zhiqingyun/conf`: 配置文件目录 <br/>

```bash
docker run --restart=always \
    --name zhiqingyun\
    -e ADMIN_PASSWORD=admin123 \
    -v /Users/ispong/zhiqingyun/zhiqingyun:/var/lib/zhiqingyun \
    -v /Users/ispong/zhiqingyun/conf:/etc/zhiqingyun/conf \
    -p 8080:8080 \
    -d isxcode/zhiqingyun:latest
```

> 快速启动

```bash
docker run --restart=always \
    --name zhiqingyun\
    -p 8080:8080 \
    -d isxcode/zhiqingyun:latest
```

##### 7. 访问项目

▪ 访问地址: http://localhost:8080 <br/>
▪ 管理员账号：`admin` <br/>
▪ 管理员密码：`admin123`

##### 8. 上传许可证

[下载许可证链接](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/license.lic)