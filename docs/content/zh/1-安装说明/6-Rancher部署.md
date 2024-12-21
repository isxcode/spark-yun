---
title: "Rancher部署"
---


## 使用Helm离线安装Rancher2.8.5

#### 关闭防火墙

```bash
sudo systemctl disable firewalld
sudo systemctl stop firewalld
sudo systemctl status firewalld
```

#### 修改hostname

```bash
sudo hostnamectl set-hostname isxcode
sudo vim /etc/hosts
```

```bash
172.16.215.83   isxcode
```

#### 关闭selinux

```bash
sudo setenforce 0
sudo getenforce
```

#### 关闭swap分区

```bash
sudo swapoff -a
sudo free -m
```

#### 挂载磁盘

> 挂载磁盘，绑定/data

```bash
sudo mkdir -p /data
```

#### 上传资源

> 需要资源邮箱找我

```bash
scp -r /Users/ispong/OneDrive/Downloads/rancher root@47.92.128.32:/tmp
```

#### 离线安装docker

```bash
cd /tmp/rancher
tar -xvf docker-19.03.9.tgz
sudo cp docker/* /usr/bin
sudo vim /etc/systemd/system/docker.service
```

```bash
[Unit]
Description=Docker Application Container Engine
Documentation=https://docs.docker.com
After=network-online.target firewalld.service
Wants=network-online.target

[Service]
Type=notify
ExecStart=/usr/bin/dockerd
ExecReload=/bin/kill -s HUP $MAINPID
TimeoutSec=0
RestartSec=2
Restart=always
StartLimitBurst=3
StartLimitInterval=60s
LimitNOFILE=infinity
LimitNPROC=infinity
LimitCORE=infinity
TasksMax=infinity
Delegate=yes
KillMode=process

[Install]
WantedBy=multi-user.target
```

```bash
sudo chmod +x /etc/systemd/system/docker.service

sudo mkdir -p /data/docker
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "data-root":"/data/docker"
}
EOF
sudo systemctl daemon-reload
sudo systemctl enable docker
sudo systemctl start docker
sudo systemctl status docker

# 赋予权限
sudo chown ispong:ispong /usr/bin/docker
sudo chown ispong:ispong /var/run/docker.sock
```

#### 离线安装docker-compose

```bash
cd /tmp/rancher
sudo cp docker-compose-linux-x86_64 /usr/bin/docker-compose
sudo chmod +x /usr/bin/docker-compose
docker-compose --version
```

#### 生成harbor的ssl证书

> 注意CN修改域名
> 将命令中的isxcode替换成对应的hostname，再执行

```bash
sudo mkdir -p /data/harbor/ssl
cd /data/harbor/ssl

sudo openssl genrsa -out ca.key 4096
sudo openssl req -x509 -new -nodes -sha512 -days 3650 \
 -subj "/C=CN/ST=Beijing/L=Beijing/O=example/OU=Personal/CN=isxcode" \
 -key ca.key \
 -out ca.crt

sudo openssl genrsa -out isxcode.key 4096
sudo openssl req -sha512 -new \
    -subj "/C=CN/ST=Beijing/L=Beijing/O=example/OU=Personal/CN=isxcode" \
    -key isxcode.key \
    -out isxcode.csr

sudo touch v3.ext
sudo chown ispong:ispong v3.ext
cat > v3.ext <<-EOF
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
extendedKeyUsage = serverAuth
subjectAltName = @alt_names

[alt_names]
DNS.1=isxcode
DNS.2=isxcode
DNS.3=isxcode
EOF

sudo openssl x509 -req -sha512 -days 3650 \
    -extfile v3.ext \
    -CA ca.crt -CAkey ca.key -CAcreateserial \
    -in isxcode.csr \
    -out isxcode.crt

sudo openssl x509 -inform PEM -in isxcode.crt -out isxcode.cert

sudo mkdir -p /data/harbor/data/cert
sudo cp isxcode.crt /data/harbor/data/cert
sudo cp isxcode.key /data/harbor/data/cert

sudo mkdir -p /etc/docker/certs.d/isxcode:8443/
sudo cp isxcode.cert /etc/docker/certs.d/isxcode:8443/
sudo cp isxcode.key /etc/docker/certs.d/isxcode:8443/
sudo cp ca.crt /etc/docker/certs.d/isxcode:8443/
sudo systemctl daemon-reload
sudo systemctl restart docker

sudo cp /etc/docker/certs.d/isxcode:8443/* /etc/pki/ca-trust/source/anchors/
sudo update-ca-trust
```

#### 离线安装harbor

```bash
sudo mkdir -p /data/harbor/data

cd /tmp/rancher
docker load -i prepare-1.9.3.tar
sudo tar zxf harbor-offline-installer-v1.9.3.tgz -C /data/harbor/
cd /data/harbor/harbor

sudo vim harbor.yml 
```

> 修改hostname 
> 修改https 
> 修改port 
> 修改data

```bash
hostname: isxcode

http:
  port: 8800

https:
  port: 8443
  certificate: /data/harbor/ssl/isxcode.crt
  private_key: /data/harbor/ssl/isxcode.key

data_volume: /data/harbor/data
```

> cd /var/log/harbor 看日志
> docker-compose down -v
> docker-compose up -d

```bash
sudo ./prepare
sudo chmod +x ./install.sh
sudo ./install.sh
docker ps -a
```

访问地址: https://47.92.209.39:8443 
账号: admin 
密码: Harbor12345

#### 创建rke2目录

```bash
sudo mkdir -p /data/rancher
sudo ln -s /data/rancher /var/lib/rancher

sudo mkdir -p /data/containers
sudo ln -s /data/containers /var/lib/containers
```

#### 安装rke2

```bash
sudo mkdir -p /data/rke2-artifacts
sudo cp /tmp/rancher/rke2-images.linux-amd64.tar.zst /data/rke2-artifacts/
sudo cp /tmp/rancher/rke2.linux-amd64.tar.gz /data/rke2-artifacts/
sudo cp /tmp/rancher/sha256sum-amd64.txt /data/rke2-artifacts/

# 必须要使用root用户执行
cd /tmp/rancher
INSTALL_RKE2_ARTIFACT_PATH=/data/rke2-artifacts sh install.sh
```

#### 启动rke2

> journalctl -u rke2-server -f  看日志

```bash
sudo systemctl enable rke2-server.service
sudo systemctl start rke2-server.service
sudo systemctl status rke2-server.service
```

#### 检查服务是否启动

> 此时k8s已经安装好了

```bash
sudo tee -a /etc/profile <<-'EOF'
export KUBECONFIG=/etc/rancher/rke2/rke2.yaml 
export PATH=$PATH:/var/lib/rancher/rke2/bin
EOF
source /etc/profile
sudo chown ispong:ispong /etc/rancher/rke2/rke2.yaml
kubectl get nodes
kubectl get pods -n kube-system
```

#### 离线安装helm

```bash
cd /tmp/rancher
tar -zxvf helm-v3.15.3-linux-amd64.tar.gz
sudo mv /tmp/rancher/linux-amd64/helm /usr/bin/helm
helm version
```

#### 导入rancher镜像 v2.8.5

> username: admin 
> password: Harbor12345

```bash
docker login isxcode:8443
```

```bash
cd /tmp/rancher
chmod +x rancher-load-images.sh
./rancher-load-images.sh --image-list ./rancher-images.txt --registry isxcode:8443/library
```

#### 修改tls认证

```bash
sudo vim /etc/rancher/rke2/config.yaml
```

```bash
tls-san:
  - isxcode
```

> 将isxcode换成对应的hostname

```bash
sudo vim /etc/rancher/rke2/registries.yaml
```

```yml
mirrors:
  docker.io:
    endpoint:
      - "https://isxcode:8443"
configs:
  "https://isxcode:8443":
    auth:
      username: admin
      password: Harbor12345
    tls:
      cert_file: /data/harbor/ssl/isxcode.cert
      key_file: /data/harbor/ssl/isxcode.key
      ca_file: /data/harbor/ssl/ca.crt
```

```bash
sudo systemctl restart rke2-server.service
```

#### k8s安装自签证书

```bash
cd /tmp/rancher
kubectl create namespace cert-manager
kubectl apply -f ./cert-manager-crd.yaml
helm install cert-manager /tmp/rancher/cert-manager-v1.15.1.tgz \
    --namespace cert-manager \
    --set image.repository=docker.io/library/quay.io/jetstack/cert-manager-controller \
    --set webhook.image.repository=docker.io/library/quay.io/jetstack/cert-manager-webhook \
    --set cainjector.image.repository=docker.io/library/quay.io/jetstack/cert-manager-cainjector \
    --set startupapicheck.image.repository=docker.io/library/quay.io/jetstack/cert-manager-startupapicheck \
    --debug
helm list -A
```

#### k8s安装rancher

> 卸载：helm uninstall rancher -n cattle-system 
> 记得修改hostname

```bash
cd /tmp/rancher
kubectl create namespace cattle-system
helm install rancher /tmp/rancher/rancher-2.8.5.tgz \
  --namespace cattle-system \
  --set hostname=isxcode \
  --set certmanager.version=1.15.1 \
  --set rancherImage=docker.io/library/rancher/rancher \
  --set useBundledSystemChart=true \
  --set systemDefaultRegistry=docker.io/library \
  --set rancherImageTag=v2.8.5 \
  --set service.type=NodePort
kubectl -n cattle-system get deploy rancher
```

#### 访问

> 获取端口号

```bash
kubectl get svc -n cattle-system
# NAME              TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
# rancher           NodePort    10.43.104.172   <none>        80:32316/TCP,443:31908/TCP   47m
# rancher-webhook   ClusterIP   10.43.77.134    <none>        443/TCP                      45m
```

> 获取密码

```bash
kubectl get secret --namespace cattle-system bootstrap-secret -o go-template='{{.data.bootstrapPassword|base64decode}}{{ "\n" }}'
```

> 访问地址: https://47.92.128.32:31908   
> 密码: pt2g584d6hrrc9cw7k9vd8bbrrwz5rpmd75tdrg8cwsh6kjk8cg4pv

#### 相关调试命令

```bash
kubectl logs -l app=rancher -n cattle-system
kubectl get pods -o wide -n cert-manager
kubectl get pods -o wide -n cattle-system
kubectl describe pod rancher-6dd9f75c9d-kxmts -n cattle-system
kubectl get events -n cattle-system
kubectl logs helm-operation-54s9f  -n cattle-system
kubectl rollout status deployment -n cattle-system rancher
kubectl edit svc rancher -n cattle-system
kubectl delete ns cert-manager
kubectl get pods --all-namespaces -o jsonpath='{.items[*].spec.containers[*].image}' | tr -s '[[:space:]]' '\n' | sort | uniq
tail -f /data/rancher/rke2/agent/logs/kubelet.log
```

#### 修改rancher端口

```bash
kubectl edit svc rancher -n cattle-system
```

#### 相关文档

- [rancher docs](https://rancher.com/docs/) 
- [rke2 安装](https://docs.rke2.io/zh/install/airgap) 
- [helm install](https://helm.sh/docs/intro/install/) 
- [rancher 中文文档](https://docs.rancher.cn/) 
- [helm离线安装rancher](https://ranchermanager.docs.rancher.com/zh/getting-started/installation-and-upgrade/other-installation-methods/air-gapped-helm-cli-install) 
- [docker 下载](https://download.docker.com/linux/static/stable/x86_64/) 
- [harbor 下载](https://github.com/goharbor/harbor/releases) 
- [rke私有仓库配置](https://docs.rke2.io/zh/install/containerd_registry_configuration) 
- [国内rancher配置](https://docs.rancher.cn/docs/rancher2/best-practices/use-in-china/_index/)