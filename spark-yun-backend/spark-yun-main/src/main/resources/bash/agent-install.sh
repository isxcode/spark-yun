#!/bin/bash

##############################
# 环境检测通过,开始执行安装
##############################

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
      rm ${BASE_PATH}/agent-install.sh
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

# 导入用户自己配置的环境变量
source ${agent_path}/conf/agent-env.sh

# 将文件解压到指定目录
tar -xf ${BASE_PATH}/zhiqingyun-agent.tar.gz -C ${home_path} > /dev/null

# 将spark-min的文件夹改成755权限
chmod -R 755 ${agent_path}/spark-min

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

# 如果用户需要默认spark
if [ ${spark_local} = "true" ]; then

  # 如何需要本地安装，则自动修改配置
  # 修改spark-defaults.conf
  if [ ! -f ${agent_path}/spark-min/conf/spark-defaults.conf ]; then
    cp ${agent_path}/spark-min/conf/spark-defaults.conf.template ${agent_path}/spark-min/conf/spark-defaults.conf
    cat <<-'EOF' >> ${agent_path}/spark-min/conf/spark-defaults.conf
spark.master          spark://localhost:7077
spark.master.web.url  http://localhost:8081
EOF
  fi

  # 修改spark-env.sh
  if [ ! -f ${agent_path}/spark-min/conf/spark-env.sh ]; then
    cp ${agent_path}/spark-min/conf/spark-env.sh.template ${agent_path}/spark-min/conf/spark-env.sh
    cat <<-'EOF' >> ${agent_path}/spark-min/conf/spark-env.sh
export SPARK_MASTER_PORT=7077
export SPARK_MASTER_WEBUI_PORT=8081
export SPARK_WORKER_OPTS="-Dspark.worker.cleanup.enabled=true -Dspark.worker.cleanup.appDataTtl=1800 -Dspark.worker.cleanup.interval=60"
EOF
  fi

  # 如果用户没有id_rsa.pub,帮他初始化
  if [ ! -f ~/.ssh/id_rsa.pub ]; then
    nohup ssh-keygen -t rsa -b 4096 -N "" -f ~/.ssh/id_rsa > /dev/null 2>&1 &
  fi
  # 如果用户没有authorized_keys,帮他初始化
  if [ ! -f ~/.ssh/authorized_keys ]; then
    touch ~/.ssh/authorized_keys
  fi
  # 如果用户没有配置免密,帮他配置
  if ! grep -q "$(cat ~/.ssh/id_rsa.pub)" ~/.ssh/authorized_keys; then
    sleep 5
    cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
    chmod 0600 ~/.ssh/authorized_keys
  fi

  if [[ "$OSTYPE" == "linux-gnu" ]]; then
    # 修改spark的配置文件,修改为内网ip
    if netstat -tln | awk '$4 ~ /:'7077'$/ {exit 1}'; then
      interIp=$(ip addr show | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1' | head -n 1)
      sed -i.bak -E "s/spark:\/\/localhost:7077/spark:\/\/$interIp:7077/g" ${agent_path}/spark-min/conf/spark-defaults.conf
      nohup bash ${agent_path}/spark-min/sbin/start-all.sh > /dev/null 2>&1 &
    fi
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
    rm ${BASE_PATH}/agent-install.sh
    exit 0
  fi
fi

# 检查是否安装
if [ -e "${agent_path}/zhiqingyun-agent.pid" ]; then
  pid=$(cat "${agent_path}/zhiqingyun-agent.pid")
  sleep 10
  if ps -p $pid >/dev/null 2>&1; then
    json_output="{ \
              \"status\": \"RUNNING\",\
              \"log\": \"安装成功\" \
            }"
    echo $json_output
  else
    json_output="{ \
              \"status\": \"STOP\",\
              \"log\": \"已安装，请激活\" \
            }"
    echo $json_output
  fi
fi

# 删除安装包 和 安装脚本
rm ${BASE_PATH}/zhiqingyun-agent.tar.gz && rm ${BASE_PATH}/agent-install.sh