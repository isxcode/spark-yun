#!/bin/bash

######################
# 停止脚本
######################

BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit
cd ".." || exit

if [ -e "zhiqingyun-agent.pid" ]; then
  pid=$(cat "zhiqingyun-agent.pid")
  if ps -p $pid >/dev/null 2>&1; then
   kill -15 ${pid}
   echo "【至轻云代理】: CLOSED"
   exit 0
  fi
fi

echo "【至轻云代理】: CLOSED"
exit 0