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
wget https://archive.apache.org/dist/spark/spark-3.4.0/spark-3.4.0-bin-hadoop3.tgz
tar vzxf spark-3.4.0-bin-hadoop3.tgz --strip-components=1 -C spark-yun/spark-yun-dist/src/main/spark-min
```

##### 企业版（可选）

> 授权可拉取代码

```bash
cd spark-yun
git clone https://github.com/isxcode/spark-yun-vip.git
```

##### 启动项目

```bash
cd spark-yun
./gradlew start
```

![img](https://img.isxcode.com/picgo/20230527155307.png)