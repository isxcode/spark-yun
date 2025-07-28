#!/bin/bash

# =============================================================================
# 至轻云依赖安装脚本
# =============================================================================

set -e  # 遇到错误立即退出

# 配置项
readonly SPARK_VERSION="3.4.1"
readonly SPARK_MIN_FILE="spark-${SPARK_VERSION}-bin-hadoop3.tgz"
readonly OSS_DOWNLOAD_URL="https://isxcode.oss-cn-shanghai.aliyuncs.com/zhiqingyun/install"

# 路径配置
readonly BASE_PATH=$(cd "$(dirname "$0")" && pwd)
readonly TMP_DIR="${BASE_PATH}/resources/tmp"
readonly JDBC_DIR="${BASE_PATH}/resources/jdbc/system"
readonly LIBS_DIR="${BASE_PATH}/resources/libs"
readonly RESOURCE_DIR="${BASE_PATH}/spark-yun-backend/spark-yun-main/src/main/resources"
readonly SPARK_MIN_DIR="${BASE_PATH}/spark-yun-dist/spark-min"

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
)

# 项目依赖列表
readonly PROJECT_JARS=(
    "prql-java-0.5.2.jar"
)

# Resources文件列表
readonly RESOURCE_FILE=(
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
# 安装函数
# =============================================================================

# 检查系统依赖
check_system_dependencies() {
    echo "检查系统依赖..."

    check_command "tar" "请安装 tar"
    check_command "java" "请安装 Java"
    check_command "node" "请安装 Node.js"

    # 检查并安装 pnpm
    if ! command -v pnpm &>/dev/null; then
        echo "未检测到 pnpm，正在安装..."
        npm install pnpm@9.0.6 -g
        echo "pnpm 安装完成"
    else
        echo "pnpm 命令检查通过"
    fi
}

# 安装spark
install_spark() {
    echo "安装 Spark ${SPARK_VERSION}..."

    # 创建必要目录
    create_dir "$TMP_DIR"
    create_dir "$SPARK_MIN_DIR"

    # 下载 Spark
    local spark_url="${OSS_DOWNLOAD_URL}/${SPARK_MIN_FILE}"
    local spark_path="${TMP_DIR}/${SPARK_MIN_FILE}"
    download_file "$spark_url" "$spark_path" "Spark ${SPARK_VERSION} 二进制文件，请耐心等待"

    # 解压 Spark（如果尚未解压）
    if [[ ! -f "${SPARK_MIN_DIR}/README.md" ]]; then
        echo "解压 Spark 并清理不需要的文件..."
        tar zxf "$spark_path" --strip-components=1 -C "$SPARK_MIN_DIR"

        # 删除不需要的文件和目录
        rm -rf "${SPARK_MIN_DIR}"/{data,examples,licenses,R,LICENSE,NOTICE,RELEASE}
        echo "Spark 解压和清理完成"
    else
        echo "Spark 已解压，跳过"
    fi
}

# 安装 Spark JAR 依赖
install_spark_jars() {
    echo "安装 Spark JAR 依赖..."

    local spark_jar_dir="${SPARK_MIN_DIR}/jars"

    # 批量下载 JAR 文件
    for jar in "${SPARK_JARS[@]}"; do
        local jar_url="${OSS_DOWNLOAD_URL}/${jar}"
        local jar_path="${spark_jar_dir}/${jar}"
        download_file "$jar_url" "$jar_path" "Spark JAR: $jar"
    done
}

# 安装数据库驱动
install_jdbc_drivers() {
    echo "安装数据库驱动..."

    # 创建 JDBC 驱动目录
    create_dir "$JDBC_DIR"

    # 批量下载驱动文件
    for driver in "${JDBC_DRIVERS[@]}"; do
        local driver_url="${OSS_DOWNLOAD_URL}/${driver}"
        local driver_path="${JDBC_DIR}/${driver}"
        download_file "$driver_url" "$driver_path" "数据库驱动: $driver"
    done
}

# 安装项目依赖
install_project_dependencies() {
    echo "安装项目依赖..."

    # 创建项目依赖目录
    create_dir "$LIBS_DIR"

    # 下载项目 JAR 依赖
    for jar in "${PROJECT_JARS[@]}"; do
        local jar_url="${OSS_DOWNLOAD_URL}/${jar}"
        local jar_path="${LIBS_DIR}/${jar}"
        download_file "$jar_url" "$jar_path" "项目依赖: $jar"
    done
}

# 安装resources依赖
install_resource_dependencies() {
    echo "安装resource依赖..."

    for file in "${RESOURCE_FILE[@]}"; do
        local file_url="${OSS_DOWNLOAD_URL}/${file}"
        local file_path="${RESOURCE_DIR}/${file}"
        download_file "$file_url" "$file_path" "项目依赖: $file"
    done
}

# =============================================================================
# 主要安装流程
# =============================================================================

main() {
    echo "开始安装至轻云项目依赖..."

    # 1. 检查系统依赖
    check_system_dependencies

    # 2. 安装Spark
    install_spark

    # 3. 安装Spark第三方依赖
    install_spark_jars

    # 4. 安装数据库驱动
    install_jdbc_drivers

    # 5. 安装项目依赖
    install_project_dependencies

    # 6. 安装resource依赖
    install_resource_dependencies

    echo "项目依赖安装完成！"
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