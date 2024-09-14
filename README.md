<div align="center">
  <img width="600" alt="image" src="https://img.isxcode.com/picgo/20240914152714.png">
</div>

---

<h1 align="center">
  至轻云
</h1>

<h3 align="center">
  打造企业级超轻量大数据计算平台
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
  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; [**至轻云**](https://zhiqingyun.isxcode.com)是一款超轻量级、企业级大数据平台，具备一键部署、开箱即用的特点。无需安装额外的大数据组件，即可快速实现企业级的离线ETL、实时计算和作业定时调度等功能，帮助企业高效处理海量数据，挖掘更多商业价值。

### 功能特点

- **超轻量**: 无需预装任何中间组件，一键部署即可实现大数据计算。
- **多租户**: 支持创建多租户，实现租户间的数据完全隔离。
- **高可用**: 兼容云原生架构，支持多节点安装，确保高可用性。
- **私有化部署**: 支持内网部署与访问，提升系统安全性。
- **代码开源**: 永久开源且免费，实时公开产品进度，内容持续更新。

### 立即体验

&nbsp;&nbsp;&nbsp;&nbsp; 演示地址：https://zhiqingyun-demo.isxcode.com </br>
&nbsp;&nbsp;&nbsp;&nbsp; 体验账号：user001 </br>
&nbsp;&nbsp;&nbsp;&nbsp; 账号密码：welcome1

### 快速部署

> 至轻云地址：http://localhost:8080 <br/>
> 管理员账号：admin <br/>
> 管理员密码：admin123

```bash
docker run -p 8080:8080 -d isxcode/zhiqingyun
```

### 源码构建

##### mac/linux

> 打包结果路径: spark-yun/spark-yun-dist/build/distributions/zhiqingyun.tar.gz

```bash
git clone https://github.com/isxcode/spark-yun.git
cd spark-yun
./gradlew install package
```

##### windows

> 注意！请使用git bash工具执行命令

```bash
git clone https://github.com/isxcode/spark-yun.git
cd spark-yun
./gradlew.bat install package
```

### 相关文档

- [快速入门](https://zhiqingyun.isxcode.com/docs/zh/0/0)
- [产品手册](https://zhiqingyun.isxcode.com/docs/zh/2/0)
- [安装文档](https://zhiqingyun.isxcode.com/docs/zh/1/0-docker)
- [博客](https://ispong.isxcode.com/tags/spark/)

### 联系我们

邮箱：hello@isxcode.com

---

**Thanks for free JetBrains Open Source license**

<a href="https://www.jetbrains.com/?from=spark-yun" target="_blank" style="border-bottom: none !important;">
    <img src="https://img.isxcode.com/index_img/jetbrains/jetbrains-3.png" height="100" alt="jetbrains"/>
</a>


