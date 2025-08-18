#!/bin/bash

######################
# 单独启动代理器
######################

# 获取脚本文件当前路径
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)

# 初始化环境变量
if [[ "$OSTYPE" == "linux-gnu" ]]; then
    source /etc/profile
    source ~/.bashrc
elif [[ "$OSTYPE" == "darwin"* ]]; then
    source /etc/profile
    source ~/.zshrc
else
    echo "{ \"status\": \"INSTALL_ERROR\",\"log\": \"该系统不支持安装\" }"
    rm "${BASE_PATH}"/agent-start.sh
    exit 0
fi

# 获取外部参数
home_path=""
agent_port=""
spark_local="false"
flink_local="false"
for arg in "$@"; do
  case "$arg" in
  --home-path=*) home_path="${arg#*=}" ;;
  --agent-port=*) agent_port="${arg#*=}" ;;
  --spark-local=*) spark_local="${arg#*=}" ;;
  --flink-local=*) flink_local="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 初始化agent_path
agent_path="${home_path}/zhiqingyun-agent"

# 导入用户自己配置的环境变量
source "${agent_path}"/conf/agent-env.sh

if [ -e "${agent_path}/zhiqingyun-agent.pid" ]; then
  pid=$(cat "${agent_path}/zhiqingyun-agent.pid")
  if ps -p "$pid" >/dev/null 2>&1; then
    echo "{ \"status\": \"STOP\", \"log\": \"请停止后，再启动代理\"}"
    rm "${BASE_PATH}"/agent-start.sh
    exit 0
  fi
fi

# 进入代理目录,防止logs文件夹生成错位
cd "${agent_path}" || exit

# 运行代理程序
if ! command -v java &>/dev/null; then
  nohup "$JAVA_HOME"/bin/java -jar -Xmx2048m "${agent_path}"/lib/zhiqingyun-agent.jar --server.port="${agent_port}" --spring.config.additional-location="${agent_path}"/conf/ > /dev/null 2>&1 &
else
  nohup java -jar -Xmx2048m "${agent_path}"/lib/zhiqingyun-agent.jar --server.port="${agent_port}" --spring.config.additional-location="${agent_path}"/conf/ > /dev/null 2>&1 &
fi
echo $! >"${agent_path}"/zhiqingyun-agent.pid

# 默认安装spark
if [ "${spark_local}" = "true" ]; then
  nohup bash "${agent_path}"/spark-min/sbin/start-all.sh > /dev/null 2>&1 &
fi

# 默认安装flink
if [ "${flink_local}" = "true" ]; then
  nohup bash "${agent_path}"/flink-min/bin/start-cluster.sh > /dev/null 2>&1 &
fi

# 返回结果
echo "{\"status\": \"RUNNING\",\"log\": \"激活成功\"}"

# 删除启动脚本
rm "${BASE_PATH}"/agent-start.sh
