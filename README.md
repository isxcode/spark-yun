# 至轻云-超轻量智能数据中心

[![Docker Pulls](https://img.shields.io/docker/pulls/isxcode/zhiqingyun)](https://hub.docker.com/r/isxcode/zhiqingyun)
[![build](https://github.com/isxcode/spark-yun/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/isxcode/spark-yun/actions/workflows/build.yml)
[![GitHub Repo stars](https://img.shields.io/github/stars/isxcode/spark-yun)](https://github.com/isxcode/spark-yun)
[![GitHub forks](https://img.shields.io/github/forks/isxcode/spark-yun)](https://github.com/isxcode/spark-yun/fork)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fisxcode%2Fspark-yun.svg?type=shield&issueType=license)](https://app.fossa.com/projects/git%2Bgithub.com%2Fisxcode%2Fspark-yun?ref=badge_shield&issueType=license)
[![GitHub License](https://img.shields.io/github/license/isxcode/spark-yun)](https://github.com/isxcode/spark-yun/blob/main/LICENSE)

<table>
    <tr>
        <td>产品官网</td>
        <td><a href="https://zhiqingyun.isxcode.com">https://zhiqingyun.isxcode.com</a></td>
    </tr>
    <tr>
        <td>源码仓库</td>
        <td><a href="https://github.com/isxcode/spark-yun">https://github.com/isxcode/spark-yun</a></td>
    </tr>
    <tr>
        <td>码云仓库</td>
        <td><a href="https://gitee.com/isxcode/spark-yun">https://gitee.com/isxcode/spark-yun</a></td>
    </tr>
    <tr>
        <td>演示环境</td>
        <td><a href="https://zhiqingyun-demo.isxcode.com">https://zhiqingyun-demo.isxcode.com</a></td>
    </tr>
    <tr>
        <td>产品推荐</td>
        <td><a href="https://zhishuyun.isxcode.com">[至数云] - 超轻量智能应用平台</a></td>
    </tr>
    <tr>
        <td>关键词</td>
        <td>大数据计算, 智数, 湖仓, 数据中台, 数据同步, Spark, Flink, Hadoop, Doris, Hive</td>
    </tr>
</table>

### 产品介绍

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;至轻云是一款企业级、智能化大数据中心。一键部署，开箱即用。可快速实现大数据计算、数据采集、数据清洗、数据安全、数据质量、数据管理、数据接口开放等功能，助力企业构建新一代智慧数据中心。

### 功能列表

| 模块     | 功能（包含商业版）                                                             |
| :------- | :----------------------------------------------------------------------------- |
| 至轻智能 | 智能问答、提示词配置、MCP一键分享                                              |
| 资源管理 | 计算集群、数据源、驱动管理、计算容器、文件管理                                 |
| 数据规划 | 数据分层、数据标准                                                             |
| 项目管理 | 项目列表、资源审批                                                             |
| 数据开发 | 数据建模、作业开发、实时计算、计算容器、函数仓库、全局变量、依赖合集、发布审批 |
| 数据运维 | 资源总览、调度历史、基线告警                                                   |
| 数据监控 | 监控总览、结构采集、数据中心                                                   |
| 数据治理 | 治理总览、数据质量、治理工单                                                   |
| 数据安全 | 审计总览、分类分级、敏感数据、我的数据、权限审批                               |
| 数据资产 | 资产总览、数据指标、数据标签、数据目录、数据地图                               |
| 数据服务 | 数据大屏、接口服务、接口日志、黑白名单、数据报表、表单管理、分享表单           |
| 后台管理 | 租户成员、角色管理、组织架构、通知配置、智能配置、后台设置                     |
| 平台管理 | 用户中心、租户管理、免密登录、登录日志、行为日志、平台授权、平台设置           |
| 个人中心 | 基础信息、修改密码、修改手机、修改邮箱、偏好设置、消息中心                     |

### 立即体验

演示地址：https://zhiqingyun-demo.isxcode.com </br>
体验账号：短信注册「添加咨询微信，审批账号」</br>
体验时间：1天 </br>
咨询微信：fanZqyccc

### 快速部署

```bash
# 访问地址：http://localhost:8080
# 管理员账号：admin 
# 管理员密码：admin123
docker run -p 8080:8080 -d isxcode/zhiqingyun
```

### 相关文档

- [快速体验](https://zhiqingyun.isxcode.com/zh/docs/1/0)
- [产品手册](https://zhiqingyun.isxcode.com/zh/docs/2/0)
- [开发手册](https://zhiqingyun.isxcode.com/zh/docs/5/3)
- [博客](https://ispong.isxcode.com)

### 源码构建

- Mac或Linux

```bash

# 安装包路径: /tmp/spark-yun/spark-yun-dist/build/distributions/zhiqingyun.tar.gz
cd /tmp
git clone https://github.com/isxcode/spark-yun.git
docker run --rm \
  -v /tmp/spark-yun:/spark-yun \
  -it isxcode/zhiqingyun-build \
  gradle package
```

- Windows

```bash
# 安装包路径: C:\Users\isxcode\Downloads\spark-yun\spark-yun-dist\build\distributions\zhiqingyun.tar.gz
cd Downloads
git clone https://github.com/isxcode/spark-yun.git
docker run --rm ^
  -v C:\Users\isxcode\Downloads\spark-yun:/spark-yun ^
  -it isxcode/zhiqingyun-build ^
  gradle package
```

### 产品展示

<table>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/sy-1.png" alt="1" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/sy-2.png" alt="2" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/sy-3.png" alt="3" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/sy-4.png" alt="4" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/sy-5.png" alt="5" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/sy-6.png" alt="6" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/sy-7.png" alt="7" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/sy-8.png" alt="8" width="400"/></td>
    </tr>
    <tr>
       <td><img src="https://img.isxcode.com/picgo/sy-9.png" alt="9" width="400"/></td>
       <td><img src="https://img.isxcode.com/picgo/sy-10.png" alt="10" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/sy-11.png" alt="11" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/sy-12.png" alt="12" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/sy-13.png" alt="13" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/sy-14.png" alt="14" width="400"/></td>
    </tr>
</table>
