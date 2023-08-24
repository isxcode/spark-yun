#!/bin/bash

######################
# 检测安装环境脚本
######################

# 判断tar解压命令
if ! command -v tar &>/dev/null; then
  echo "【结果】：未检测到tar命令"
  exit 0
fi

# 判断是否有java命令
if ! command -v java &>/dev/null; then
  echo "【结果】：未检测到java1.8.x环境"
  exit 0
fi

# 判断java版本是否为1.8
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ "$java_version" != "1.8"* ]]; then
  echo "【结果】：未检测到java1.8.x环境"
  exit 0
fi

# 判断是否有kubectl命令
if ! command -v kubectl &>/dev/null; then
  echo "【结果】：未检测到kubectl命令"
  exit 0
fi

# 判断kubectl命令，是否可以访问k8s集群
if ! kubectl cluster-info &>/dev/null; then
  echo "【结果】：kubectl无法访问k8s集群"
  exit 0
fi


# 执行拉取spark镜像命令
if ! docker image inspect apache/spark:v3.1.3 &>/dev/null; then
  echo "【结果】：没有apache/spark:v3.1.3镜像，需要执行拉取镜像命令，docker pull apache/spark:v3.1.3"
  exit 0
fi

# 检测命名空间是否有spark-yun
if ! kubectl get namespace zhiqingyun-space &>/dev/null; then
  echo "【结果】：没有zhiqingyun命令空间，需要执行命令，kubectl create namespace zhiqingyun-space"
  exit 0
fi

# 判断是否存在zhiqingyun用户
if ! kubectl get serviceaccount --namespace zhiqingyun-space | grep -q zhiqingyun; then
  echo "【结果】：zhiqingyun命令空间中，不存在zhiqingyun用户，需要执行命令，kubectl create serviceaccount zhiqingyun -n zhiqingyun-space "
  exit 0
fi

# 判断是否zhiqingyun有读写权限
hasRole=$(kubectl auth can-i create pods --as=system:serviceaccount:zhiqingyun-space:zhiqingyun 2>&1)
if [ "$hasRole" = "no" ]; then
  echo "【结果】：zhiqingyun没有创建pod的权限，需要执行命令，kubectl create clusterrolebinding spark-role --clusterrole=edit --serviceaccount=zhiqingyun-space:zhiqingyun --namespace=zhiqingyun-space "
  exit 0
fi

echo "【结果】：允许启动"