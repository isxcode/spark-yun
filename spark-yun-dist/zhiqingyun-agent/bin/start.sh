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

# 判断zhiqingyun-agent.log是否存在,不存在则新建
if [ ! -f logs/zhiqingyun-agent.log ]; then
  mkdir logs
  touch logs/zhiqingyun-agent.log
fi

# 执行agent-env.sh
source conf/agent-env.sh

# 运行jar包
if ! command -v java &>/dev/null; then
  nohup $JAVA_HOME/bin/java -jar -Xmx2048m lib/zhiqingyun-agent.jar --spring.config.additional-location=conf/ > /dev/null 2>&1 &
else
  nohup java -jar -Xmx2048m lib/zhiqingyun-agent.jar --spring.config.additional-location=conf/ > /dev/null 2>&1 &
fi

echo $! >zhiqingyun-agent.pid
echo "【至轻云代理】: RUNNING"

tail -f logs/zhiqingyun-agent.log