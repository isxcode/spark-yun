#!/bin/bash

# 获取当前路径
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit
cd ".." || exit

print_log="true"
for arg in "$@"; do
  case "$arg" in
    --print-log=*) print_log="${arg#*=}" ;;
    *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 导入用户指定环境变量
source "conf/zhiqingyun-env.sh"

# 项目已经在运行中
if [ -e "zhiqingyun.pid" ]; then
  pid=$(cat "zhiqingyun.pid")
  if ps -p $pid >/dev/null 2>&1; then
    echo "【至轻云】: HAS RUNNING"
    exit 0
  fi
fi

# 判断spark-yun.log是否存在,不存在则新建
if [ ! -f logs/spark-yun.log ]; then
  mkdir logs
  touch logs/spark-yun.log
fi

# 运行至轻云程序
if ! command -v java &>/dev/null; then
  nohup $JAVA_HOME/bin/java -jar -Xmx2048m lib/zhiqingyun.jar --spring.profiles.active=local --spring.config.additional-location=conf/ > /dev/null 2>&1 &
else
  nohup java -jar -Xmx2048m lib/zhiqingyun.jar --spring.profiles.active=local --spring.config.additional-location=conf/ > /dev/null 2>&1 &
fi
echo $! >zhiqingyun.pid

echo "【至轻云】: STARTING"
if [ "$print_log" == "true" ]; then
  tail -f logs/spark-yun.log
fi