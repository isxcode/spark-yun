<div align="center">
  <a href="https://zhiqingyun.isxcode.com">
    <img src="https://img.isxcode.com/picgo/20241209155900.png" alt="image" width="600">
  </a>
</div>

---

<h1 align="center">
  至轻云
</h1>

<h3 align="center">
    超轻量级大数据计算平台/数据中台
</h3>

<div align="center">

[![Docker Pulls](https://img.shields.io/docker/pulls/isxcode/zhiqingyun)](https://hub.docker.com/r/isxcode/zhiqingyun)
[![Build](https://github.com/isxcode/spark-yun/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/isxcode/spark-yun/actions/workflows/build.yml)
[![GitHub Repo stars](https://img.shields.io/github/stars/isxcode/spark-yun)](https://github.com/isxcode/spark-yun)
[![GitHub forks](https://img.shields.io/github/forks/isxcode/spark-yun)](https://github.com/isxcode/spark-yun/fork)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fisxcode%2Fspark-yun.svg?type=shield&issueType=license)](https://app.fossa.com/projects/git%2Bgithub.com%2Fisxcode%2Fspark-yun?ref=badge_shield&issueType=license)
[![GitHub License](https://img.shields.io/github/license/isxcode/spark-yun)](https://github.com/isxcode/spark-yun/blob/main/LICENSE)

</div>

### 产品介绍
  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;至轻云是一款超轻量级、企业级大数据计算平台，基于Spark生态打造。一键部署，开箱即用。快速实现大数据离线ETL、Spark计算、实时计算、可视化调度、自定义接口、数据大屏以及自定义表单等多种功能，为企业提供高效便捷的大数据解决方案。

### 功能特点

- **轻量级产品**: 无需额外组件安装，一键部署，开箱即用。
- **云原生私有化**: 兼容云原生架构，支持多节点安装与高可用集群部署。
- **复杂数据处理**: 基于原生Spark分布式架构，高效地执行复杂数据计算。

### 立即体验

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;演示地址：https://zhiqingyun-demo.isxcode.com </br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;体验账号：user001 </br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;账号密码：welcome1

### 快速部署

> 访问地址：http://localhost:8080 <br/>
> 管理员账号：admin <br/>
> 管理员密码：admin123

```bash
docker run -p 8080:8080 -d isxcode/zhiqingyun
```

### 相关文档

- [快速入门](https://zhiqingyun.isxcode.com/zh/docs/zh/1/0)
- [产品手册](https://zhiqingyun.isxcode.com/zh/docs/zh/2/0)
- [开发手册](https://zhiqingyun.isxcode.com/zh/docs/zh/6/1)
- [博客](https://ispong.isxcode.com/tags/spark/)

### 源码构建

> 注意！！！编译环境需访问外网，且需提前安装Nodejs和Java，推荐版本如下: </br>
> Java: zulu8.78.0.19-ca-jdk8.0.412-x64 </br>
> Nodejs: node-v18.20.3-x64

##### MacOS/Linux

> 安装包路径: spark-yun/spark-yun-dist/build/distributions/zhiqingyun.tar.gz

```bash
git clone https://github.com/isxcode/spark-yun.git
cd spark-yun
./gradlew install clean package
```

##### Windows10/11

> 注意！！！请使用Git Bash工具执行以下命令

```bash
git clone https://github.com/isxcode/spark-yun.git
cd spark-yun
./gradlew.bat install clean package
```

### 联系我们

邮箱：hello@isxcode.com
