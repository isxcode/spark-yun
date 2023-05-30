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

##### 4. 修改配置文件

> 根据平台不同，修改目录地址。

```bash
vim spark-yun/spark-yun-backend/src/main/resources/application-local.yml
```

```yml
spark-yun:
  resources-path: ${自定义路径}/.zhiqingyun/resources  # 资源文件目录
  agent-bin-dir: ${项目路径}/spark-yun-dist/src/main/bash # bash脚本路径
  agent-tar-gz-dir: ${项目路径}/spark-yun-dist/build/distributions # 代理包路径
```

##### 5. 下载spark二进制文件

> 解压路径可以自定义

```bash
wget https://archive.apache.org/dist/spark/spark-3.1.1/spark-3.1.1-bin-hadoop3.2.tgz 
tar vzxf spark-3.1.1-bin-hadoop3.2.tgz -C /tmp/
```

> 修改`gradle.properties`文件配置，将spark二进制的文件夹填入

```yaml
SPARK_MIN_DIR=/tmp/spark-3.1.1-bin-hadoop3.2
```

##### 6. 启动项目

> 最好可以访问外网

```bash
cd spark-yun
./gradlew start
```
