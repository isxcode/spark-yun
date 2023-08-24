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

# 判断是否安装spark，判断是否配置SPARK_HOME
if [ -n "$SPARK_HOME" ]; then
  SPARK_PATH=$SPARK_HOME
else
  echo "【结果】：未配置SPARK_HOME环境变量"
  exit 0
fi

# 从$SPARK_HOME/conf/spark-defaults.conf 配置文件中，获取SPARK_MASTER_PORT和SPARK_MASTER_WEBUI_PORT
# spark.master          spark://isxcode:7077
# spark.master.web.url  http://isxcode:8081
config_file="$SPARK_PATH/conf/spark-defaults.conf"

if [ ! -f "$config_file" ]; then
  echo "【结果】：$SPARK_HOME/conf/spark-defaults.conf配置文件不存在"
  exit 0
fi

spark_master_web_url=$(grep -E "^spark.master\.web\.url[[:space:]]+" "$config_file" | awk '{print $2}')
spark_master=$(grep -E "^spark.master[[:space:]]+" "$config_file" | awk '{print $2}')

if [[ -z $spark_master_web_url ]]; then
  echo "【结果】：$SPARK_HOME/conf/spark-defaults.conf配置文件中未配置spark.master.web.url，例如：spark.master.web.url  http://localhost:8081"
  exit 0
fi

if [[ -z $spark_master ]]; then
 echo "【结果】：$SPARK_HOME/conf/spark-defaults.conf配置文件中未配置spark.master，例如：spark.master          spark://localhost:7077"
 exit 0
fi

# 判断是否可以访问SPARK_MASTER_PORT=7077
regex="^spark://([^:]+):([0-9]+)$"
if [[ $spark_master =~ $regex ]]; then
  host=${BASH_REMATCH[1]}
  port=${BASH_REMATCH[2]}
  if ! (echo >/dev/tcp/$host/$port) &>/dev/null; then
     echo "【结果】：无法访问spark.master.web.url的服务，请检查spark服务。"
     exit 0
  fi
else
  echo "【结果】：spark master url 填写格式异常"
  exit 0
fi

# 判断是否可以访问SPARK_MASTER_WEBUI_PORT=8081
regex="^(http|https)://([^:/]+):([0-9]+)"
if [[ $spark_master_web_url =~ $regex ]]; then
  protocol=${BASH_REMATCH[1]}
  host=${BASH_REMATCH[2]}
  port=${BASH_REMATCH[3]}
  if ! (echo >/dev/tcp/$host/$port) &>/dev/null; then
     echo "【结果】：无法访问 spark web ui，请检查spark服务。"
     exit 0
  fi
else
  echo "【结果】：spark web ui url 填写格式异常"
  exit 0
fi

echo "【结果】：允许启动"
