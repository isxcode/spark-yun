### 安装git，下载源码

- [git 安装](https://ispong.isxcode.com/github/git/git%20%E5%AE%89%E8%A3%85/)

```bash
git clone -b main https://gitee.com/isxcode/spark-yun.git
```

### 安装java环境

- [java 安装](https://ispong.isxcode.com/spring/java/java%20%E5%AE%89%E8%A3%85/)

### 安装docker环境

- [docker 安装](https://ispong.isxcode.com/linux/docker/docker%20%E5%AE%89%E8%A3%85/)

### 安装node环境

- [node 安装](https://ispong.isxcode.com/react/nodejs/nodejs%20%E5%AE%89%E8%A3%85/)

### 安装gradle环境

- [gradle 安装](https://ispong.isxcode.com/react/nodejs/gradle%20%E5%AE%89%E8%A3%85/)

### 下载spark二进制

> 在jars中提前下载好jdbc驱动

```bash
nohup wget https://archive.apache.org/dist/spark/spark-3.1.1/spark-3.1.1-bin-hadoop3.2.tgz >> download_spark.log 2>&1 &  
tail -f download_spark.log
tar vzxf spark-3.1.1-bin-hadoop3.2.tgz -C /tmp/
```

### 镜像打包

```bash
./gradlew docker
```

### 启动容器

```bash
docker run --name spark-yun -p 30111:8080 -d isxcode/zhiqingyun:0.0.1
```

### 访问界面

- http://192.168.0.106:30111
