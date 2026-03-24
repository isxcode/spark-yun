---
title: "Docker部署"
---

## 使用Docker部署至轻云

### 镜像选择

##### Docker Hub官方镜像仓库

```bash
docker run -p 8080:8080 isxcode/zhiqingyun
```

##### 阿里云镜像仓库(国内用户推荐）

```bash
registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64
registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-arm64
```

**架构说明：**

- `latest-amd64`: 适用于 x86_64 架构服务器（常见的 Intel/AMD 处理器）
- `latest-arm64`: 适用于 ARM64 架构服务器（如 Apple M1/M2 芯片的 macOS）

### 快速启动

⚠️ **注意** 

> 服务器的最大打开文件数限制数，默认为1024，太小会导致作业执行速度变慢，建议修改为65535。

```bash
# 临时配置方式
ulimit -n 65535
```

```bash
docker run \
    --restart=always \
    --name zhiqingyun \
    -e ADMIN_PASSWORD=admin1234 \
    -p 8088:8080 \
    -d registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64
```

### 启动后访问

**访问地址**: http://localhost:8088  
**管理员账号**: `admin`  
**管理员密码**: `admin1234`

> ⚠️ **安全提示**: 生产环境请务必修改默认密码！

### 变量配置

| 变量名            | 说明           | 默认值      | 示例                           |
|:---------------|:-------------|:---------|:-----------------------------|
| ADMIN_PASSWORD | 管理员初始密码      | admin123 | admin1234                    |
| LOG_LEVEL      | 日志级别         | info     | info, debug, warn            |
| ACTIVE_ENV     | 环境配置文件       | docker   | dev, prod                    |
| PARAMS         | SpringBoot参数 | -        | --spring.flyway.enabled=true |
| JVMOPTIONS     | Jvm参数        | -        | -Xmx4g                       |

> 📝 **注意**: `ADMIN_PASSWORD` 仅在首次启动时生效，密码会保存到数据库中。如需修改密码，请在系统中操作或清空数据库重新初始化。

### 数据持久化

> 创建本地目录

```bash
mkdir -p /data/zhiqingyun/data
mkdir -p /data/zhiqingyun/conf
```

### 配置文件下载

```bash
# 下载默认配置文件
curl -o /data/zhiqingyun/conf/application-docker.yml \
  https://raw.githubusercontent.com/isxcode/spark-yun/refs/heads/main/spark-yun-backend/spark-yun-main/src/main/resources/application-docker.yml
```

### 挂载说明

| 容器路径                   | 说明              | 推荐挂载                    |
|------------------------|-----------------|-------------------------|
| `/var/lib/zhiqingyun`  | 数据存储目录（数据库、文件等） | `/data/zhiqingyun/data` |
| `/etc/zhiqingyun/conf` | 配置文件目录          | `/data/zhiqingyun/conf` |

### 参考命令

```bash
docker run \
    --restart=always \
    --name zhiqingyun \
    -e ADMIN_PASSWORD=admin123 \
    -e LOG_LEVEL=info \
    -e ACTIVE_ENV=docker \
    -e PARAMS="--spring.flyway.enabled=true" \
    -v /data/zhiqingyun/data:/var/lib/zhiqingyun \
    -v /data/zhiqingyun/conf:/etc/zhiqingyun/conf \
    -p 8088:8080 \
    -d registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64
```

### 镜像相关命令

```bash
# 查看容器运行状态
docker ps -a | grep zhiqingyun

# 查看容器日志
docker logs -f zhiqingyun

# 停止容器
docker stop zhiqingyun

# 重启容器
docker restart zhiqingyun
```

### 镜像版本升级

```bash
# 停止并删除旧容器和镜像
docker stop zhiqingyun && docker rm zhiqingyun
docker tag registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64 registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64-bak-20250728 
docker rmi registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64

# 拉取最新镜像
docker pull registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64
```

### jvm优化配置

> -Xmx4g 最大堆内存上限为4GB
> -Xms2g 初始堆内存为2GB
> -XX:+ExitOnOutOfMemoryError 首次发生oom就立即退出进程
> -XX:+HeapDumpOnOutOfMemoryError 发生内存溢出时自动生成堆转储文件
> -XX:HeapDumpPath=/var/lib/zhiqingyun/ 堆转储文件的保存路径
> -XX:+UseG1GC 使用G1（Garbage-First）垃圾回收器
> -XX:MaxMetaspaceSize=256m 元空间最大容量为256MB

```bash
-Xms2g -Xmx4g -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/lib/zhiqingyun/ -XX:+UseG1GC -XX:MaxMetaspaceSize=256m 
```

```bash
docker run \
    --restart=always \
    --name zhiqingyun \
    -e JVMOPTIONS="-Xms2g -Xmx4g -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/lib/zhiqingyun/ -XX:+UseG1GC -XX:MaxMetaspaceSize=256m" \
    -p 8088:8080 \
    -d registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64
```