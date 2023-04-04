#### 前提

- linux服务器 centOS 7.9 16GB 4核
- [git linux安装](https://ispong.isxcode.com/github/git/git%20%E5%AE%89%E8%A3%85/)
- [java linux安装](https://ispong.isxcode.com/spring/java/java%20%E5%AE%89%E8%A3%85/)
- [docker linux安装](https://ispong.isxcode.com/linux/docker/docker%20%E5%AE%89%E8%A3%85/)
- [node linux安装](https://ispong.isxcode.com/react/nodejs/nodejs%20%E5%AE%89%E8%A3%85/)

#### 下载源码

```bash
git clone -b main https://gitee.com/isxcode/spark-yun.git
```

### 下载spark

```bash
wget https://archive.apache.org/dist/spark/spark-3.1.1/spark-3.1.1-bin-hadoop3.2.tgz 
tar vzxf spark-3.1.1-bin-hadoop3.2.tgz -C /tmp/
```

### 镜像打包

```bash

./gradlew docker
```

### 启动容器

```bash
docker run -p 30111:8080 -d isxcode/spark-star:3.0.1
```

### 访问界面

- http://39.98.223.46:30111
