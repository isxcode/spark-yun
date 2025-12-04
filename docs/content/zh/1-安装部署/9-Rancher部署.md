---
title: "Rancher部署"
---

> 使用Rancher部署至轻云

#### 本地下载镜像

```bash
docker pull registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64
docker save registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64 -o /Users/ispong/Downloads/zhiqingyun-image-20251204.tar.gz
```

#### 推送到镜像仓库

```bash
scp /Users/ispong/Downloads/zhiqingyun-image-20251204.tar.gz root@120.55.168.57:/tmp
docker load -i /tmp/zhiqingyun-image-20251204.tar.gz

# 设置日期版本号
docker tag registry.cn-shanghai.aliyuncs.com/isxcode/zhiqingyun:latest-amd64 172.19.189.246:30003/library/isxcode/zhiqingyun:20251204

# 推送到镜像仓库
docker push 172.19.189.246:30003/library/isxcode/zhiqingyun:20251204
```

#### 创建Namespace

```bash
kubectl create namespace isxcode
```

#### 添加镜像仓库密钥

![20251204163840](https://img.isxcode.com/picgo/20251204163840.png)

![20251204163859](https://img.isxcode.com/picgo/20251204163859.png)

![20251204164254](https://img.isxcode.com/picgo/20251204164254.png)

#### 创建ConfigMap

> 下载配置文件

```bash
wget https://raw.githubusercontent.com/isxcode/spark-yun/refs/heads/main/spark-yun-backend/spark-yun-main/src/main/resources/application-docker.yml -O  /Users/ispong/Downloads/application-docker.yml
```

![20251204164609](https://img.isxcode.com/picgo/20251204164609.png)

![20251204164623](https://img.isxcode.com/picgo/20251204164623.png)

#### 创建Pvc

![20251204164805](https://img.isxcode.com/picgo/20251204164805.png)

#### 创建Deployment

![20251204164915](https://img.isxcode.com/picgo/20251204164915.png)

#### 挂载存储

> 挂载pvc

![20251204165517](https://img.isxcode.com/picgo/20251204165517.png)

> 挂载configMap

![20251204165534](https://img.isxcode.com/picgo/20251204165534.png)

> 选择对应的配置 zhiqingyun-pvc 和 zhiqingyun-config

![20251204165602](https://img.isxcode.com/picgo/20251204165602.png)

#### 映射存储

> pvc映射 /var/lib/zhiqingyun -->  data  (/data/local-path-provisioner/data)
> configMap映射 /etc/zhiqingyun/conf 注意：这里映射只能是目录

![20251204184938](https://img.isxcode.com/picgo/20251204184938.png)

#### 配置镜像

> 镜像：172.19.189.246:30003/library/isxcode/zhiqingyun:20251204

![20251204170157](https://img.isxcode.com/picgo/20251204170157.png)

#### 端口号映射

> Node Port : zhiqingyun-port
> 8080 > 30167

![20251204170418](https://img.isxcode.com/picgo/20251204170418.png)

#### 环境变量

> ADMIN_PASSWORD : admin12345

![20251204170316](https://img.isxcode.com/picgo/20251204170316.png)

#### 访问项目

- 访问地址：http://120.55.168.57:30167
- 管理员账号：admin
- 配置密码：admin12345