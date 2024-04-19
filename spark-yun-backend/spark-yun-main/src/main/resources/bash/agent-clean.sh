#!/bin/bash

######################
# 清理代理
######################

# 获取脚本文件当前路径
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)

# 获取外部参数
user=""
for arg in "$@"; do
  case "$arg" in
  --user=*) user="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 删除hdfs用户缓存文件
rm -rf /tmp/hadoop-*/nm-local-dir/usercache/${user}/filecache

# 删除hdfs中失败的spark作业缓存
if command -v hadoop &>/dev/null; then
  hadoop fs -rm -r /user/${user}/.sparkStaging
fi

# 清理k8s中容器
if command -v kubectl &>/dev/null; then
  kubectl delete --all pods --namespace=zhiqingyun-space
fi

# 清理docker中的容器
if command -v docker &>/dev/null; then
  docker ps -a | grep 'k8s_POD_zhiqingyun-*' | awk '{print $1}' | xargs docker rm
fi

# 返回结果
json_output="{ \
          \"status\": \"CLEAN_SUCCESS\",
          \"log\": \"清理成功\"
        }"
echo $json_output

# 删除脚本
rm ${BASE_PATH}/agent-clean.sh