#!/bin/bash

######################
# 检测安装环境脚本
######################

source /etc/profile

home_path=""
agent_port=""
for arg in "$@"; do
  case "$arg" in
  --home-path=*) home_path="${arg#*=}" ;;
  --agent-port=*) agent_port="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 判断home_path目录是否存在
if [ ! -d "$home_path" ]; then
  mkdir -p $home_path
fi

# 判断是否之前已安装代理
if [ -e "${home_path}/spark-yun-agent.pid" ]; then
  pid=$(cat "${home_path}/spark-yun-agent.pid")
  if ps -p $pid >/dev/null 2>&1; then
    json_output="{ \
            \"status\": \"RUNNING\", \
            \"log\": \"正在运行中\" \
          }"
    echo $json_output
    rm /tmp/sy-env.sh
    exit 0
  else
    json_output="{ \
            \"status\": \"STOP\", \
            \"log\": \"已安装，请启动\" \
          }"
    echo $json_output
    rm /tmp/sy-env.sh
    exit 0
  fi
fi

# 判断tar解压命令
if ! command -v tar &>/dev/null; then
  json_output="{ \
        \"status\": \"INSTALL_ERROR\", \
        \"log\": \"未检测到tar命令\" \
      }"
  echo $json_output
  rm /tmp/sy-env.sh
  exit 0
fi

# 判断mpstat命令
if ! command -v mpstat &>/dev/null; then
  json_output="{ \
        \"status\": \"INSTALL_ERROR\", \
        \"log\": \"未检测到mpstat命令\" \
      }"
  echo $json_output
  rm /tmp/sy-env.sh
  exit 0
fi

# 判断是否有java命令
if ! command -v java &>/dev/null; then
  json_output="{ \
    \"status\": \"INSTALL_ERROR\", \
    \"log\": \"未检测到java1.8.x环境\" \
  }"
  echo $json_output
  rm /tmp/sy-env.sh
  exit 0
fi

# 判断java版本是否为1.8
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ "$java_version" != "1.8"* ]]; then
  json_output="{ \
      \"status\": \"INSTALL_ERROR\", \
      \"log\": \"未检测到java1.8.x环境\" \
    }"
  echo $json_output
  rm /tmp/sy-env.sh
  exit 0
fi

# 判断是否有kubectl命令
if ! command -v kubectl &>/dev/null; then
  json_output="{ \
    \"status\": \"INSTALL_ERROR\", \
    \"log\": \"未检测到kubectl命令\" \
  }"
  echo $json_output
  rm /tmp/sy-env.sh
  exit 0
fi

# 判断kubectl命令，是否可以访问k8s集群
if ! kubectl cluster-info &>/dev/null; then
  json_output="{ \
          \"status\": \"INSTALL_ERROR\", \
          \"log\": \"kubectl无法访问k8s集群\" \
        }"
  echo $json_output
  rm /tmp/sy-env.sh
  exit 0
fi

# 判断端口号是否被占用
if ! netstat -tln | awk '$4 ~ /:'"$agent_port"'$/ {exit 1}'; then
  json_output="{ \
          \"status\": \"INSTALL_ERROR\", \
          \"log\": \"${agent_port} 端口号已被占用\" \
        }"
  echo $json_output
  rm /tmp/sy-env.sh
  exit 0
fi

# 返回可以安装
json_output="{ \
          \"status\": \"CAN_INSTALL\", \
          \"hadoopHome\": \"$HADOOP_PATH\", \
          \"log\": \"允许安装\" \
        }"
echo $json_output

# 删除检测脚本
rm /tmp/sy-env.sh
