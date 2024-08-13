#!/bin/bash

######################
# 获取节点信息
######################

BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)

# 获取已用内存
USED_MEMORY=$(free | grep Mem: | awk '{printf "%.0f", $3 *1024}')

# 获取已用存储
USED_STORAGE=$(df -B 1 -T | egrep 'ext4|xfs|btrfs' | awk '{total += $4} END {print total}')

# 获取cpu使用率
CPU_PERCENT=$(mpstat 1 1 | awk 'END {printf "%.2f", 100 - $NF}')

# 获取网络IO读写速度（单位：字节/秒）
NET_IO_READ=$(cat /proc/net/dev | grep eth0 | awk '{print $2}')
NET_IO_WRITE=$(cat /proc/net/dev | grep eth0 | awk '{print $10}')

# 获取磁盘IO读写速度（单位：字节/秒）
DISK_IO_READ=$(iostat -d -k | awk '/Device/{flag=1;next}/^$/{flag=0}flag{printf "%.0f\n", $3}')
DISK_IO_WRITE=$(iostat -d -k | awk '/Device/{flag=1;next}/^$/{flag=0}flag{printf "%.0f\n", $4}')

# 返回json的日志
json_output="{ \
  \"status\": \"SUCCESS\", \
  \"log\": \"检测完成\", \
  \"usedMemorySize\": \"$USED_MEMORY\", \
  \"usedStorageSize\": \"$USED_STORAGE\", \
  \"cpuPercent\": \"$CPU_PERCENT\", \
  \"networkIoReadSpeedStr\": \"$NET_IO_READ\", \
  \"networkIoWriteSpeedStr\": \"$NET_IO_WRITE\", \
  \"diskIoReadSpeedStr\": \"$DISK_IO_READ\", \
  \"diskIoWriteSpeedStr\": \"$DISK_IO_WRITE\" \
}"

echo $json_output