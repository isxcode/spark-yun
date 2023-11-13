#!/bin/bash

# 获取当前路径
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit
cd ".." || exit

# 项目已启动不执行
if [ -e "zhiqingyun.pid" ]; then
  pid=$(cat "zhiqingyun.pid")
  if ps -p $pid >/dev/null 2>&1; then
    echo "【至轻云】: RUNNING"
    exit 0
  fi
fi

# 启动项目
nohup java -jar -Xmx2048m lib/zhiqingyun.jar --spring.profiles.active=local --spring.config.additional-location=conf/ >> logs/zhiqingyun.log 2>&1 &
echo $! >zhiqingyun.pid
echo "【至轻云】: RUNNING"
tail -f logs/zhiqingyun.log