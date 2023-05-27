#!/bin/bash

######################
# 检测脚本
######################

home_path=""
for arg in "$@"; do
  case "$arg" in
    --home-path=*) home_path="${arg#*=}" ;;
    *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 判断home_path目录是否存在
if [ ! -d "$home_path" ]; then
  mkdir -p $home_path
fi

# 检查是否有README.md文件，有则表示已安装，无则表示未安装
if [ -e "${home_path}/README.md" ]; then
  # 检查pid文件，无则表示未运行，有再判断pid是否运行
  if [ -e "${home_path}/spark-yun-agent.pid" ]; then
    pid=$(cat "${home_path}/spark-yun-agent.pid")
    if ps -p $pid > /dev/null 2>&1; then
        CHECK_STATUS="RUNNING"
    else
        CHECK_STATUS="STOP"
    fi
  fi
else
  CHECK_STATUS="UN_INSTALL"
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
CPU_PERCENT=$(mpstat 1 1 | awk '/Average:/ {printf "%.2f", 100 - $NF}')

# 返回json的日志
json_output="{ \
  \"status\": \"$CHECK_STATUS\", \
  \"log\": \"检测完成\", \
  \"allMemory\": $ALL_MEMORY, \
  \"usedMemory\": $USED_MEMORY, \
  \"allStorage\": $ALL_STORAGE, \
  \"usedStorage\": $USED_STORAGE, \
  \"cpuPercent\": $CPU_PERCENT \
}"

echo $json_output

# 删除检测脚本
rm /tmp/sy-check.sh

