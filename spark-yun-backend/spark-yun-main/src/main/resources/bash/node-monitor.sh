#!/bin/bash

######################
# 获取节点信息
######################

BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)

# 获取已用内存
USED_MEMORY=$(free | grep Mem: | awk '{printf "%.1f", $3/1024/1024}')

# 获取已用存储
USED_STORAGE=$(df -B 1 -T | egrep 'ext4|xfs|btrfs' | awk '{total += $4} END {printf "%.1f",total/1024/1024/1024}')

# 获取cpu使用率
CPU_PERCENT=$(top -bn 1 | grep "Cpu(s)" | awk -F',' '{print 100 - $4}' | awk '{print $1}')

# 获取网络IO读写速度（单位：字节/秒）
NET_IO_READ=$(sar -n DEV 1 2 | tail -n 1 | awk '{print $5}')
NET_IO_WRITE=$(sar -n DEV 1 2 | tail -n 1 | awk '{print $6}')

# 获取磁盘IO读写速度（单位：字节/秒）
DISK_IO_READ=$(sar -d 1 2 | tail -n 1 | awk '{print $4}')
DISK_IO_WRITE=$(sar -d 1 2 | tail -n 1 | awk '{print $5}')

# 返回json的日志
json_output="{ \
  \"status\": \"SUCCESS\", \
  \"log\": \"检测完成\", \
  \"usedMemorySize\": \"$USED_MEMORY\", \
  \"usedStorageSize\": \"$USED_STORAGE\", \
  \"cpuPercent\": \"$CPU_PERCENT\", \
  \"networkIoReadSpeed\": \"$NET_IO_READ\", \
  \"networkIoWriteSpeed\": \"$NET_IO_WRITE\", \
  \"diskIoReadSpeed\": \"$DISK_IO_READ\", \
  \"diskIoWriteSpeed\": \"$DISK_IO_WRITE\" \
}"

echo $json_output