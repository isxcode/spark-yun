#!/bin/bash

######################
# 卸载脚本
######################

BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)

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
hadoop fs -rm -r /user/${user}/.sparkStaging

# 删除spark中的work缓存
rm -rf /opt/spark/work

# 清理k8s中容器
kubectl delete --all pods --namespace=zhiqingyun-space

# 清理docker中的容器
docker ps -a | grep 'k8s_POD_zhiqingyun-job-*' | awk '{print $1}' | xargs docker rm

# 返回结果
json_output="{ \
          \"status\": \"CLEAN_SUCCESS\",
          \"log\": \"清理成功\"
        }"
echo $json_output

# 删除脚本
rm ${BASE_PATH}/agent-clean.sh