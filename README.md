# 至轻云-超轻量级智能化大数据中心

[![Docker Pulls](https://img.shields.io/docker/pulls/isxcode/zhiqingyun)](https://hub.docker.com/r/isxcode/zhiqingyun)
[![build](https://github.com/isxcode/spark-yun/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/isxcode/spark-yun/actions/workflows/build.yml)
[![GitHub Repo stars](https://img.shields.io/github/stars/isxcode/spark-yun)](https://github.com/isxcode/spark-yun)
[![GitHub forks](https://img.shields.io/github/forks/isxcode/spark-yun)](https://github.com/isxcode/spark-yun/fork)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fisxcode%2Fspark-yun.svg?type=shield&issueType=license)](https://app.fossa.com/projects/git%2Bgithub.com%2Fisxcode%2Fspark-yun?ref=badge_shield&issueType=license)
[![GitHub License](https://img.shields.io/github/license/isxcode/spark-yun)](https://github.com/isxcode/spark-yun/blob/main/LICENSE)

|        |                                                                       |
|--------|-----------------------------------------------------------------------|
| 产品官网:  | https://zhiqingyun.isxcode.com                                        |
| 源码仓库:  | https://github.com/isxcode/spark-yun                                  |
| 演示环境:  | https://zhiqingyun-demo.isxcode.com                                   |
| 部署文档:  | https://zhiqingyun.isxcode.com/zh/docs/zh/1/2                         |
| 安装包下载: | https://zhiqingyun-demo.isxcode.com/tools/open/file/zhiqingyun.tar.gz |
| 许可证下载: | https://zhiqingyun-demo.isxcode.com/tools/open/file/license.lic       |
| 友情链接:  | [至数云 - 超轻量级人工智能应用平台](https://zhishuyun.isxcode.com)                    |
| 关键词:   | 大数据计算, AI智能, 数仓, 数据清洗, 数据同步, Spark, Flink, Hadoop, Hive               |
|        |                                                                       |

### 产品介绍

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;至轻云是一款企业级、智能化大数据中心。一键部署，开箱即用。可快速实现大数据计算、数据采集、数据清洗、数据安全、数据质量、数据管理、数据接口开放等功能，助力企业构建新一代智慧数据中心。

### 功能特点

- **轻量级产品**: 无需安装额外组件，一键部署，开箱即用。
- **云原生私有化**: 兼容云原生架构，支持多节点安装与高可用集群部署。
- **分布式计算**: 基于分布式架构，高效执行复杂数据计算。

### 立即体验

> [!TIP]
> 演示地址：https://zhiqingyun-demo.isxcode.com </br>
> 体验账号：zhiyao </br>
> 账号密码：zhiyao123

### 快速部署

> [!NOTE]
> 访问地址：http://localhost:8080 <br/>
> 管理员账号：admin <br/>
> 管理员密码：admin123

```bash
docker run -p 8080:8080 -d isxcode/zhiqingyun
```

### 相关文档

- [快速入门](https://zhiqingyun.isxcode.com/zh/docs/zh/1/0)
- [产品手册](https://zhiqingyun.isxcode.com/zh/docs/zh/2/0)
- [开发手册](https://zhiqingyun.isxcode.com/zh/docs/zh/5/1)
- [博客](https://ispong.isxcode.com/tags/spark/)

### 源码构建

> [!IMPORTANT]
> 安装包路径: /tmp/spark-yun/spark-yun-dist/build/distributions/zhiqingyun.tar.gz

```bash
cd /tmp
git clone https://github.com/isxcode/spark-yun.git
docker run --rm \
  -v /tmp/spark-yun:/spark-yun \
  -w /spark-yun \
  -it registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun-build:latest-amd \
  /bin/bash -c "source /etc/profile && gradle install clean package"
```
