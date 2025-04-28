---
title: "Mac源码编译"
---

## 本地源码编译部署

#### Windows10/11 环境编译

推荐版本如下:

- Java: [zulu8.78.0.19-ca-jdk8.0.412-x64 下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/downloads/zulu8.78.0.19-ca-jdk8.0.412-win_x64.msi) 
- Nodejs: [node-v18.20.3-x64 下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/downloads/node-v22.14.0.pkg)
- Gradle: [gradle-7.6.1 下载](https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/downloads/gradle-7.6.1-bin.zip)

#### 源码编译

> 请使用Git Bash终端工具执行以下命令

![20250422140510](https://img.isxcode.com/picgo/20250422140510.png)

```bash
git clone https://github.com/isxcode/spark-yun.git
cd spark-yun
gradle install clean package
```

![20250422141223](https://img.isxcode.com/picgo/20250422141223.png)

#### MacOS/Linux 环境编译

```bash
git clone https://github.com/isxcode/spark-yun.git
cd spark-yun
gradle install clean package
```

#### 解压安装包

> 安装包路径: spark-yun/spark-yun-dist/build/distributions/zhiqingyun.tar.gz  

```bash
tar -vzxf spark-yun-dist/build/distributions/zhiqingyun.tar.gz
java -jar zhiqingyun/lib/zhiqingyun.jar
```

#### 访问项目

- 访问地址: http://localhost:8080 
- 管理员账号：`admin` 
- 管理员密码：`admin123`