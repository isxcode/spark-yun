# 下载spark二进制
cd /tmp
wget https://archive.apache.org/dist/spark/spark-3.4.0/spark-3.4.0-bin-hadoop3.tgz

# 解压到spark-min中
mkdir -p /workspace/spark-yun/spark-yun-dist/spark-min
tar vzxf /tmp/spark-3.4.0-bin-hadoop3.tgz --strip-components=1 -C /workspace/spark-yun/spark-yun-dist/spark-min

# 解压到/tmp下
cp -rf /workspace/spark-yun/spark-yun-dist/spark-min /tmp/spark-min

# 修改spark-defaults.conf
cp /tmp/spark-min/conf/spark-defaults.conf.template /tmp/spark-min/conf/spark-defaults.conf
tee -a /tmp/spark-min/conf/spark-defaults.conf <<-'EOF'
spark.master          spark://localhost:7077
spark.master.web.url  http://localhost:8081
EOF

# 修改spark-env.sh
cp /tmp/spark-min/conf/spark-env.sh.template /tmp/spark-min/conf/spark-env.sh
tee -a /tmp/spark-min/conf/spark-env.sh <<-'EOF'
export SPARK_MASTER_PORT=7077
export SPARK_MASTER_WEBUI_PORT=8081
export JAVA_HOME=/home/gitpod/.sdkman/candidates/java/11.0.20.fx-zulu
EOF

# 启动spark服务
bash /tmp/spark-min/sbin/start-master.sh
bash /tmp/spark-min/sbin/start-worker.sh spark://localhost:7077