#!/bin/bash

######################
# 启动脚本
######################

BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit
cd ".." || exit

if [ -e "zhiqingyun-agent.pid" ]; then
  pid=$(cat "zhiqingyun-agent.pid")
  if ps -p $pid >/dev/null 2>&1; then
    echo "【至轻云代理】: HAS RUNNING"
    exit 0
  fi
fi

# 判断zhiliuyun-agent.log是否存在,不存在则新建
if [ ! -f logs/zhiliuyun-agent.log ]; then
  mkdir logs
  touch logs/zhiliuyun-agent.log
fi

# 运行jar包
nohup java -jar -Xmx2048m lib/zhiqingyun-agent.jar --spring.config.additional-location=conf/ > /dev/null 2>&1 &
echo $! >zhiqingyun-agent.pid
echo "【至轻云代理】: RUNNING"

tail -f logs/zhiqingyun-agent.log