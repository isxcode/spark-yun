# 至轻云-超轻量级智能化大数据中心

[![Docker Pulls](https://img.shields.io/docker/pulls/isxcode/zhiqingyun)](https://hub.docker.com/r/isxcode/zhiqingyun)
[![build](https://github.com/isxcode/spark-yun/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/isxcode/spark-yun/actions/workflows/build.yml)
[![GitHub Repo stars](https://img.shields.io/github/stars/isxcode/spark-yun)](https://github.com/isxcode/spark-yun)
[![GitHub forks](https://img.shields.io/github/forks/isxcode/spark-yun)](https://github.com/isxcode/spark-yun/fork)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fisxcode%2Fspark-yun.svg?type=shield&issueType=license)](https://app.fossa.com/projects/git%2Bgithub.com%2Fisxcode%2Fspark-yun?ref=badge_shield&issueType=license)
[![GitHub License](https://img.shields.io/github/license/isxcode/spark-yun)](https://github.com/isxcode/spark-yun/blob/main/LICENSE)

<table>
    <tr>
        <td>产品官网</td>
        <td>https://zhiqingyun.isxcode.com</td>
    </tr>
    <tr>
        <td>源码仓库</td>
        <td>https://github.com/isxcode/spark-yun</td>
    </tr>
    <tr>
        <td>演示环境</td>
        <td>https://zhiqingyun-demo.isxcode.com</td>
    </tr>
    <tr>
        <td>部署文档</td>
        <td>https://zhiqingyun.isxcode.com/zh/docs/zh/1/2</td></tr>
    <tr>
        <td>安装包下载</td>
        <td>https://zhiqingyun-demo.isxcode.com/tools/open/file/zhiqingyun.tar.gz</td>
    </tr>
    <tr>
        <td>许可证下载</td>
        <td>https://zhiqingyun-demo.isxcode.com/tools/open/file/license.lic</td>
    </tr>
    <tr>
        <td>友情链接</td>
        <td><a href="https://zhishuyun.isxcode.com">[至数云] - 超轻量级人工智能应用平台</a></td>
    </tr>
    <tr>
        <td>关键词</td>
        <td>大数据计算, 智数, 湖仓, 数据中台, 数据同步, Spark, Flink, Hadoop, Doris, Hive</td>
    </tr>
</table>

### 产品介绍

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;至轻云是一款企业级、智能化大数据中心。一键部署，开箱即用。可快速实现大数据计算、数据采集、数据清洗、数据安全、数据质量、数据管理、数据接口开放等功能，助力企业构建新一代智慧数据中心。

### 功能列表

| 模块   | 功能                       |
|------|--------------------------|
| 首页   | 系统监控、集群监控、实例监控           |
| 资源管理 | 计算集群、数据源、驱动管理、资源中心       |
| 数据规划 | 数据分层、数据标准、数据建模           |
| 数据开发 | 作业流、函数仓库、全局变量、实时计算、计算容器  |
| 任务调度 | 调度历史                     |
| 基线告警 | 消息体、基线配置、告警实例            |
| 元数据  | 数据地图、数据采集、采集实例           |
| 数据服务 | 数据卡片、数据大屏、接口服务、分享表单      |
| 后台管理 | 用户中心、租户列表、租户成员、免密登录、证书安装 |

### 立即体验

演示地址：https://zhiqingyun-demo.isxcode.com </br>
体验账号：zhiyao </br>
账号密码：zhiyao123

### 相关文档

- [快速入门](https://zhiqingyun.isxcode.com/zh/docs/zh/1/0)
- [产品手册](https://zhiqingyun.isxcode.com/zh/docs/zh/2/0)
- [开发手册](https://zhiqingyun.isxcode.com/zh/docs/zh/5/1)
- [博客](https://ispong.isxcode.com/tags/spark/)

### 快速部署

```bash
# 访问地址：http://localhost:8080
# 管理员账号：admin 
# 管理员密码：admin123
docker run -p 8080:8080 -d isxcode/zhiqingyun
```

### 源码构建

```bash
# 安装包路径: /tmp/spark-yun/spark-yun-dist/build/distributions/zhiqingyun.tar.gz
cd /tmp
git clone https://github.com/isxcode/spark-yun.git
docker run --rm \
  -v /tmp/spark-yun:/spark-yun \
  -w /spark-yun \
  -it registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun-build:latest-amd \
  /bin/bash -c "source /etc/profile && gradle install clean package"
```

### 产品展示

<table>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/4.png" alt="登录页" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/3.png" alt="首页" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/13.png" alt="数据源" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/5.png" alt="作业流" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/11.png" alt="pySpark" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/12.png" alt="运行结果" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/14.png" alt="作业配置" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/10.png" alt="任务调度" width="400"/></td>
    </tr>
    <tr>
       <td><img src="https://img.isxcode.com/picgo/1.png" alt="数据分层" width="400"/></td>
       <td><img src="https://img.isxcode.com/picgo/2.png" alt="数据地图" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/8.png" alt="自定义接口" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/7.png" alt="分享表单" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/9.png" alt="数据分析" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/6.png" alt="数据大屏" width="400"/></td>
    </tr>
</table>