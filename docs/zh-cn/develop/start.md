?> 开发者拉取代码，并本地启动项目

##### 1. 环境安装

> 只要能跑起来，不一定需要根据推荐版本进行安装。

- **Git** (推荐2.x) - https://git-scm.com/
- **Jdk** (推荐Azul-8) - https://www.azul.com/downloads/
- **Nodejs** (推荐LTS-18) - https://nodejs.org/en

##### 2. Fork社区版代码

> 社区版代码只支持社区功能，所有代码授权需要联系申请`授权许可证`。

- Fork链接：https://github.com/isxcode/spark-yun/fork

![20230530164434](https://img.isxcode.com/picgo/20230530164434.png)

##### 3. 拉取代码

```bash
git clone https://github.com/ispong/spark-yun.git
```

##### 4. 下载spark二进制文件

> 解压路径可以自定义

```bash
wget https://archive.apache.org/dist/spark/spark-3.4.0/spark-3.4.0-bin-hadoop3.tgz
tar vzxf spark-3.4.0-bin-hadoop3.tgz -C spark-yun/spark-yun-dist/src/main/
mv spark-3.4.0-bin-hadoop3 spark-min
```

##### 5. 启动项目

> 最好可以访问外网

```bash
cd spark-yun
./gradlew start
```
