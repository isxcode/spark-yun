#!/bin/bash

# =============================================================================
# 至轻云依赖下载脚本
# =============================================================================

set -e  # 遇到错误立即退出

# 配置项
readonly SPARK_VERSION="3.4.1"
readonly SPARK_MIN_FILE="spark-${SPARK_VERSION}-bin-hadoop3.tgz"
readonly FLINK_VERSION="1.18.1"
readonly FLINK_MIN_FILE="flink-${FLINK_VERSION}-bin-scala_2.12.tgz"
readonly OSS_DOWNLOAD_URL="https://zhiqingyun-demo.isxcode.com/tools/open/file"

# 路径配置
readonly BASE_PATH=$(cd "$(dirname "$0")" && pwd)
readonly TMP_DIR="${BASE_PATH}/resources/tmp"
readonly JDBC_DIR="${BASE_PATH}/resources/jdbc/system"
readonly LIBS_DIR="${BASE_PATH}/resources/libs"
readonly TMP_SPARK_MIN_JARS="${TMP_DIR}/spark-min/jars"
readonly TMP_FLINK_MIN_LIB="${TMP_DIR}/flink-min/lib"

# spark依赖列表
readonly SPARK_JARS=(
    "spark-sql-kafka-0-10_2.12-${SPARK_VERSION}.jar"
    "spark-streaming-kafka-0-10_2.12-${SPARK_VERSION}.jar"
    "spark-token-provider-kafka-0-10_2.12-${SPARK_VERSION}.jar"
    "commons-pool2-2.11.1.jar"
    "kafka-clients-3.1.2.jar"
    "bcpkix-jdk18on-1.78.1.jar"
    "bcprov-jdk18on-1.78.1.jar"
    "commons-dbutils-1.7.jar"
    "HikariCP-4.0.3.jar"
)

# flink依赖列表
readonly FLINK_JARS=(
    "flink-connector-base-${FLINK_VERSION}.jar"
    "flink-connector-jdbc-3.1.2-1.18.jar"
    "bcpkix-jdk18on-1.78.1.jar"
    "bcprov-jdk18on-1.78.1.jar"
    "bson-5.2.1.jar"
)

# 数据源驱动列表
readonly JDBC_DRIVERS=(
    "mysql-connector-j-8.1.0.jar"
    "postgresql-42.6.0.jar"
    "Dm8JdbcDriver18-8.1.1.49.jar"
    "clickhouse-jdbc-0.8.2-shaded-all.jar"
    "ngdbc-2.18.13.jar"
    "mysql-connector-java-5.1.49.jar"
    "mssql-jdbc-12.4.2.jre8.jar"
    "hive-jdbc-3.1.3-standalone.jar"
    "hive-jdbc-uber-2.6.3.0-235.jar"
    "ojdbc8-19.23.0.0.jar"
    "oceanbase-client-2.4.6.jar"
    "jcc-11.5.8.0.jar"
    "gbase-connector-java-9.5.0.7-build1-bin.jar"
    "jconn4-16.0.jar"
    "h2-2.2.224.jar"
    "presto-jdbc-0.295.jar"
    "trino-jdbc-418.jar"
    "ojdbc6.jar"
    "duckdb_jdbc-1.4.2.0.jar"
    "taos-jdbcdriver-3.7.7-dist.jar"
)

# 项目依赖列表
readonly PROJECT_JARS=(
    "prql-java-0.5.2.jar"
    "slf4j-reload4j-2.0.0.jar"
    "libprql_java-osx-arm64.dylib"
    "libprql_java-linux64.so"
)

# =============================================================================
# 工具函数
# =============================================================================

# 检查命令是否存在
check_command() {
    local cmd=$1
    local install_msg=$2

    if ! command -v "$cmd" &>/dev/null; then
        echo "未检测到 $cmd 命令，$install_msg" >&2
        exit 1
    fi
    echo "$cmd 命令检查通过"
}

# 创建目录
create_dir() {
    local dir=$1
    if [[ ! -d "$dir" ]]; then
        mkdir -p "$dir"
        echo "创建目录: $dir"
    fi
}

# 下载文件
download_file() {
    local url=$1
    local output_path=$2
    local description=$3

    if [[ -f "$output_path" ]]; then
        echo "$description 已存在，跳过下载"
        return 0
    fi

    echo "开始下载 $description..."
    if curl -ssL "$url" -o "$output_path"; then
      if head -n 1 "$output_path" | grep -q "<?xml"; then
        if grep -q "<Error>" "$output_path" && grep -q "<Code>NoSuchKey</Code>" "$output_path"; then
            rm -rf "$output_path"
            echo "下载失败，请联系管理员: ispong@outlook.com" >&2
            exit 1
        fi
      fi
      echo "$description 下载成功"
    fi
}

# =============================================================================
# 下载函数
# =============================================================================

# 检查系统依赖
check_system_dependencies() {
    echo "检查系统依赖..."

    check_command "tar" "请下载 tar"
    check_command "java" "请下载 Java"
    check_command "node" "请下载 Node.js"

    # 检查并下载 pnpm
    if ! command -v "pnpm" &>/dev/null; then
        echo "未检测到 pnpm，正在下载..."
        npm install pnpm@9.0.6 -g
        echo "pnpm 下载完成"
    else
        echo "pnpm 命令检查通过"
    fi
}

# 下载spark
download_spark() {
    echo "下载 Spark ${SPARK_VERSION}..."

    # 创建必要目录
    create_dir "$TMP_DIR"

    # 下载 Spark
    local spark_url="${OSS_DOWNLOAD_URL}/${SPARK_MIN_FILE}"
    local spark_path="${TMP_DIR}/${SPARK_MIN_FILE}"
    download_file "$spark_url" "$spark_path" "Spark ${SPARK_VERSION} 二进制文件，请耐心等待"
}

# 下载flink
download_flink() {
    echo "下载 Flink ${FLINK_VERSION}..."

    # 创建必要目录
    create_dir "$TMP_DIR"

    # 下载 Flink
    local flink_url="${OSS_DOWNLOAD_URL}/${FLINK_MIN_FILE}"
    local flink_path="${TMP_DIR}/${FLINK_MIN_FILE}"
    download_file "$flink_url" "$flink_path" "Flink ${TMP_DIR} 二进制文件，请耐心等待"
}

# 下载 Spark JAR 依赖
download_spark_jars() {
    echo "下载 Spark JAR 依赖..."

    # 创建必要目录
    create_dir "$TMP_SPARK_MIN_JARS"

    local spark_jar_dir="${TMP_SPARK_MIN_JARS}"

    # 批量下载 JAR 文件
    for jar in "${SPARK_JARS[@]}"; do
        local jar_url="${OSS_DOWNLOAD_URL}/${jar}"
        local jar_path="${spark_jar_dir}/${jar}"
        download_file "$jar_url" "$jar_path" "Spark JAR: $jar"
    done
}

# 下载 Flink JAR 依赖
download_flink_jars() {
    echo "下载 Flink JAR 依赖..."

    # 创建必要目录
    create_dir "$TMP_FLINK_MIN_LIB"

    local flink_jar_dir="${TMP_FLINK_MIN_LIB}"

    # 批量下载 JAR 文件
    for jar in "${FLINK_JARS[@]}"; do
        local jar_url="${OSS_DOWNLOAD_URL}/${jar}"
        local jar_path="${flink_jar_dir}/${jar}"
        download_file "$jar_url" "$jar_path" "Flink JAR: $jar"
    done
}

# 下载数据库驱动
download_jdbc_drivers() {
    echo "下载数据库驱动..."

    # 创建 JDBC 驱动目录
    create_dir "$JDBC_DIR"

    # 批量下载驱动文件
    for driver in "${JDBC_DRIVERS[@]}"; do
        local driver_url="${OSS_DOWNLOAD_URL}/${driver}"
        local driver_path="${JDBC_DIR}/${driver}"
        download_file "$driver_url" "$driver_path" "数据库驱动: $driver"
    done
}

# 下载项目依赖
download_project_dependencies() {
    echo "下载项目依赖..."

    # 创建项目依赖目录
    create_dir "$LIBS_DIR"

    # 下载项目 JAR 依赖
    for jar in "${PROJECT_JARS[@]}"; do
        local jar_url="${OSS_DOWNLOAD_URL}/${jar}"
        local jar_path="${LIBS_DIR}/${jar}"
        download_file "$jar_url" "$jar_path" "项目依赖: $jar"
    done
}

# =============================================================================
# 主要下载流程
# =============================================================================

main() {
    echo "开始下载至轻云项目依赖..."

    # 1. 检查系统依赖
    check_system_dependencies

    # 2. 下载Spark
    download_spark

    # 3. 下载Spark第三方依赖
    download_spark_jars

    # 4. 下载Fink
    download_flink

    # 5. 下载Flink第三方依赖
    download_flink_jars

    # 6. 下载数据库驱动
    download_jdbc_drivers

    # 7. 下载项目依赖
    download_project_dependencies

    echo "项目依赖下载完成！"
}

# =============================================================================
# 脚本入口
# =============================================================================

# 切换到脚本所在目录
cd "$BASE_PATH" || {
    echo "无法切换到项目目录: $BASE_PATH" >&2
    exit 1
}

# 执行主函数
main