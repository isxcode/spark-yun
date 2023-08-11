#!/bin/bash

# 获取当前路径
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit
cd ".." || exit

# 项目启动后，关闭进程
if [ -e "zhiqingyun.pid" ]; then
  pid=$(cat "zhiqingyun.pid")
  if ps -p $pid >/dev/null 2>&1; then
   kill -15 ${pid}
   echo "【至轻云】: CLOSED"
   exit 0
  fi
fi

echo "【至轻云】: CLOSED"
exit 0