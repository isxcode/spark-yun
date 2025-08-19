#!/bin/bash

##########################################
# 安装前,检测yarn模式是否允许安装
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
    echo "{\"status\": \"INSTALL_ERROR\",\"log\": \"该系统不支持安装\"}"
    rm "${BASE_PATH}"/agent-yarn.sh
    exit 0
fi

# 获取外部参数
home_path=""
agent_port=""
spark_local="false"
for arg in "$@"; do
  case "$arg" in
  --home-path=*) home_path="${arg#*=}" ;;
  --agent-port=*) agent_port="${arg#*=}" ;;
  --spark-local=*) spark_local="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 初始化agent_path
agent_path="${home_path}/zhiqingyun-agent"

# 创建zhiqingyun-agent目录
if [ ! -d "${agent_path}" ]; then
  mkdir -p "${agent_path}"
fi

# 帮助用户初始化agent-env.sh文件
if [ ! -f "${agent_path}/conf/agent-env.sh" ]; then
  mkdir "${agent_path}/conf"
  touch "${agent_path}/conf/agent-env.sh"
  echo '#export JAVA_HOME=' >> "${agent_path}/conf/agent-env.sh"
  echo '#export HADOOP_HOME=' >> "${agent_path}/conf/agent-env.sh"
  echo '#export HADOOP_CONF_DIR=' >> "${agent_path}/conf/agent-env.sh"
fi

# 导入用户自己配置的环境变量
source "${agent_path}/conf/agent-env.sh"

# 判断当前是否安装代理
if [ -e "${agent_path}/zhiqingyun-agent.pid" ]; then
  pid=$(cat "${agent_path}/zhiqingyun-agent.pid")
  if ps -p "$pid" >/dev/null 2>&1; then
    echo "{ \"status\": \"RUNNING\", \"log\": \"正在运行中\"}"
    rm "${BASE_PATH}"/agent-yarn.sh
    exit 0
  else
    echo "{ \"status\": \"STOP\", \"log\": \"已安装，请启动\"}"
    rm "${BASE_PATH}"/agent-yarn.sh
    exit 0
  fi
fi

# 判断是否有java命令
if ! command -v java &>/dev/null; then
  if [ ! -n "$JAVA_HOME" ]; then
    echo "{ \"status\": \"INSTALL_ERROR\", \"log\": \"未检测到java1.8.x环境,节点请安装java 推荐命令: sudo yum install java-1.8.0-openjdk-devel java-1.8.0-openjdk -y,或者配置 ${agent_path}/conf/agent-env.sh文件中的JAVA_HOME变量\"}"
    rm "${BASE_PATH}"/agent-yarn.sh
    exit 0
  fi
fi

# 判断是否配置HADOOP_HOME环境变量
if [ -z "$HADOOP_HOME" ]; then
  echo "{\"status\": \"INSTALL_ERROR\", \"log\": \"未配置HADOOP_HOME环境变量,节点请配置 ${agent_path}/conf/agent-env.sh文件中的HADOOP_HOME变量\"}"
  rm "${BASE_PATH}"/agent-yarn.sh
  exit 0
fi

# 判断是否配置HADOOP_CONF_DIR环境变量
if [ -z "$HADOOP_CONF_DIR" ]; then
  echo "{ \"status\": \"INSTALL_ERROR\",\"log\": \"未配置HADOOP_CONF_DIR环境变量,节点请配置 ${agent_path}/conf/agent-env.sh文件中的HADOOP_CONF_DIR变量\" }"
  rm "${BASE_PATH}"/agent-yarn.sh
  exit 0
fi

# 判断本地是否有yarn命令
if ! command -v yarn &>/dev/null; then
  echo "{\"status\": \"INSTALL_ERROR\", \"log\": \"未检测到yarn命令\"}"
  rm "${BASE_PATH}"/agent-yarn.sh
  exit 0
fi

# 判断本地是否启动yarn服务
if ! timeout 60s yarn node -list &>/dev/null; then
  echo "{ \"status\": \"INSTALL_ERROR\", \"log\": \"未启动yarn服务\" }"
  rm "${BASE_PATH}"/agent-yarn.sh
  exit 0
fi

# 返回可以安装
echo "{ \"status\": \"CAN_INSTALL\", \"hadoopHome\": \"$HADOOP_HOME\", \"log\": \"允许安装\" }"

# 删除本地检测脚本
rm "${BASE_PATH}"/agent-yarn.sh