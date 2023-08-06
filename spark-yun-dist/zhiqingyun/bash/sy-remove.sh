#!/bin/bash

######################
# 卸载脚本
######################

home_path=""
for arg in "$@"; do
  case "$arg" in
  --home-path=*) home_path="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 关闭进程
if [ -e "${home_path}/spark-yun-agent.pid" ]; then
  pid=$(cat "${home_path}/spark-yun-agent.pid")
  if ps -p $pid >/dev/null 2>&1; then
    kill -9 ${pid}
  fi
fi

# 删除安装包
rm -rf ${home_path}

# 返回结果
json_output="{ \
          \"status\": \"UN_INSTALL\",
          \"log\": \"卸载成功\"
        }"
echo $json_output

# 删除脚本
rm /tmp/sy-remove.sh

