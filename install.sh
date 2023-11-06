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
if [ ! -f "${SPARK_MIN_DIR}"/README.md ]; then
  tar vzxf "${TMP_DIR}"/"${SPARK_MIN_FILE}" --strip-components=1 -C "${SPARK_MIN_DIR}"
  rm -rf "${SPARK_MIN_DIR}"/data
  rm -rf "${SPARK_MIN_DIR}"/examples
  rm -rf "${SPARK_MIN_DIR}"/licenses
  rm -rf "${SPARK_MIN_DIR}"/python
  rm -rf "${SPARK_MIN_DIR}"/R
  rm "${SPARK_MIN_DIR}"/LICENSE
  rm "${SPARK_MIN_DIR}"/NOTICE
  rm "${SPARK_MIN_DIR}"/RELEASE
fi

# 修改spark-defaults.conf
if [ ! -f "${SPARK_MIN_DIR}"/conf/spark-defaults.conf ]; then
  cp "${SPARK_MIN_DIR}"/conf/spark-defaults.conf.template "${SPARK_MIN_DIR}"/conf/spark-defaults.conf
  tee -a "${SPARK_MIN_DIR}"/conf/spark-defaults.conf <<-'EOF'
spark.master          spark://localhost:7077
spark.master.web.url  http://localhost:8081
EOF
fi

# 修改spark-env.sh
if [ ! -f "${SPARK_MIN_DIR}"/conf/spark-env.sh ]; then
cp "${SPARK_MIN_DIR}"/conf/spark-env.sh.template "${SPARK_MIN_DIR}"/conf/spark-env.sh
  tee -a "${SPARK_MIN_DIR}"/conf/spark-env.sh <<-'EOF'
export SPARK_MASTER_PORT=7077
export SPARK_MASTER_WEBUI_PORT=8081
EOF
fi

# 创建resources文件夹
JDBC_DIR="${BASE_PATH}"/resources/jdbc/system

if [ ! -d "${JDBC_DIR}" ]; then
    mkdir -p "${JDBC_DIR}"
fi

# 下载mysql8驱动
if [ ! -f "${JDBC_DIR}"/mysql-connector-j-8.1.0.jar ]; then
  wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.1.0/mysql-connector-j-8.1.0.jar -O ${JDBC_DIR}/mysql-connector-j-8.1.0.jar
  echo "mysql-connector-j-8.1.0.jar驱动下载成功"
fi

# 下载postgresql驱动
if [ ! -f "${JDBC_DIR}"/postgresql-42.6.0.jar ]; then
  wget https://repo1.maven.org/maven2/org/postgresql/postgresql/42.6.0/postgresql-42.6.0.jar -O ${JDBC_DIR}/postgresql-42.6.0.jar
  echo "postgresql-42.6.0.jar驱动下载成功"
fi

# 下载dm驱动
if [ ! -f "${JDBC_DIR}"/Dm8JdbcDriver18-8.1.1.49.jar ]; then
  wget https://repo1.maven.org/maven2/com/dameng/Dm8JdbcDriver18/8.1.1.49/Dm8JdbcDriver18-8.1.1.49.jar -O ${JDBC_DIR}/Dm8JdbcDriver18-8.1.1.49.jar
  echo "Dm8JdbcDriver18-8.1.1.49.jar驱动下载成功"
fi

# 下载clickhouse驱动
if [ ! -f "${JDBC_DIR}"/clickhouse-jdbc-0.5.0.jar ]; then
  wget https://repo1.maven.org/maven2/com/clickhouse/clickhouse-jdbc/0.5.0/clickhouse-jdbc-0.5.0.jar -O ${JDBC_DIR}/clickhouse-jdbc-0.5.0.jar
  echo "clickhouse-jdbc-0.5.0.jar驱动下载成功"
fi

# 下载hana驱动
if [ ! -f "${JDBC_DIR}"/ngdbc-2.18.13.jar ]; then
  wget https://repo1.maven.org/maven2/com/sap/cloud/db/jdbc/ngdbc/2.18.13/ngdbc-2.18.13.jar -O ${JDBC_DIR}/ngdbc-2.18.13.jar
  echo "ngdbc-2.18.13.jar驱动下载成功"
fi

# 下载doris驱动
if [ ! -f "${JDBC_DIR}"/mysql-connector-java-5.1.49.jar ]; then
  wget https://repo1.maven.org/maven2/mysql/mysql-connector-java/5.1.49/mysql-connector-java-5.1.49.jar -O ${JDBC_DIR}/mysql-connector-java-5.1.49.jar
  echo "mysql-connector-java-5.1.49.jar驱动下载成功"
fi

# 下载sqlserver驱动
if [ ! -f "${JDBC_DIR}"/mssql-jdbc-12.4.2.jre8.jar ]; then
  wget https://repo1.maven.org/maven2/com/microsoft/sqlserver/mssql-jdbc/12.4.2.jre8/mssql-jdbc-12.4.2.jre8.jar -O ${JDBC_DIR}/mssql-jdbc-12.4.2.jre8.jar
  echo "mssql-jdbc-12.4.2.jre8.jar驱动下载成功"
fi

# 下载hive3驱动
if [ ! -f "${JDBC_DIR}"/hive-jdbc-3.1.3-standalone.jar ]; then
  wget https://repo1.maven.org/maven2/org/apache/hive/hive-jdbc/3.1.3/hive-jdbc-3.1.3-standalone.jar -O ${JDBC_DIR}/hive-jdbc-3.1.3-standalone.jar
  echo "hive-jdbc-3.1.3-standalone.jar驱动下载成功"
fi

# 下载hive2驱动
if [ ! -f "${JDBC_DIR}"/hive-jdbc-uber-2.6.3.0-235.jar ]; then
  wget https://github.com/timveil/hive-jdbc-uber-jar/releases/download/v1.8-2.6.3/hive-jdbc-uber-2.6.3.0-235.jar -O ${JDBC_DIR}/hive-jdbc-uber-2.6.3.0-235.jar
  echo "hive-jdbc-2.1.1-standalone.jar驱动下载成功"
fi

# 下载oracle驱动
if [ ! -f "${JDBC_DIR}"/ojdbc10-19.20.0.0.jar ]; then
  wget https://repo1.maven.org/maven2/com/oracle/database/jdbc/ojdbc10/19.20.0.0/ojdbc10-19.20.0.0.jar -O ${JDBC_DIR}/ojdbc10-19.20.0.0.jar
  echo "ojdbc10-19.20.0.0.jar驱动下载成功"
fi

# 下载oceanbase驱动
if [ ! -f "${JDBC_DIR}"/oceanbase-client-2.4.6.jar ]; then
  wget https://repo1.maven.org/maven2/com/oceanbase/oceanbase-client/2.4.6/oceanbase-client-2.4.6.jar -O ${JDBC_DIR}/oceanbase-client-2.4.6.jar
  echo "oceanbase-client-2.4.6.jar驱动下载成功"
fi

# 下载db2驱动
if [ ! -f "${JDBC_DIR}"/jcc-11.5.8.0.jar ]; then
  wget https://repo1.maven.org/maven2/com/ibm/db2/jcc/11.5.8.0/jcc-11.5.8.0.jar -O ${JDBC_DIR}/jcc-11.5.8.0.jar
  echo "jcc-11.5.8.0.jar驱动下载成功"
fi

# 返回状态
echo "【安装结果】：安装成功"