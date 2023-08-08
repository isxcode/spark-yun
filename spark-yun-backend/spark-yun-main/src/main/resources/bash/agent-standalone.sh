#!/bin/bash

######################
# 检测安装环境脚本
######################

source /etc/profile

BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)

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
    rm ${BASE_PATH}/agent-standalone.sh
    exit 0
  else
    json_output="{ \
            \"status\": \"STOP\", \
            \"log\": \"已安装，请启动\" \
          }"
    echo $json_output
    rm ${BASE_PATH}/agent-standalone.sh
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
  rm ${BASE_PATH}/agent-standalone.sh
  exit 0
fi

# 判断是否有java命令
if ! command -v java &>/dev/null; then
  json_output="{ \
    \"status\": \"INSTALL_ERROR\", \
    \"log\": \"未检测到java命令，需要java1.8.x环境\" \
  }"
  echo $json_output
  rm ${BASE_PATH}/agent-standalone.sh
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
  rm ${BASE_PATH}/agent-standalone.sh
  exit 0
fi

# 判断端口号是否被占用
if ! netstat -tln | awk '$4 ~ /:'"$agent_port"'$/ {exit 1}'; then
  json_output="{ \
          \"status\": \"INSTALL_ERROR\", \
          \"log\": \"${agent_port} 端口号已被占用\" \
        }"
  echo $json_output
  rm ${BASE_PATH}/agent-standalone.sh
  exit 0
fi

# 判断是否安装spark，判断是否配置SPARK_HOME
if [ -n "$SPARK_HOME" ]; then
  SPARK_PATH=$SPARK_HOME
else
  json_output="{ \
            \"status\": \"INSTALL_ERROR\", \
            \"log\": \"未配置SPARK_HOME环境变量\" \
          }"
  echo $json_output
  rm ${BASE_PATH}/agent-standalone.sh
  exit 0
fi

# 从$SPARK_HOME/conf/spark-defaults.conf 配置文件中，获取SPARK_MASTER_PORT和SPARK_MASTER_WEBUI_PORT
# spark.master          spark://isxcode:7077
# spark.master.web.url  http://isxcode:8081
config_file="$SPARK_PATH/conf/spark-defaults.conf"

if [ ! -f "$config_file" ]; then
    json_output="{ \
                       \"status\": \"INSTALL_ERROR\", \
                       \"log\": \"$SPARK_HOME/conf/spark-defaults.conf配置文件不存在\" \
                     }"
      echo $json_output
      rm ${BASE_PATH}/agent-standalone.sh
      exit 0
fi

spark_master_web_url=$(grep -E "^spark.master\.web\.url[[:space:]]+" "$config_file" | awk '{print $2}')
spark_master=$(grep -E "^spark.master[[:space:]]+" "$config_file" | awk '{print $2}')

if [[ -z $spark_master_web_url ]]; then
  json_output="{ \
               \"status\": \"INSTALL_ERROR\", \
               \"log\": \"$SPARK_HOME/conf/spark-defaults.conf配置文件中未配置spark.master.web.url\" \
             }"
  echo $json_output
  rm ${BASE_PATH}/agent-standalone.sh
  exit 0
fi

if [[ -z $spark_master ]]; then
  json_output="{ \
                   \"status\": \"INSTALL_ERROR\", \
                   \"log\": \"$SPARK_HOME/conf/spark-defaults.conf配置文件中未配置spark.master\" \
                 }"
  echo $json_output
  rm ${BASE_PATH}/agent-standalone.sh
  exit 0
fi

# 判断是否可以访问SPARK_MASTER_PORT=7077
regex="^spark://([^:]+):([0-9]+)$"
if [[ $spark_master =~ $regex ]]; then
  host=${BASH_REMATCH[1]}
  port=${BASH_REMATCH[2]}
  if ! (echo >/dev/tcp/$host/$port) &>/dev/null; then
    json_output="{ \
                      \"status\": \"INSTALL_ERROR\", \
                      \"log\": \"无法访问spark.master.web.url的服务\" \
                    }"
    echo $json_output
    rm /tmp/sy-env-standalone.sh
    exit 0
  fi
else
  json_output="{ \
              \"status\": \"INSTALL_ERROR\", \
              \"log\": \"spark master url 填写格式异常\" \
            }"
  echo $json_output
  rm ${BASE_PATH}/agent-standalone.sh
  exit 0
fi

# 判断是否可以访问SPARK_MASTER_WEBUI_PORT=8081
regex="^(http|https)://([^:/]+):([0-9]+)"
if [[ $spark_master_web_url =~ $regex ]]; then
  protocol=${BASH_REMATCH[1]}
  host=${BASH_REMATCH[2]}
  port=${BASH_REMATCH[3]}
  if ! (echo >/dev/tcp/$host/$port) &>/dev/null; then
    json_output="{ \
                    \"status\": \"INSTALL_ERROR\", \
                    \"log\": \"无法访问 spark web ui\" \
                  }"
    echo $json_output
    rm ${BASE_PATH}/agent-standalone.sh
    exit 0
  fi
else
  json_output="{ \
                  \"status\": \"INSTALL_ERROR\", \
                  \"log\": \"spark web ui url 填写格式异常\" \
                }"
  echo $json_output
  rm ${BASE_PATH}/agent-standalone.sh
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
rm ${BASE_PATH}/agent-standalone.sh
