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
    json_output="{ \
                      \"status\": \"INSTALL_ERROR\", \
                      \"log\": \"该系统不支持安装\" \
                    }"
      echo $json_output
      rm ${BASE_PATH}/agent-start.sh
      exit 0
fi

# 获取外部参数
home_path=""
agent_port=""
spark_local="false"
for arg in "$@"; do
  case "$arg" in
  --home-path=*) home_path="${arg#*=}" ;;
  --agent-port=*) agent_port="${arg#*=}" ;;
  --spark-local=*) spark_local="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 初始化agent_path
agent_path="${home_path}/zhiqingyun-agent"

if [ -e "${agent_path}/zhiqingyun-agent.pid" ]; then
  pid=$(cat "${agent_path}/zhiqingyun-agent.pid")
  if ps -p $pid >/dev/null 2>&1; then
    json_output="{ \
                \"status\": \"STOP\", \
                \"log\": \"请停止后，再启动代理\"
              }"
    rm ${BASE_PATH}/agent-start.sh
    exit 0
  fi
fi

# 进入代理目录,防止logs文件夹生成错位
cd ${agent_path}

# 判断zhiqingyun-agent.log是否存在,不存在则新建
if [ ! -f "${agent_path}/logs/zhiqingyun-agent.log" ]; then
  mkdir "${agent_path}/logs"
  touch "${agent_path}/logs/zhiqingyun-agent.log"
fi

# 运行代理程序
if ! command -v java &>/dev/null; then
  nohup $JAVA_HOME/bin/java -jar -Xmx2048m ${agent_path}/lib/zhiqingyun-agent.jar --server.port=${agent_port} --spring.config.additional-location=${agent_path}/conf/ > /dev/null 2>&1 &
else
  nohup java -jar -Xmx2048m ${agent_path}/lib/zhiqingyun-agent.jar --server.port=${agent_port} --spring.config.additional-location=${agent_path}/conf/ > /dev/null 2>&1 &
fi
echo $! >${agent_path}/zhiqingyun-agent.pid

# 如果用户指定spark安装,默认帮他启动spark
if [ ${spark_local} = "true" ]; then
  if [[ "$OSTYPE" == "linux-gnu" ]]; then
    nohup bash ${agent_path}/spark-min/sbin/start-all.sh > /dev/null 2>&1 &
  elif [[ "$OSTYPE" == "darwin"* ]]; then
    nohup bash ${agent_path}/spark-min/sbin/start-master.sh  > /dev/null 2>&1 &
    sleep 5
    nohup bash ${agent_path}/spark-min/sbin/start-worker.sh --webui-port 8081 spark://localhost:7077 > /dev/null 2>&1 &
  else
    json_output="{ \
                      \"status\": \"INSTALL_ERROR\", \
                      \"log\": \"该系统不支持安装\" \
                    }"
    echo $json_output
    rm ${BASE_PATH}/agent-start.sh
    exit 0
  fi
fi

# 返回结果
json_output="{ \
          \"status\": \"RUNNING\"
          \"log\": \"激活成功\",
        }"
echo $json_output

# 删除启动脚本
rm ${BASE_PATH}/agent-start.sh
