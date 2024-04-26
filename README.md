<div align="center">
  <img width="600" alt="image" src="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/github_product.jpg">
</div>

---

<h1 align="center">
  至轻云
</h1>

<h3 align="center">
  企业级大数据计算平台
</h3>

<div align="center">

![Docker Pulls](https://img.shields.io/docker/pulls/isxcode/zhiqingyun)
![GitHub release (with filter)](https://img.shields.io/github/v/release/isxcode/spark-yun)
![GitHub Repo stars](https://img.shields.io/github/stars/isxcode/spark-yun)
![GitHub forks](https://img.shields.io/github/forks/isxcode/spark-yun)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fisxcode%2Fspark-yun.svg?type=small)](https://app.fossa.com/projects/git%2Bgithub.com%2Fisxcode%2Fspark-yun?ref=badge_small)
![GitHub License](https://img.shields.io/github/license/isxcode/spark-yun)

</div>

### 产品特色

- **代码开源**：允许用户二次自定义功能开发
- **高可用**：支持高可用集群部署，提高用户抗灾能力
- **多租户**：一套系统多部门使用,支持多租户数据隔离
- **云原生**：Docker快速部署，一键启动
- **私有化部署**：可内网部署，提高用户数据安全性
- **spark纯原生**：支持spark官网的所有原生用法

### 产品体验

> 如需产品讲解，请邮箱联系: `hello@isxcode.com`

- 演示环境：https://zhiqingyun-demo.isxcode.com
- 体验账号：user001
- 账号密码：welcome1

### 快速部署

> 推荐使用amd64架构平台

```bash
docker run -p 8080:8080 -d isxcode/zhiqingyun
```

- 访问地址：http://localhost:8080
- 管理员账号：admin
- 管理员密码：admin123

### 相关文档

- [快速入门](https://zhiqingyun.isxcode.com/docs/zh/0/0)
- [使用手册](https://zhiqingyun.isxcode.com/docs/zh/0/0)
- [技术博客](https://ispong.isxcode.com/tags/spark/)
- [常见问题](https://mwur1opms2a.feishu.cn/wiki/space/7350313682586451971?ccm_open_type=lark_wiki_spaceLink&open_tab_from=wiki_home)

### 开发手册

- java-1.8
- nodejs-18.14.2
- vue-3.3.2
- spring-2.7.9

```bash
git clone https://github.com/isxcode/spark-yun.git
gradle install
gradle clean package
# 安装包: spark-yun/spark-yun-dist/build/distributions/zhiqingyun.tar.gz
```

### 关于我们

邮箱：hello@isxcode.com <br/>
微信公众号：<br/>

<img width="230" alt="image" src="https://github.com/ispong/spark-yun/assets/34756621/ae6323bf-3455-434f-a919-949af1eca11f">
