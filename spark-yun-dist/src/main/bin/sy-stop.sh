#!/bin/bash

######################
# 停止脚本
######################

home_path=""
for arg in "$@"; do
  case "$arg" in
  --home-path=*) home_path="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

if [ -e "${home_path}/spark-yun-agent.pid" ]; then
  pid=$(cat "${home_path}/spark-yun-agent.pid")
  if ps -p $pid >/dev/null 2>&1; then
   kill -9 ${pid}
  fi
fi

# 返回结果
json_output="{ \
          \"status\": \"STOP\"
          \"log\": \"停止成功\",
        }"
echo $json_output

# 删除检测脚本
rm /tmp/sy-stop.sh
