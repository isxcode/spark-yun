#!/bin/bash

##########################################
# 安装前,检测k8s模式是否允许安装
##########################################

# 获取脚本文件当前路径
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)

# 初始化环境变量
if [[ "$OSTYPE" == "linux-gnu" ]]; then
    source /etc/profile
    source ~/.bashrc
elif [[ "$OSTYPE" == "darwin"* ]]; then
    source /etc/profile
    source ~/.zshrc
else
    echo "{\"status\": \"INSTALL_ERROR\", \"log\": \"该系统不支持安装\"}"
    rm "${BASE_PATH}"/agent-kubernetes.sh
    exit 0
fi

# 获取外部参数
home_path=""
agent_port=""
for arg in "$@"; do
  case "$arg" in
  --home-path=*) home_path="${arg#*=}" ;;
  --agent-port=*) agent_port="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 初始化agent_path
agent_path="${home_path}/zhiqingyun-agent"

# 创建zhiqingyun-agent目录
if [ ! -d "${agent_path}" ]; then
  mkdir -p "${agent_path}"
fi

# 判断tar解压命令
if ! command -v tar &>/dev/null; then
  echo "{\"status\": \"INSTALL_ERROR\", \"log\": \"未检测到tar命令\"}"
  rm "${BASE_PATH}"/agent-kubernetes.sh
  exit 0
fi

# 将文件解压到指定目录
tar -xf "${BASE_PATH}"/zhiqingyun-agent.tar.gz -C "${home_path}" > /dev/null

# 导入用户自己配置的环境变量
source "${agent_path}/conf/agent-env.sh"

# 判断是否之前已安装代理
if [ -e "${agent_path}/zhiqingyun-agent.pid" ]; then
  pid=$(cat "${agent_path}/zhiqingyun-agent.pid")
  if ps -p "$pid" >/dev/null 2>&1; then
    echo "{ \"status\": \"RUNNING\", \"log\": \"正在运行中\"}"
    rm "${BASE_PATH}"/agent-kubernetes.sh
    exit 0
  else
    echo "{ \"status\": \"STOP\",\"log\": \"已安装，请启动\"}"
    rm "${BASE_PATH}"/agent-kubernetes.sh
    exit 0
  fi
fi

# 判断是否有java命令
if ! command -v java &>/dev/null; then
  if [ ! -n "$JAVA_HOME" ]; then
    echo "{ \"status\": \"INSTALL_ERROR\",\"log\": \"未检测到java1.8.x环境,节点请安装java 推荐命令: sudo yum install java-1.8.0-openjdk-devel java-1.8.0-openjdk -y,或者配置 ${agent_path}/conf/agent-env.sh文件中的JAVA_HOME变量\" }"
    rm "${BASE_PATH}"/agent-kubernetes.sh
    exit 0
  fi
fi

# 判断是否有kubectl命令
if ! command -v kubectl &>/dev/null; then
  echo "{ \"status\": \"INSTALL_ERROR\", \"log\": \"未检测到kubectl命令\"}"
  rm "${BASE_PATH}"/agent-kubernetes.sh
  exit 0
fi

# 判断kubectl命令，是否可以访问k8s集群
if ! kubectl cluster-info &>/dev/null; then
  echo "{  \"status\": \"INSTALL_ERROR\", \"log\": \"kubectl无法访问k8s集群\" }"
  rm "${BASE_PATH}"/agent-kubernetes.sh
  exit 0
fi

# 执行拉取spark镜像命令
if ! docker image inspect spark:3.4.1 &>/dev/null; then
  echo "{  \"status\": \"INSTALL_ERROR\", \"log\": \"没有spark:3.4.1镜像，需要执行拉取镜像命令，docker pull spark:3.4.1 或者 docker pull registry.cn-shanghai.aliyuncs.com/isxcode/spark:3.4.1-amd64 && docker tag registry.cn-shanghai.aliyuncs.com/isxcode/spark:3.4.1-amd64 spark:3.4.1 \" }"
  rm "${BASE_PATH}"/agent-kubernetes.sh
  exit 0
fi

# 执行拉取flink镜像命令
if ! docker image inspect flink:1.18.1-scala_2.12 &>/dev/null; then
  echo "{   \"status\": \"INSTALL_ERROR\", \"log\": \"没有flink:1.18.1-scala_2.12镜像，需要执行拉取镜像命令，docker pull flink:1.18.1-scala_2.12 或者 docker pull registry.cn-shanghai.aliyuncs.com/isxcode/flink:1.18.1-scala-2.12-amd64 && docker tag registry.cn-shanghai.aliyuncs.com/isxcode/flink:1.18.1-scala-2.12-amd64 flink:1.18.1-scala_2.12\" }"
  rm "${BASE_PATH}"/agent-kubernetes.sh
  exit 0
fi

# 检测命名空间是否有zhiqingyun-space
if ! kubectl get namespace zhiqingyun-space &>/dev/null; then
  echo "{ \"status\": \"INSTALL_ERROR\", \"log\": \"没有zhiqingyun命令空间，需要执行命令，kubectl create namespace zhiqingyun-space \"}"
  rm "${BASE_PATH}"/agent-kubernetes.sh
  exit 0
fi

# 判断是否存在zhiqingyun用户
if ! kubectl get serviceaccount --namespace zhiqingyun-space | grep -q zhiqingyun; then
  echo "{\"status\": \"INSTALL_ERROR\",\"log\": \"zhiqingyun命令空间中，不存在zhiqingyun用户，需要执行命令，kubectl create serviceaccount zhiqingyun -n zhiqingyun-space \" }"
  rm "${BASE_PATH}"/agent-kubernetes.sh
  exit 0
fi

# 判断是否zhiqingyun有读写权限
hasRole=$(kubectl auth can-i create pods --as=system:serviceaccount:zhiqingyun-space:zhiqingyun 2>&1)
if [ "$hasRole" = "no" ]; then
  echo "{ \"status\": \"INSTALL_ERROR\", \"log\": \"zhiqingyun没有创建pod的权限，需要执行命令，kubectl create clusterrolebinding spark-role --clusterrole=edit --serviceaccount=zhiqingyun-space:zhiqingyun --namespace=zhiqingyun-space \" }"
  rm "${BASE_PATH}"/agent-kubernetes.sh
  exit 0
fi

# 判断端口号是否被占用
if ! netstat -tln | awk '$4 ~ /:'"$agent_port"'$/ {exit 1}'; then
  echo "{ \"status\": \"INSTALL_ERROR\", \"log\": \"${agent_port} 端口号已被占用\" }"
  rm "${BASE_PATH}"/agent-kubernetes.sh
  exit 0
fi

# 返回可以安装
echo "{   \"status\": \"CAN_INSTALL\", \"hadoopHome\": \"$HADOOP_PATH\", \"log\": \"允许安装\" }"

# 删除检测脚本
rm "${BASE_PATH}"/agent-kubernetes.sh