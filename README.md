# 至轻云-轻量级智能数据中心

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
        <td>托管平台</td>
        <td><a href="https://zhiqingyun-saas.isxcode.com">https://zhiqingyun-saas.isxcode.com</a></td>
    </tr>
    <tr>
        <td>GitHub</td>
        <td><a href="https://github.com/isxcode/spark-yun">https://github.com/isxcode/spark-yun</a></td>
    </tr>
    <tr>
        <td>Gitee</td>
        <td><a href="https://gitee.com/isxcode/spark-yun">https://gitee.com/isxcode/spark-yun</a></td>
    </tr>
    <tr>
        <td>友情推荐</td>
        <td><a href="https://zhishuyun.isxcode.com">[至数云] - 轻量级智能应用平台</a></td>
    </tr>
    <tr>
        <td>关键词</td>
        <td>大数据, 智数, 数仓, 湖仓, 中台, 数据治理, Spark, Flink, Hadoop, Doris, Hive</td>
    </tr>
</table>

### 产品介绍

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;至轻云是一款企业级、智能化数据中心。一键部署，开箱即用。可快速实现大数据计算、数据规划、数据研发、数据监控、数据治理、数据安全、数据资产、数据服务、数据应用等功能，助力企业构建新一代智慧数据中心。

### 功能列表「社区开源」

| 模块     | 功能                                                       |
| :------- | :--------------------------------------------------------- |
| 资源管理 | 计算集群、数据源、驱动管理、资源中心                       |
| 数据开发 | 作业流、函数仓库                                           |
| 运维监控 | 资源总览、调度历史                                         |
| 后台管理 | 租户成员、角色管理                                         |
| 平台管理 | 用户中心、租户管理、登录方式、登录日志、行为日志、平台授权 |
| 个人中心 | 基础信息、修改密码、修改手机、修改邮箱、修改语言           |

### 功能列表「企业合作」

| 模块     | 功能                                                                           |
| :------- | :----------------------------------------------------------------------------- |
| 至轻智能 | 智能问数、提示词管理、MCP管理                                                  |
| 资源管理 | 计算集群、数据来源、计算容器、应用连接、存储资源、文件资源                     |
| 数据规划 | 数据分层、数据架构、代码标准、字段标准                                         |
| 项目管理 | 项目列表、项目成员、项目资源                                                   |
| 数据研发 | 数据建模、数据开发、实时计算、全局变量、函数仓库、依赖合集                     |
| 数据运维 | 运维总览、发布审批、基线告警、调度实例                                         |
| 数据监控 | 监控总览、结构采集、采集实例、数据中心                                         |
| 数据质量 | 质量总览、质量规则、质量方案、质量工单                                         |
| 数据管理 | 数据总览、数据融合、数据维护、数据审批                                         |
| 数据安全 | 安全总览、分级分类、敏感加密、权限审批、访问审计                               |
| 数据资产 | 资产总览、数据指标、数据标签、我的数据、数据目录、数据地图                     |
| 数据服务 | 服务总览、黑白名单、数据转发、接口服务                                         |
| 数据应用 | 数据市场、数据报表、数据大屏、应用表单                                         |
| 后台管理 | 租户成员、角色管理、组织架构、通知配置、智能配置、后台设置                     |
| 平台管理 | 用户中心、租户管理、免密登录、登录方式、登录日志、行为日志、平台授权、平台设置 |
| 个人中心 | 基础信息、修改密码、修改手机、修改邮箱、令牌安全、偏好设置、消息中心           |

### 托管平台

平台地址：https://zhiqingyun-saas.isxcode.com </br>
账号注册：登录方式 > 短信登录 「添加咨询微信，申请审批」</br>
咨询微信：fanZqyccc

### 快速部署

```bash
# 访问地址：http://localhost:8080
# 管理员账号：admin 
# 管理员密码：admin123
docker run -p 8080:8080 -d isxcode/zhiqingyun
```

### 源码构建

- Mac或Linux「terminal中执行」

```bash
# 安装包路径: /tmp/spark-yun/spark-yun-dist/build/distributions/zhiqingyun.tar.gz
cd /tmp
git clone https://github.com/isxcode/spark-yun.git
docker run --rm \
  -v /tmp/spark-yun:/spark-yun \
  -it isxcode/zhiqingyun-build \
  gradle package
```

- Windows「CMD中执行」

```bash
# 安装包路径: C:\Users\isxcode\Downloads\spark-yun\spark-yun-dist\build\distributions\zhiqingyun.tar.gz
cd Downloads
git clone https://github.com/isxcode/spark-yun.git
docker run --rm ^
  -v C:\Users\isxcode\Downloads\spark-yun:/spark-yun ^
  -it isxcode/zhiqingyun-build ^
  gradle package
```

### 相关文档

- [快速体验](https://zhiqingyun.isxcode.com/zh/docs/1/0)
- [产品手册](https://zhiqingyun.isxcode.com/zh/docs/2/0)
- [开发手册](https://zhiqingyun.isxcode.com/zh/docs/5/3)
- [博客](https://ispong.isxcode.com/tags/)

### 产品展示

<table>
    <tr>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-5.png" alt="1" width="400"/></td>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-6.png" alt="2" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-7.png" alt="3" width="400"/></td>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-8.png" alt="4" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-9.png" alt="5" width="400"/></td>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-10.png" alt="6" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-11.png" alt="7" width="400"/></td>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-12.png" alt="8" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-13.png" alt="9" width="400"/></td>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-14.png" alt="10" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-15.png" alt="11" width="400"/></td>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-16.png" alt="12" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-17.png" alt="13" width="400"/></td>
        <td><img src="https://zhiqingyun-saas.isxcode.com/tools/open/file/web-product-18.png" alt="14" width="400"/></td>
    </tr>
</table>
  
