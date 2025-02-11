#!/bin/bash

echo "开始安装"

# 检查tar命令
if ! command -v tar &>/dev/null; then
  echo "【安装结果】：未检测到tar命令,请安装tar"
  exit 1
fi

# 检查java命令
if ! command -v java &>/dev/null; then
  echo "【安装结果】：未检测到java命令，请安装java"
  exit 1
fi

# 检查node命令
if ! command -v node &>/dev/null; then
  echo "【安装结果】：未检测到node命令，请安装nodejs"
  exit 1
fi

# 检查pnpm命令
if ! command -v pnpm &>/dev/null; then
   echo "【提示】：未检测到pnpm命令，已经执行命令: npm install pnpm@9.0.6 -g"
   npm install pnpm@9.0.6 -g
fi

# 进入项目目录
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit

# 创建tmp目录
TMP_DIR="${BASE_PATH}"/resources/tmp
SPARK_MIN_FILE=spark-3.4.1-bin-hadoop3.tgz
OSS_DOWNLOAD_URL=https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/install
SPARK_MIN_DOWNLOAD_URL="${OSS_DOWNLOAD_URL}"/"${SPARK_MIN_FILE}"
SPARK_MIN_DIR="${BASE_PATH}"/spark-yun-dist/spark-min

# 创建tmp目录
if [ ! -d "${TMP_DIR}" ]; then
    mkdir -p "${TMP_DIR}"
fi

# 下载spark二进制文件
if [ ! -f "${TMP_DIR}"/"${SPARK_MIN_FILE}" ]; then
    echo "spark-3.4.1开始下载，请耐心等待"
    curl -ssL "${SPARK_MIN_DOWNLOAD_URL}" -o "${TMP_DIR}"/"${SPARK_MIN_FILE}"
    if [ $? -eq 0 ]; then
        echo "spark-3.4.1下载成功"
    else
        echo "【安装结果】：spark-3.4.1下载失败"
        exit 1
    fi
fi

# 创建spark-min目录
if [ ! -d "${SPARK_MIN_DIR}" ]; then
    mkdir -p "${SPARK_MIN_DIR}"
fi

# 解压spark程序，并删除不需要的文件
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

# 下载spark的jars依赖
SPARK_JAR_DIR="${SPARK_MIN_DIR}"/jars
if [ ! -f "${SPARK_JAR_DIR}"/spark-sql-kafka-0-10_2.12-3.4.1.jar ]; then
  echo "spark-sql-kafka-0-10_2.12-3.4.1.jar开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/spark-sql-kafka-0-10_2.12-3.4.1.jar -o "${SPARK_JAR_DIR}"/spark-sql-kafka-0-10_2.12-3.4.1.jar
  echo "spark-sql-kafka-0-10_2.12-3.4.1.jar下载成功"
fi
if [ ! -f "${SPARK_JAR_DIR}"/spark-streaming-kafka-0-10_2.12-3.4.1.jar ]; then
  echo "spark-streaming-kafka-0-10_2.12-3.4.1.jar开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/spark-streaming-kafka-0-10_2.12-3.4.1.jar -o "${SPARK_JAR_DIR}"/spark-streaming-kafka-0-10_2.12-3.4.1.jar
  echo "spark-streaming-kafka-0-10_2.12-3.4.1.jar下载成功"
fi
if [ ! -f "${SPARK_JAR_DIR}"/spark-token-provider-kafka-0-10_2.12-3.4.1.jar ]; then
  echo "spark-token-provider-kafka-0-10_2.12-3.4.1.jar开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/spark-token-provider-kafka-0-10_2.12-3.4.1.jar -o "${SPARK_JAR_DIR}"/spark-token-provider-kafka-0-10_2.12-3.4.1.jar
  echo "spark-token-provider-kafka-0-10_2.12-3.4.1.jar下载成功"
fi
if [ ! -f "${SPARK_JAR_DIR}"/commons-pool2-2.11.1.jar ]; then
  echo "commons-pool2-2.11.1.jar开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/commons-pool2-2.11.1.jar -o "${SPARK_JAR_DIR}"/commons-pool2-2.11.1.jar
  echo "commons-pool2-2.11.1.jar下载成功"
fi
if [ ! -f "${SPARK_JAR_DIR}"/kafka-clients-3.1.2.jar ]; then
  echo "kafka-clients-3.1.2.jar开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/kafka-clients-3.1.2.jar -o "${SPARK_JAR_DIR}"/kafka-clients-3.1.2.jar
  echo "kafka-clients-3.1.2.jar下载成功"
fi
if [ ! -f "${SPARK_JAR_DIR}"/bcpkix-jdk18on-1.78.1.jar ]; then
  echo "bcpkix-jdk18on-1.78.1.jar开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/bcpkix-jdk18on-1.78.1.jar -o "${SPARK_JAR_DIR}"/bcpkix-jdk18on-1.78.1.jar
  echo "bcpkix-jdk18on-1.78.1.jar下载成功"
fi
if [ ! -f "${SPARK_JAR_DIR}"/bcprov-jdk18on-1.78.1.jar ]; then
  echo "bcprov-jdk18on-1.78.1.jar开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/bcprov-jdk18on-1.78.1.jar -o "${SPARK_JAR_DIR}"/bcprov-jdk18on-1.78.1.jar
  echo "bcprov-jdk18on-1.78.1.jar下载成功"
fi
if [ ! -f "${SPARK_JAR_DIR}"/commons-dbutils-1.7.jar ]; then
  echo "commons-dbutils-1.7.jar开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/commons-dbutils-1.7.jar -o "${SPARK_JAR_DIR}"/commons-dbutils-1.7.jar
  echo "commons-dbutils-1.7.jar下载成功"
fi
if [ ! -f "${SPARK_JAR_DIR}"/HikariCP-4.0.3.jar ]; then
  echo "HikariCP-4.0.3.jar开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/HikariCP-4.0.3.jar -o "${SPARK_JAR_DIR}"/HikariCP-4.0.3.jar
  echo "HikariCP-4.0.3.jar下载成功"
fi

# 创建系统驱动目录
JDBC_DIR="${BASE_PATH}"/resources/jdbc/system
if [ ! -d "${JDBC_DIR}" ]; then
    mkdir -p "${JDBC_DIR}"
fi

# 下载数据库驱动文件
if [ ! -f "${JDBC_DIR}"/mysql-connector-j-8.1.0.jar ]; then
  echo "mysql-connector-j-8.1.0.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/mysql-connector-j-8.1.0.jar -o ${JDBC_DIR}/mysql-connector-j-8.1.0.jar
  echo "mysql-connector-j-8.1.0.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/postgresql-42.6.0.jar ]; then
  echo "postgresql-42.6.0.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/postgresql-42.6.0.jar -o ${JDBC_DIR}/postgresql-42.6.0.jar
  echo "postgresql-42.6.0.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/Dm8JdbcDriver18-8.1.1.49.jar ]; then
  echo "Dm8JdbcDriver18-8.1.1.49.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/Dm8JdbcDriver18-8.1.1.49.jar -o ${JDBC_DIR}/Dm8JdbcDriver18-8.1.1.49.jar
  echo "Dm8JdbcDriver18-8.1.1.49.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/clickhouse-jdbc-0.5.0.jar ]; then
  echo "clickhouse-jdbc-0.5.0.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/clickhouse-jdbc-0.5.0.jar -o ${JDBC_DIR}/clickhouse-jdbc-0.5.0.jar
  echo "clickhouse-jdbc-0.5.0.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/ngdbc-2.18.13.jar ]; then
  echo "ngdbc-2.18.13.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/ngdbc-2.18.13.jar -o ${JDBC_DIR}/ngdbc-2.18.13.jar
  echo "ngdbc-2.18.13.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/mysql-connector-java-5.1.49.jar ]; then
  echo "mysql-connector-java-5.1.49.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/mysql-connector-java-5.1.49.jar -o ${JDBC_DIR}/mysql-connector-java-5.1.49.jar
  echo "mysql-connector-java-5.1.49.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/mssql-jdbc-12.4.2.jre8.jar ]; then
  echo "mssql-jdbc-12.4.2.jre8.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/mssql-jdbc-12.4.2.jre8.jar -o ${JDBC_DIR}/mssql-jdbc-12.4.2.jre8.jar
  echo "mssql-jdbc-12.4.2.jre8.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/hive-jdbc-3.1.3-standalone.jar ]; then
  echo "hive-jdbc-3.1.3-standalone.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/hive-jdbc-3.1.3-standalone.jar -o ${JDBC_DIR}/hive-jdbc-3.1.3-standalone.jar
  echo "hive-jdbc-3.1.3-standalone.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/hive-jdbc-uber-2.6.3.0-235.jar ]; then
  echo "hive-jdbc-2.1.1-standalone.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/hive-jdbc-uber-2.6.3.0-235.jar -o ${JDBC_DIR}/hive-jdbc-uber-2.6.3.0-235.jar
  echo "hive-jdbc-2.1.1-standalone.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/ojdbc8-19.23.0.0.jar ]; then
  echo "ojdbc8-19.23.0.0.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/ojdbc8-19.23.0.0.jar -o ${JDBC_DIR}/ojdbc8-19.23.0.0.jar
  echo "ojdbc8-19.23.0.0.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/oceanbase-client-2.4.6.jar ]; then
  echo "oceanbase-client-2.4.6.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/oceanbase-client-2.4.6.jar -o ${JDBC_DIR}/oceanbase-client-2.4.6.jar
  echo "oceanbase-client-2.4.6.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/jcc-11.5.8.0.jar ]; then
  echo "jcc-11.5.8.0.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/jcc-11.5.8.0.jar -o ${JDBC_DIR}/jcc-11.5.8.0.jar
  echo "jcc-11.5.8.0.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/h2-2.2.224.jar ]; then
  echo "h2-2.2.224.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/h2-2.2.224.jar -o ${JDBC_DIR}/h2-2.2.224.jar
  echo "h2-2.2.224.jar驱动下载成功"
fi

# 创建项目依赖文件夹
LIBS_DIR="${BASE_PATH}"/resources/libs
if [ ! -d "${LIBS_DIR}" ]; then
    mkdir -p "${LIBS_DIR}"
fi

# 下载项目第三方依赖
if [ ! -f "${LIBS_DIR}"/prql-java-0.5.2.jar ]; then
  echo "prql-java-0.5.2.jar开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/prql-java-0.5.2.jar -o ${LIBS_DIR}/prql-java-0.5.2.jar
  echo "prql-java-0.5.2.jar下载成功"
fi

# 下载prql文件
# prql 二进制文件(mac arm64)
if [ ! -f "${BASE_PATH}"/spark-yun-backend/spark-yun-main/src/main/resources/libprql_java-osx-arm64.dylib ]; then
  echo "prql_java-osx-arm64.dylib开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/libprql_java-osx-arm64.dylib -o ${BASE_PATH}/spark-yun-backend/spark-yun-main/src/main/resources/libprql_java-osx-arm64.dylib
  echo "prql_java-osx-arm64.dylib下载成功"
fi
# prql 二进制文件(linux amd64)
if [ ! -f "${BASE_PATH}"/spark-yun-backend/spark-yun-main/src/main/resources/libprql_java-linux64.so ]; then
  echo "prql_java-linux64.so开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/libprql_java-linux64.so -o ${BASE_PATH}/spark-yun-backend/spark-yun-main/src/main/resources/libprql_java-linux64.so
  echo "prql_java-linux64.so下载成功"
fi

# 返回状态
echo "【安装结果】：安装成功"