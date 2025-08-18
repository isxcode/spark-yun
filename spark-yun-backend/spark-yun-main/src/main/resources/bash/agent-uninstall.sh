#!/bin/bash

######################
# 单独卸载代理器
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
    echo "{ \"status\": \"INSTALL_ERROR\",\"log\": \"该系统不支持安装\"}"
    rm "${BASE_PATH}"/agent-uninstall.sh
    exit 0
fi

# 获取外部参数
home_path=""
spark_local="false"
flink_local="false"
for arg in "$@"; do
  case "$arg" in
  --home-path=*) home_path="${arg#*=}" ;;
  --spark-local=*) spark_local="${arg#*=}" ;;
  --flink-local=*) flink_local="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 初始化agent_path
agent_path="${home_path}/zhiqingyun-agent"

# 如果进程存在,杀死进程
if [ -e "${agent_path}/zhiqingyun-agent.pid" ]; then
  pid=$(cat "${agent_path}/zhiqingyun-agent.pid")
  if ps -p "$pid" >/dev/null 2>&1; then
    kill -9 "${pid}"
  fi
fi

# 停止spark
if [ "${spark_local}" = "true" ]; then
  nohup bash "${agent_path}"/spark-min/sbin/stop-all.sh > /dev/null 2>&1 &
fi

# 停止flink
if [ "${flink_local}" = "true" ]; then
  nohup bash "${agent_path}"/flink-min/bin/stop-cluster.sh > /dev/null 2>&1 &
fi

sleep 10

# 删除安装目录
rm -rf "${agent_path}"

# 返回结果
echo "{ \"status\": \"UN_INSTALL\", \"log\": \"卸载成功\"}"

# 删除脚本
rm "${BASE_PATH}"/agent-uninstall.sh