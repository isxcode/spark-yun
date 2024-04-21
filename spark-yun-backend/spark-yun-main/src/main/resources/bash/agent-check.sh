#!/bin/bash

######################
# 检测代理器
######################

# 获取脚本文件当前路径
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)

# 获取外部参数
home_path=""
for arg in "$@"; do
  case "$arg" in
    --home-path=*) home_path="${arg#*=}" ;;
    *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 初始化agent_path
agent_path="${home_path}/zhiqingyun-agent"

# 创建zhiqingyun-agent目录
if [ ! -d "${agent_path}" ]; then
  mkdir -p "${agent_path}"
fi

# 检查代理当前状态
if [ -e "${agent_path}/README.md" ]; then
  if [ -e "${agent_path}/zhiqingyun-agent.pid" ]; then
    pid=$(cat "${agent_path}/zhiqingyun-agent.pid")
    if ps -p $pid > /dev/null 2>&1; then
        # 已启动
        CHECK_STATUS="RUNNING"
    else
        # 未启动
        CHECK_STATUS="STOP"
    fi
  fi
else
  # 未安装
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
  \"allMemory\": \"$ALL_MEMORY\", \
  \"usedMemory\": \"$USED_MEMORY\", \
  \"allStorage\": \"$ALL_STORAGE\", \
  \"usedStorage\": \"$USED_STORAGE\", \
  \"cpuPercent\": \"$CPU_PERCENT\" \
}"

echo $json_output

# 删除当前脚本文件
rm ${BASE_PATH}/agent-check.sh