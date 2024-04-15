#!/bin/bash

######################
# 安装脚本
######################

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

home_path=""
agent_port=""
for arg in "$@"; do
  case "$arg" in
  --home-path=*) home_path="${arg#*=}" ;;
  --agent-port=*) agent_port="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 判断路径是否存在
if [ ! -d "${home_path}/zhiqingyun-agent" ]; then
  json_output="{ \
            \"status\": \"INSTALL_ERROR\",\
            \"log\": \"安装目录不存在"${home_path}/zhiqingyun-agent"\" \
            }"
  echo $json_output
  exit 0
fi

cd "${home_path}"/zhiqingyun-agent || exit

# 将文件解压到指定目录
tar -xf /tmp/zhiqingyun-agent.tar.gz -C ${home_path} > /dev/null

# 判断zhiqingyun-agent.log是否存在,不存在则新建
if [ ! -f logs/zhiqingyun-agent.log ]; then
  mkdir logs
  touch logs/zhiqingyun-agent.log
fi

# 运行jar包
nohup java -jar -Xmx2048m lib/zhiqingyun-agent.jar --server.port=${agent_port} --spring.config.additional-location=conf/ > /dev/null 2>&1 &
echo $! >zhiqingyun-agent.pid

# 检查是否安装
if [ -e "zhiqingyun-agent.pid" ]; then
  pid=$(cat "zhiqingyun-agent.pid")
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
rm /tmp/zhiqingyun-agent.tar.gz && rm /tmp/agent-install.sh