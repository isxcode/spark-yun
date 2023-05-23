?> 至轻云中非企业版本功能将全部**开源**，支持本地代码构建部署。

##### 前提

- **CentOS-7.9**
- [Java-1.8](https://ispong.isxcode.com/spring/java/java%20%E5%AE%89%E8%A3%85/)
- [Node-16](https://ispong.isxcode.com/react/nodejs/nodejs%20%E5%AE%89%E8%A3%85/)

##### 下载代码

```bash
git clone https://gitee.com/isxcode/spark-yun.git
```

##### 下载spark二进制文件

!> 目前只可以使用3.1.1版本

```bash
wget https://archive.apache.org/dist/spark/spark-3.1.1/spark-3.1.1-bin-hadoop3.2.tgz 
tar vzxf spark-3.1.1-bin-hadoop3.2.tgz -C /tmp/
```

##### 构建docker镜像

?> 需要提前[安装docker环境](https://ispong.isxcode.com/linux/docker/docker%20%E5%AE%89%E8%A3%85/)

```bash
./gradlew docker
```

![20230404105427](https://img.isxcode.com/picgo/20230404105427.png)

##### 构建jar包

```bash
./gradlew package
```

##### 容器启动

```bash
docker run --name zhiqingyun -p ${localPort}:8080 -d isxcode/zhiqingyun:${version}
```

##### 访问界面

```text
http://localhost:${localPort}
```
