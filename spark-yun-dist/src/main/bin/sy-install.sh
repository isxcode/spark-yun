#!/bin/bash
# 安装脚本

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

mkdir ${home_path}/spark-yun-agent

tar -vxf ${home_path}/spark-yun-agent.tar.gz -C ${home_path}/spark-yun-agent/

if [ -e "${home_path}/spark-yun-agent/spark-yun-agent.pid" ]; then
  pid=$(cat "${home_path}/spark-yun-agent/spark-yun-agent.pid")
  if [ -n "$pid" ]; then
    kill -9 "$pid"
  fi
fi

export HADOOP_HOME=${hadoop_home_path}
export HADOOP_CONF_DIR=${hadoop_home_path}/etc/hadoop
nohup java -jar -Xmx2048m ${home_path}/spark-yun-agent/lib/spark-yun-agent.jar --server.port=${agent_port} >> ${home_path}/spark-yun-agent/log/spark-yun-agent.log 2>&1 &
echo $! > ${home_path}/spark-yun-agent/spark-yun-agent.pid

rm ${home_path}/spark-yun-agent.tar.gz && rm ${home_path}/spark-yun-install
echo "安装完成"
