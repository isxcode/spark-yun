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

# 获取HADOOP_HOME环境变量值
if [ -n "$HADOOP_HOME" ]; then
  HADOOP_PATH=$HADOOP_HOME
else
  echo "【结果】：未配置HADOOP_HOME环境变量"
  exit 0
fi

# 判断yarn命令
if ! command -v yarn &>/dev/null; then
  echo "【结果】：未检测到yarn命令"
  exit 0
fi

# 判断yarn是否正常运行
if ! timeout 10s yarn node -list &>/dev/null; then
  echo "【结果】：未启动yarn服务"
  exit 0
fi

echo "【结果】：允许启动"