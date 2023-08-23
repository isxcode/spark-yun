#!/bin/bash

######################
# 清理日志脚本
######################

# hdfs 78% -> 48%
rm -rf /tmp/hadoop-zhiqingyun/nm-local-dir/usercache/zhiqingyun/filecache

# spark 48% -> 34%
rm -rf /data/spark/spark-3.4.0-bin-hadoop3/work

# k8s 34% -> 34%
kubectl delete --all pods --namespace=zhiqingyun-space

# docker 34% -> 34%
docker ps -a | grep 'k8s_POD_zhiqingyun-job-*' | awk '{print $1}' | xargs docker rm