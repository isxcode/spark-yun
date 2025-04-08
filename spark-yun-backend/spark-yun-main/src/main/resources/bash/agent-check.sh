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
ALL_MEMORY=$(free | grep Mem: | awk '{printf "%.1f", $2/1024/1024}')

# 获取已用内存
USED_MEMORY=$(free | grep Mem: | awk '{printf "%.1f", $3/1024/1024}')

# 获取所有存储
ALL_STORAGE=$(lsblk -b | grep disk | awk '{total += $4} END {printf "%.1f", total/1024/1024/1024}')

# 获取已用存储
USED_STORAGE=$(df -B 1 -T | egrep 'ext4|xfs|btrfs' | awk '{total += $4} END {printf "%.1f",total/1024/1024/1024}')

# 获取cpu使用率
CPU_PERCENT=$(top -bn 1 | grep "Cpu(s)" | awk -F',' '{print 100 - $4}' | awk '{print $1}')

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