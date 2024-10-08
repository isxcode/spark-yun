<div align="center">
  <img width="600" alt="image" src="https://img.isxcode.com/picgo/20241002182632.png">
</div>

---

<h1 align="center">
  至轻云
</h1>

<h3 align="center">
  超轻量级Spark计算平台
</h3>

<div align="center">

[![Docker Pulls](https://img.shields.io/docker/pulls/isxcode/zhiqingyun)](https://hub.docker.com/r/isxcode/zhiqingyun)
[![GitHub release (with filter)](https://img.shields.io/github/v/release/isxcode/spark-yun)](https://github.com/isxcode/spark-yun/releases)
[![GitHub Repo stars](https://img.shields.io/github/stars/isxcode/spark-yun)](https://github.com/isxcode/spark-yun)
[![GitHub forks](https://img.shields.io/github/forks/isxcode/spark-yun)](https://github.com/isxcode/spark-yun/fork)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fisxcode%2Fspark-yun.svg?type=shield&issueType=license)](https://app.fossa.com/projects/git%2Bgithub.com%2Fisxcode%2Fspark-yun?ref=badge_shield&issueType=license)
[![GitHub License](https://img.shields.io/github/license/isxcode/spark-yun)](https://github.com/isxcode/spark-yun/blob/main/LICENSE)

</div>

### 产品介绍
  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; [**至轻云**](https://zhiqingyun.isxcode.com)是一款超轻量级、企业级大数据计算产品，围绕Spark生态打造的数据计算平台。一键部署，开箱即用。可快速实现Spark计算、大数据离线ETL、实时计算、作业定时调度等功能。

### 功能特点

- **超轻量级**: 无需预装任何组件，一键部署，开箱即用。
- **私有化部署**: 兼容云原生架构，支持多节点安装，高可用集群部署。
- **可视化操作**: 基于Spark原生打造，快速执行Spark计算。

### 立即体验

> 演示地址：https://zhiqingyun-demo.isxcode.com </br>
> 体验账号：user001 </br>
> 账号密码：welcome1

### 快速部署

> 访问地址：http://localhost:8080 <br/>
> 管理员账号：admin <br/>
> 管理员密码：admin123

```bash
docker run -p 8080:8080 -d isxcode/zhiqingyun
```

### 相关文档

- [快速入门](https://zhiqingyun.isxcode.com/docs/zh/0/0)
- [产品手册](https://zhiqingyun.isxcode.com/docs/zh/2/0)
- [安装文档](https://zhiqingyun.isxcode.com/docs/zh/1/0-docker)
- [博客](https://ispong.isxcode.com/tags/spark/)

### 源码构建

##### mac/linux

> 打包路径: spark-yun/spark-yun-dist/build/distributions/zhiqingyun.tar.gz

```bash
git clone https://github.com/isxcode/spark-yun.git
cd spark-yun
./gradlew install clean package start
```

##### windows

> 注意！请使用Git Bash工具执行命令

```bash
git clone https://github.com/isxcode/spark-yun.git
cd spark-yun
./gradlew.bat install clean package start
```

### 联系我们

邮箱：hello@isxcode.com

---

**Thanks for free JetBrains Open Source license**

<a href="https://www.jetbrains.com/?from=spark-yun" target="_blank" style="border-bottom: none !important;">
    <img src="https://img.isxcode.com/index_img/jetbrains/jetbrains-3.png" height="100" alt="jetbrains"/>
</a>


