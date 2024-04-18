#!/bin/bash

######################
# 检测安装环境脚本
######################

if [[ "$OSTYPE" == "linux-gnu" ]]; then
    source /etc/profile
    source ~/.bashrc
elif [[ "$OSTYPE" == "darwin"* ]]; then
    source /etc/profile
    source ~/.zshrc
else
    json_output="{ \
                      \"status\": \"INSTALL_ERROR\", \
                      \"log\": \"该系统不支持安装\" \
                    }"
      echo $json_output
      rm /tmp/agent-yarn.sh
      exit 0
fi

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

cd "${home_path}" || exit

# 判断home_path目录是否存在
if [ ! -d "$home_path" ]; then
  mkdir -p $home_path
fi

# 帮用户生成agent-env.sh
if [ ! -f "$home_path/conf/agent-env.sh" ]; then
  mkdir "$home_path/conf"
  touch "$home_path/conf/agent-env.sh"
  echo '#export JAVA_HOME=' >> "$home_path/conf/agent-env.sh"
  echo '#export HADOOP_HOME=' >> "$home_path/conf/agent-env.sh"
  echo '#export HADOOP_CONF_DIR=' >> "$home_path/conf/agent-env.sh"
fi
source "$home_path/conf/agent-env.sh"

# 判断是否之前已安装代理
if [ -e "${home_path}/zhiqingyun-agent.pid" ]; then
  pid=$(cat "${home_path}/zhiqingyun-agent.pid")
  if ps -p $pid >/dev/null 2>&1; then
    json_output="{ \
            \"status\": \"RUNNING\", \
            \"log\": \"正在运行中\" \
          }"
    echo $json_output
    rm /tmp/agent-yarn.sh
    exit 0
  else
    json_output="{ \
            \"status\": \"STOP\", \
            \"log\": \"已安装，请启动\" \
          }"
    echo $json_output
    rm /tmp/agent-yarn.sh
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
  rm /tmp/agent-yarn.sh
  exit 0
fi

# 判断是否有java命令
if ! command -v java &>/dev/null; then
  if [ ! -n "$JAVA_HOME" ]; then
    json_output="{ \
    \"status\": \"INSTALL_ERROR\", \
    \"log\": \"未检测到java1.8.x环境,节点请安装java或者配置 $home_path/conf/agent-env.sh文件\" \
    }"
    echo $json_output
    rm /tmp/agent-yarn.sh
    exit 0
  fi
else
  java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
  if [[ "$java_version" != "1.8"* ]]; then
    json_output="{ \
        \"status\": \"INSTALL_ERROR\", \
        \"log\": \"未检测到java1.8.x环境\" \
      }"
    echo $json_output
    rm /tmp/agent-yarn.sh
    exit 0
  fi
fi

# 获取HADOOP_HOME环境变量值
if [ -n "$HADOOP_HOME" ]; then
  HADOOP_PATH=$HADOOP_HOME
else
  json_output="{ \
            \"status\": \"INSTALL_ERROR\", \
            \"log\": \"未配置HADOOP_HOME环境变量,节点请配置 $home_path/conf/agent-env.sh文件\" \
          }"
  echo $json_output
  rm /tmp/agent-yarn.sh
  exit 0
fi
if [ -n "$HADOOP_CONF_DIR" ]; then

else
  json_output="{ \
            \"status\": \"INSTALL_ERROR\", \
            \"log\": \"未配置HADOOP_CONF_DIR环境变量,节点请配置 $home_path/conf/agent-env.sh文件\" \
          }"
  echo $json_output
  rm /tmp/agent-yarn.sh
  exit 0
fi

# 判断yarn命令
if ! command -v yarn &>/dev/null; then
  json_output="{ \
      \"status\": \"INSTALL_ERROR\", \
      \"log\": \"未检测到yarn命令\" \
    }"
  echo $json_output
  rm /tmp/agent-yarn.sh
  exit 0
fi

if [[ "$OSTYPE" == "linux-gnu" ]]; then
    # 判断yarn是否正常运行
    if ! timeout 30s yarn node -list &>/dev/null; then
      json_output="{ \
            \"status\": \"INSTALL_ERROR\", \
            \"log\": \"未启动yarn服务\" \
          }"
      echo $json_output
      rm /tmp/agent-yarn.sh
      exit 0
    fi
elif [[ "$OSTYPE" == "darwin"* ]]; then
    # 判断yarn是否正常运行
    if ! yarn node -list &>/dev/null; then
      json_output="{ \
            \"status\": \"INSTALL_ERROR\", \
            \"log\": \"未启动yarn服务\" \
          }"
      echo $json_output
      rm /tmp/agent-yarn.sh
      exit 0
    fi
else
    json_output="{ \
                      \"status\": \"INSTALL_ERROR\", \
                      \"log\": \"该系统不支持安装\" \
                    }"
      echo $json_output
      rm /tmp/agent-yarn.sh
      exit 0
fi

# 判断端口号是否被占用
if ! netstat -tln | awk '$4 ~ /:'"$agent_port"'$/ {exit 1}'; then
  json_output="{ \
          \"status\": \"INSTALL_ERROR\", \
          \"log\": \"${agent_port} 端口号已被占用\" \
        }"
  echo $json_output
  rm /tmp/agent-yarn.sh
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
rm /tmp/agent-yarn.sh