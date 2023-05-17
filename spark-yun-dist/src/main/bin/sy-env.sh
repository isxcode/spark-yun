#!/bin/bash

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
  ENV_STATUS="CAN_NOT_INSTALL"
  LOG=$home_path+"目录不存在"
fi

# 判断是否之前已安装代理
if [ -e "${home_path}/spark-yun-agent.pid" ]; then
  pid=$(cat "${home_path}/spark-yun-agent.pid")
  if ps -p $pid > /dev/null 2>&1; then
      ENV_STATUS="RUNNING"
  else
      ENV_STATUS="INSTALLED"
  fi
else
  ENV_STATUS="INSTALLED"
fi

# 判断tar解压命令


# 判断java版本

# 判断hadoop环境变量

# 判断yarn是否正常运行

# 判断端口号是否占用

#

# 检查是否有README.md文件，有则表示已安装，无则表示未安装
if [ -e "${home_path}/README.md" ]; then
  INSTALL_STATUS="INSTALLED"
else
  INSTALL_STATUS="NO_INSTALL"
fi

# 检查pid文件，无则表示未运行，有再判断pid是否运行
if [ -e "${home_path}/spark-yun-agent.pid" ]; then
  pid=$(cat "${home_path}/spark-yun-agent.pid")
  if ps -p $pid > /dev/null 2>&1; then
      RUN_STATUS="RUNNING"
  else
      RUN_STATUS="STOP"
  fi
else
  RUN_STATUS="STOP"
fi

# 获取所有内存
ALL_MEMORY=$(grep MemTotal /proc/meminfo | awk '{print $2/1024/1024}' | bc -l | awk '{printf "%.2f\n", $1}')

# 获取已用内存
USED_MEMORY=$(free | grep Mem: | awk '{printf "%.2f", $3/1024/1024}')

# 获取所有存储
ALL_STORAGE=$(lsblk -b | grep disk | awk '{total += $4} END {print total/1024/1024/1024}')

# 获取已用存储
USED_STORAGE=$(df -h -t ext4 | awk '{total += $3} END {print total}')

# 获取cpu使用率
CPU_PERCENT=$(top -bn1 | grep '%Cpu' | awk '{print 100-$8}')

# 返回json的日志
json_output="{ \
  \"envStatus\": \"$ENV_STATUS\", \
  \"log\": \"$LOG\" \
}"

echo $json_output

# 删除检测脚本
rm -rf ${home_path}/spark-yun-check
