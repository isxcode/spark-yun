#!/bin/bash

# =============================================================================
# 至轻云依赖安装脚本
# =============================================================================

set -e  # 遇到错误立即退出

# 配置项
readonly SPARK_VERSION="4.1.2"
readonly SPARK_MIN_FILE="spark-${SPARK_VERSION}-bin-hadoop3.tgz"
readonly FLINK_VERSION="2.2.0"
readonly FLINK_MIN_FILE="flink-${FLINK_VERSION}-bin-scala_2.12.tgz"
readonly OSS_DOWNLOAD_URL="https://zhiqingyun-demo.isxcode.com/tools/open/file"

# 路径配置
readonly BASE_PATH=$(cd "$(dirname "$0")" && pwd)
readonly TMP_DIR="${BASE_PATH}/resources/tmp"
readonly SPARK_MIN_DIR="${BASE_PATH}/spark-yun-dist/spark-min"
readonly FLINK_MIN_DIR="${BASE_PATH}/spark-yun-dist/flink-min"
readonly JDBC_DIR="${BASE_PATH}/resources/jdbc/system"
readonly LIBS_DIR="${BASE_PATH}/resources/libs"
readonly RESOURCE_DIR="${BASE_PATH}/spark-yun-backend/spark-yun-main/src/main/resources"
readonly TMP_SPARK_MIN_JARS="${TMP_DIR}/spark-min/jars"
readonly TMP_FLINK_MIN_LIB="${TMP_DIR}/flink-min/lib"
readonly TMP_JDBC_DIR="${TMP_DIR}/jdbc/system"
readonly TMP_LIBS_DIR="${TMP_DIR}/libs"

# spark依赖列表
readonly SPARK_JARS=(
    "spark-sql-kafka-0-10_2.13-${SPARK_VERSION}.jar"
    "spark-streaming-kafka-0-10_2.13-${SPARK_VERSION}.jar"
    "spark-token-provider-kafka-0-10_2.13-${SPARK_VERSION}.jar"
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
    "flink-connector-jdbc-3.3.0-1.20.jar"
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
    "hive-jdbc-4.1.0-standalone.jar"
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
readonly PROJECT_LIBS=(
    "prql-java-0.5.2.jar"
    "slf4j-reload4j-2.0.0.jar"
    "libprql_java-osx-arm64.dylib"
    "libprql_java-linux-aarch64.so"
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
        echo "$cmd command not found, $install_msg" >&2
        exit 1
    fi
}

# 创建目录
create_dir() {
    local dir=$1
    if [[ ! -d "$dir" ]]; then
        mkdir -p "$dir"
        echo "Creating directory: $dir"
    fi
}

# 下载文件
download_file() {
    local url=$1
    local output_path=$2
    local description=$3

    if [[ -f "$output_path" ]]; then
        return 0
    fi

    echo "Starting download of $description..."
    if curl -ssL --retry 3 --retry-delay 2 "$url" -o "$output_path"; then
        if head -n 1 "$output_path" | grep -q "<?xml"; then
            if grep -q "<Error>" "$output_path" && grep -q "<Code>NoSuchKey</Code>" "$output_path"; then
                rm -rf "$output_path"
                echo "Download failed, please contact administrator: ispong@outlook.com" >&2
                exit 1
            fi
        fi
        echo "$description downloaded successfully"
    else
        rm -rf "$output_path"
        echo "$description download failed" >&2
        exit 1
    fi
}

# 拷贝缺失文件
copy_file_if_missing() {
    local source_file=$1
    local target_dir=$2
    local description=$3
    local target_file="${target_dir}/$(basename "$source_file")"

    if [[ -f "$target_file" ]]; then
        return 0
    fi

    create_dir "$target_dir"
    echo "Copying $description..."
    cp "$source_file" "$target_dir"/
}

# =============================================================================
# 下载函数
# =============================================================================

# 检查系统依赖
check_system_dependencies() {
    check_command "tar" "Please install tar"
    check_command "java" "Please install Java"
    check_command "node" "Please install Node.js"

    # 检查并下载 pnpm
    if ! command -v "pnpm" &>/dev/null; then
        echo "pnpm not detected, downloading..."
        npm install pnpm@9.0.6 -g
        echo "pnpm download completed"
    fi
}

# 下载spark
download_spark() {
    create_dir "$TMP_DIR"

    local spark_url="${OSS_DOWNLOAD_URL}/${SPARK_MIN_FILE}"
    local spark_path="${TMP_DIR}/${SPARK_MIN_FILE}"
    download_file "$spark_url" "$spark_path" "Spark ${SPARK_VERSION} binary file, please be patient"
}

# 下载flink
download_flink() {
    create_dir "$TMP_DIR"

    local flink_url="${OSS_DOWNLOAD_URL}/${FLINK_MIN_FILE}"
    local flink_path="${TMP_DIR}/${FLINK_MIN_FILE}"
    download_file "$flink_url" "$flink_path" "Flink ${FLINK_VERSION} binary file, please be patient"
}

# 下载 Spark JAR 依赖
download_spark_jars() {
    create_dir "$TMP_SPARK_MIN_JARS"

    for jar in "${SPARK_JARS[@]}"; do
        local jar_url="${OSS_DOWNLOAD_URL}/${jar}"
        local jar_path="${TMP_SPARK_MIN_JARS}/${jar}"
        download_file "$jar_url" "$jar_path" "Spark JAR: $jar"
    done
}

# 下载 Flink JAR 依赖
download_flink_jars() {
    create_dir "$TMP_FLINK_MIN_LIB"

    for jar in "${FLINK_JARS[@]}"; do
        local jar_url="${OSS_DOWNLOAD_URL}/${jar}"
        local jar_path="${TMP_FLINK_MIN_LIB}/${jar}"
        download_file "$jar_url" "$jar_path" "Flink JAR: $jar"
    done
}

# 下载数据库驱动
download_jdbc_drivers() {
    create_dir "$TMP_JDBC_DIR"

    for driver in "${JDBC_DRIVERS[@]}"; do
        local driver_url="${OSS_DOWNLOAD_URL}/${driver}"
        local driver_path="${TMP_JDBC_DIR}/${driver}"
        download_file "$driver_url" "$driver_path" "JDBC driver: $driver"
    done
}

# 下载项目依赖
download_project_libs() {
    create_dir "$TMP_LIBS_DIR"

    for jar in "${PROJECT_LIBS[@]}"; do
        local jar_url="${OSS_DOWNLOAD_URL}/${jar}"
        local jar_path="${TMP_LIBS_DIR}/${jar}"
        download_file "$jar_url" "$jar_path" "Project dependency: $jar"
    done
}

# =============================================================================
# 安装函数
# =============================================================================

# 安装spark
install_spark() {
    # 创建必要目录
    create_dir "$SPARK_MIN_DIR"

    # 解压 Spark（如果尚未解压）
    if [[ ! -f "${SPARK_MIN_DIR}/README.md" ]]; then
        echo "Installing Spark ${SPARK_VERSION}..."
        local spark_path="${TMP_DIR}/${SPARK_MIN_FILE}"
        tar zxf "$spark_path" --strip-components=1 -C "$SPARK_MIN_DIR"

        # 删除不需要的文件和目录
        rm -rf "${SPARK_MIN_DIR}"/{data,examples,licenses,R,LICENSE,NOTICE,RELEASE}

        # 修改spark的默认配置文件
        cp "${SPARK_MIN_DIR}/conf/spark-env.sh.template" "${SPARK_MIN_DIR}/conf/spark-env.sh"
        echo "export SPARK_MASTER_PORT=7077" >> "${SPARK_MIN_DIR}/conf/spark-env.sh"
        echo "export SPARK_MASTER_HOST=0.0.0.0" >> "${SPARK_MIN_DIR}/conf/spark-env.sh"
        echo "export SPARK_MASTER_WEBUI_PORT=8081" >> "${SPARK_MIN_DIR}/conf/spark-env.sh"
        echo "export SPARK_WORKER_HOST=0.0.0.0" >> "${SPARK_MIN_DIR}/conf/spark-env.sh"
        echo "export SPARK_WORKER_WEBUI_PORT=8082" >> "${SPARK_MIN_DIR}/conf/spark-env.sh"
        echo "export SPARK_WORKER_CORES=32" >> "${SPARK_MIN_DIR}/conf/spark-env.sh"
        echo "export SPARK_WORKER_MEMORY=64g " >> "${SPARK_MIN_DIR}/conf/spark-env.sh"
        cp "${SPARK_MIN_DIR}/conf/spark-defaults.conf.template" "${SPARK_MIN_DIR}/conf/spark-defaults.conf"
        echo "export SPARK_WORKER_OPTS=\"-Dspark.worker.cleanup.enabled=true -Dspark.worker.cleanup.appDataTtl=1800 -Dspark.worker.cleanup.interval=60\"" >> "${SPARK_MIN_DIR}/conf/spark-defaults.conf"
        echo "spark.master          spark://0.0.0.0:7077" >> "${SPARK_MIN_DIR}/conf/spark-defaults.conf"
        echo "spark.master.web.url  http://0.0.0.0:8081" >> "${SPARK_MIN_DIR}/conf/spark-defaults.conf"
    fi
}

# 安装flink
install_flink() {
    # 创建必要目录
    create_dir "$FLINK_MIN_DIR"

    # 解压 Flink（如果尚未解压）
    if [[ ! -f "${FLINK_MIN_DIR}/bin/flink" ]]; then
        echo "Installing Flink ${FLINK_VERSION}..."
        local flink_path="${TMP_DIR}/${FLINK_MIN_FILE}"
        tar zxf "$flink_path" --strip-components=1 -C "$FLINK_MIN_DIR"

        # awk 兼容多种系统
        awk '
          /rest.bind-address: localhost/ { sub(/localhost/, "0.0.0.0") }
          /taskmanager.numberOfTaskSlots: 1/ { sub(/1/, "10") }
          { print }
        ' "${FLINK_MIN_DIR}/conf/flink-conf.yaml" > "${FLINK_MIN_DIR}/conf/flink-conf.yaml.tmp"
        mv "${FLINK_MIN_DIR}/conf/flink-conf.yaml.tmp" "${FLINK_MIN_DIR}/conf/flink-conf.yaml"
        echo "rest.port: 8083" >> "${FLINK_MIN_DIR}/conf/flink-conf.yaml"

        # 删除不需要的文件和目录
        rm -rf "${FLINK_MIN_DIR}"/{NOTICE,LICENSE,licenses,examples}
    fi
}

# 拷贝 Spark JAR 依赖
install_spark_jars() {
    for jar in "${TMP_SPARK_MIN_JARS}"/*.jar; do
        [[ -f "$jar" ]] || continue
        copy_file_if_missing "$jar" "${SPARK_MIN_DIR}/jars" "Spark JAR: $(basename "$jar")"
    done
}

# 拷贝 Flink Lib 依赖
install_flink_libs() {
    for jar in "${TMP_FLINK_MIN_LIB}"/*.jar; do
        [[ -f "$jar" ]] || continue
        copy_file_if_missing "$jar" "${FLINK_MIN_DIR}/lib" "Flink JAR: $(basename "$jar")"
    done
}

# 拷贝项目依赖
install_resources_libs() {
    create_dir "$LIBS_DIR"

    for lib in "${TMP_LIBS_DIR}"/*; do
        [[ -f "$lib" ]] || continue
        copy_file_if_missing "$lib" "$LIBS_DIR" "project dependency: $(basename "$lib")"
    done

    for lib in "${LIBS_DIR}"/libprql_*; do
        [[ -f "$lib" ]] || continue
        copy_file_if_missing "$lib" "$RESOURCE_DIR" "project native library: $(basename "$lib")"
    done
}

# 拷贝驱动
install_jdbc() {
    create_dir "$JDBC_DIR"

    for driver in "${TMP_JDBC_DIR}"/*.jar; do
        [[ -f "$driver" ]] || continue
        copy_file_if_missing "$driver" "$JDBC_DIR" "JDBC driver: $(basename "$driver")"
    done
}

# =============================================================================
# 主要安装流程
# =============================================================================

main() {
    # 1. 检查系统依赖
    check_system_dependencies

    # 2. 下载Spark
    download_spark

    # 3. 下载Spark第三方依赖
    download_spark_jars

    # 4. 下载Flink
    download_flink

    # 5. 下载Flink第三方依赖
    download_flink_jars

    # 6. 下载数据库驱动
    download_jdbc_drivers

    # 7. 下载项目依赖
    download_project_libs

    # 8. 安装Spark
    install_spark

    # 9. 拷贝Spark第三方依赖
    install_spark_jars

    # 10. 安装Flink
    install_flink

    # 11. 拷贝Flink第三方依赖
    install_flink_libs

    # 12. 拷贝项目依赖
    install_resources_libs

    # 13. 拷贝数据库驱动
    install_jdbc
}

# =============================================================================
# 脚本入口
# =============================================================================

# 切换到脚本所在目录
cd "$BASE_PATH" || {
    echo "Cannot switch to project directory: $BASE_PATH" >&2
    exit 1
}

# 执行主函数
main
