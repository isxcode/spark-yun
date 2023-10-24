#!/bin/bash

echo "开始安装"

# 判断tar解压命令
if ! command -v tar &>/dev/null; then
  echo "【安装结果】：未检测到tar命令"
  exit 1
fi

# 判断是否有java命令
if ! command -v java &>/dev/null; then
  echo "【安装结果】：未检测到java命令"
  exit 1
fi

# 获取当前路径
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit

# 创建tmp目录
TMP_DIR="${BASE_PATH}"/tmp
SPARK_MIN_FILE=spark-3.4.0-bin-hadoop3.tgz
SPARK_MIN_DOWNLOAD_URL=https://archive.apache.org/dist/spark/spark-3.4.0/spark-3.4.0-bin-hadoop3.tgz
SPARK_MIN_DIR="${BASE_PATH}"/spark-yun-dist/spark-min

# 如果TMP_DIR目录不存在则新建
if [ ! -d "${TMP_DIR}" ]; then
    mkdir -p "${TMP_DIR}"
fi

# 如果没有SPARK_MIN_FILE文件，则使用SPARK_MIN_DOWNLOAD_URL下载
if [ ! -f "${TMP_DIR}"/"${SPARK_MIN_FILE}" ]; then
    cd "${TMP_DIR}"
    wget "${SPARK_MIN_DOWNLOAD_URL}" -O "${SPARK_MIN_FILE}"
    if [ $? -eq 0 ]; then
        echo "spark下载成功"
    else
        echo "【安装结果】：spark下载失败"
        exit 1
    fi
fi

# 如果没有SPARK_MIN_DIR目录，则新建
if [ ! -d "${SPARK_MIN_DIR}" ]; then
    mkdir -p "${SPARK_MIN_DIR}"
fi

# 解压SPARK_MIN_FILE，到指定目录SPARK_MIN_DIR
tar vzxf "${TMP_DIR}"/"${SPARK_MIN_FILE}" --strip-components=1 -C "${SPARK_MIN_DIR}"

# 修改spark-defaults.conf
cp "${SPARK_MIN_DIR}"/conf/spark-defaults.conf.template "${SPARK_MIN_DIR}"/conf/spark-defaults.conf
tee -a "${SPARK_MIN_DIR}"/conf/spark-defaults.conf <<-'EOF'
spark.master          spark://localhost:7077
spark.master.web.url  http://localhost:8081
EOF

# 修改spark-env.sh
cp "${SPARK_MIN_DIR}"/conf/spark-env.sh.template "${SPARK_MIN_DIR}"/conf/spark-env.sh
tee -a "${SPARK_MIN_DIR}"/conf/spark-env.sh <<-'EOF'
export SPARK_MASTER_PORT=7077
export SPARK_MASTER_WEBUI_PORT=8081
EOF

# 返回状态
echo "【安装结果】：安装成功"