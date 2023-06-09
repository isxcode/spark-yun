#!/bin/bash

######################
# 安装脚本
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

# 将文件解压到指定目录
tar -vxf /tmp/spark-yun-agent.tar.gz -C ${home_path}/ > /dev/null

# 运行jar包
nohup java -jar -Xmx2048m ${home_path}/lib/spark-yun-agent.jar --server.port=${agent_port} >>${home_path}/log/spark-yun-agent.log 2>&1 &
echo $! >${home_path}/spark-yun-agent.pid

# 检查是否安装
if [ -e "${home_path}/spark-yun-agent.pid" ]; then
  pid=$(cat "${home_path}/spark-yun-agent.pid")
  sleep 10
  if ps -p $pid >/dev/null 2>&1; then
    json_output="{ \
              \"status\": \"RUNNING\"
            }"
    echo $json_output
  else
    json_output="{ \
                  \"status\": \"STOP\"
                }"
    echo $json_output
  fi
fi

# 删除安装包 和 安装脚本
rm /tmp/spark-yun-agent.tar.gz && rm /tmp/sy-install.sh
