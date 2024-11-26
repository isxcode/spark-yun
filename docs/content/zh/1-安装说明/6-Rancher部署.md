---
title: "Rancher部署"
---

> 基于阿里云实现至轻云高可用部署，基于nginx实现负载均衡

##### 资源购买

> 配置：centos7、4核心、16GB内存

- ▪ 服务器1: nfs共享盘服务
- ▪ 磁盘: 挂载到服务器1中
- ▪ 服务器2: nginx负载均衡服务
- ▪ 服务器3: 安装至轻云1
- ▪ 服务器4: 安装至轻云2
- ▪ 服务器5: 安装至轻云3

| 服务器名 | 公网ip          | 内网ip          |
|------|---------------|---------------|
| 服务器1 | 47.92.73.226  | 172.16.215.85 |
| 服务器2 | 39.100.69.110 | 172.16.215.84 |
| 服务器3 | 47.92.198.55  | 172.16.215.86 |
| 服务器4 | 47.92.1.129   | 172.16.215.87 |
| 服务器5 | 47.92.5.96    | 172.16.215.83 |

##### 挂载磁盘

给服务器1挂载磁盘，参考文档：[linux 挂载磁盘](https://ispong.isxcode.com/os/linux/linux%20%E6%8C%82%E8%BD%BD%E7%A3%81%E7%9B%98/)

##### 安装nfs服务

给服务器1安装nfs服务，参考文档：[linux 安装nfs服务](https://ispong.isxcode.com/os/linux/linux%20%E5%AE%89%E8%A3%85nfs%E6%9C%8D%E5%8A%A1/) 

##### 挂载共享盘

给服务器3、服务器4、服务器5挂载服务器的共享盘，参考文档：[linux 挂载共享盘](https://ispong.isxcode.com/os/linux/linux%20%E6%8C%82%E8%BD%BD%E5%85%B1%E4%BA%AB%E7%A3%81%E7%9B%98/)

##### 安装至轻云服务

给服务器3、服务器4、服务器5安装至轻云服务，参考文档：[至轻云 安装包部署](https://zhiqingyun.isxcode.com/docs/zh/1/2)

#### 注意,修改配置文件，将资源文件目录指向共享盘

> 包括： spring.datasource.url和isx-app.resources-path

```bash
vim zhiqingyun/conf/application-local.yml
```

```yaml
server:
  port: 8080

spring:

  security:
    user:
      name: admin
      password: admin123

  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:file:/data/zhiqingyun/h2/data;AUTO_SERVER=TRUE
    username: root
    password: root1234

isx-app:
  resources-path: /data/zhiqingyun
  admin-passwd: admin123
  use-ssl: false
```

#### 注意，复制默认数据库驱动文件到共享盘

> 只需要执行一次,不需要重复执行

```bash
mkdir -p /data/zhiqingyun
cp -r zhiqingyun/resources/jdbc /data/zhiqingyun/
```

> 启动项目

三台服务器都要启动至轻云服务

```bash
bash zhiqingyun/bin/start.sh
```

##### 安装nginx负载服务

给服务器2安装nginx负载服务 参考文档：[nginx 负载均衡配置](https://ispong.isxcode.com/vue/nginx/nginx%20%E8%B4%9F%E8%BD%BD%E5%9D%87%E8%A1%A1%E9%85%8D%E7%BD%AE/)

##### 通过nginx访问至轻云

▪ 网址：http://39.100.69.110 <br/>
▪ 账号：admin <br/>
▪ 密码：admin123