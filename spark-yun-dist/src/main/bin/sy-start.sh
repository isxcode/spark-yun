#!/bin/bash

######################
# 启动脚本
######################

home_path=""
agent_port=""
hadoop_home_path=""
for arg in "$@"; do
  case "$arg" in
  --home-path=*) home_path="${arg#*=}" ;;
  --agent-port=*) agent_port="${arg#*=}" ;;
  --hadoop-home=*) hadoop_home_path="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

if [ -e "${home_path}/spark-yun-agent.pid" ]; then
  pid=$(cat "${home_path}/spark-yun-agent.pid")
  if ps -p $pid >/dev/null 2>&1; then
    json_output="{ \
                \"startStatus\": \"FAIL\", \
                \"log\": \"请停止后，再启动代理\"
              }"
    exit 1
  fi
fi

# 配置环境变量
export HADOOP_HOME=${hadoop_home_path}
export HADOOP_CONF_DIR=${hadoop_home_path}/etc/hadoop

# 运行jar包
nohup java -jar -Xmx2048m ${home_path}/lib/spark-yun-agent.jar --server.port=${agent_port} >>${home_path}/log/spark-yun-agent.log 2>&1 &
echo $! >${home_path}/spark-yun-agent/spark-yun-agent.pid

# 返回结果
json_output="{ \
          \"startStatus\": \"SUCCESS\"
        }"
echo $json_output
