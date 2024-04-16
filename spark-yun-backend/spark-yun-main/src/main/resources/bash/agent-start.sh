#!/bin/bash

######################
# 启动脚本
######################

BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)

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

if [ -e "${home_path}/zhiqingyun-agent.pid" ]; then
  pid=$(cat "${home_path}/zhiqingyun-agent.pid")
  if ps -p $pid >/dev/null 2>&1; then
    json_output="{ \
                \"status\": \"STOP\", \
                \"log\": \"请停止后，再启动代理\"
              }"
    exit 1
  fi
fi

# 运行jar包
nohup java -jar -Xmx2048m ${home_path}/lib/zhiqingyun-agent.jar --server.port=${agent_port} >>${home_path}/logs/zhiqingyun-agent.log 2>&1 &
echo $! >${home_path}/zhiqingyun-agent.pid

# 运行spark-local
if [ ${spark_local} = "true" ]; then
  # 初始化ssh-key
  if [ ! -f ${home_path}/.ssh/id_rsa.pub ]; then
    nohup ssh-keygen -t rsa -b 4096 -N "" -f ${home_path}/.ssh/id_rsa > /dev/null 2>&1 &
  fi
  # 初始化authorized_keys
  if [ ! -f ${home_path}/.ssh/authorized_keys ]; then
    touch ${home_path}/.ssh/authorized_keys
  fi
  # 初始化免密
  if ! grep -q "$(cat ${home_path}/.ssh/id_rsa.pub)" ${home_path}/.ssh/authorized_keys; then
    sleep 2
    cat ${home_path}/.ssh/id_rsa.pub >> ${home_path}/.ssh/authorized_keys
    chmod 0600 ${home_path}/.ssh/authorized_keys
  fi
  # 修改spark的配置文件
  if netstat -tln | awk '$4 ~ /:'7077'$/ {exit 1}'; then
    interIp=$(ip addr show | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1' | head -n 1)
    sed -i.bak -E "s/spark:\/\/localhost:7077/spark:\/\/$interIp:7077/g" "${home_path}/zhiqingyun-agent/spark-min/conf/spark-defaults.conf"
    nohup bash ${home_path}/zhiqingyun-agent/spark-min/sbin/start-all.sh > /dev/null 2>&1 &
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
