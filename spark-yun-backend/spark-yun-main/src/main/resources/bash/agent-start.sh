#!/bin/bash

######################
# 启动脚本
######################

source /etc/profile

home_path=""
agent_port=""
for arg in "$@"; do
  case "$arg" in
  --home-path=*) home_path="${arg#*=}" ;;
  --agent-port=*) agent_port="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

if [ -e "${home_path}/spark-yun-agent.pid" ]; then
  pid=$(cat "${home_path}/spark-yun-agent.pid")
  if ps -p $pid >/dev/null 2>&1; then
    json_output="{ \
                \"status\": \"STOP\", \
                \"log\": \"请停止后，再启动代理\"
              }"
    exit 1
  fi
fi

# 运行jar包
nohup java -jar -Xmx2048m ${home_path}/lib/spark-yun-agent.jar --server.port=${agent_port} >>${home_path}/log/zhiqingyun-agent.log 2>&1 &
echo $! >${home_path}/spark-yun-agent.pid

# 返回结果
json_output="{ \
          \"status\": \"RUNNING\"
          \"log\": \"激活成功\",
        }"
echo $json_output

# 删除启动脚本
rm /tmp/sy-start.sh
