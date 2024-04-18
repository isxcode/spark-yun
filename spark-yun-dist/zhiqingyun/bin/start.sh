#!/bin/bash

# 获取当前路径
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit
cd ".." || exit

# 项目已启动不执行
if [ -e "zhiqingyun.pid" ]; then
  pid=$(cat "zhiqingyun.pid")
  if ps -p $pid >/dev/null 2>&1; then
    echo "【至轻云】: HAS RUNNING"
    exit 0
  fi
fi

# 帮用户生成agent-env.sh
if [ ! -f "conf/zhiqingyun-env.sh" ]; then
  touch "conf/zhiqingyun-env.sh"
  echo '#export JAVA_HOME=' >> "conf/zhiqingyun-env.sh"
fi
source "conf/zhiqingyun-env.sh"

# 执行zhiqingyun-env.sh
source conf/zhiqingyun-env.sh

# 判断spark-yun.log是否存在,不存在则新建
if [ ! -f logs/spark-yun.log ]; then
  mkdir logs
  touch logs/spark-yun.log
fi

if ! command -v java &>/dev/null; then
  nohup $JAVA_HOME/bin/java -jar -Xmx2048m lib/zhiqingyun.jar --spring.profiles.active=local --spring.config.additional-location=conf/ > /dev/null 2>&1 &
else
  nohup java -jar -Xmx2048m lib/zhiqingyun.jar --spring.profiles.active=local --spring.config.additional-location=conf/ > /dev/null 2>&1 &
fi

echo $! >zhiqingyun.pid
echo "【至轻云】: RUNNING"
tail -f logs/spark-yun.log