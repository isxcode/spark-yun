#!/bin/bash

# =============================================================================
# 至轻云依赖安装脚本
# =============================================================================

set -e  # 遇到错误立即退出

# 配置项
readonly SPARK_VERSION="3.4.1"
readonly SPARK_MIN_FILE="spark-${SPARK_VERSION}-bin-hadoop3.tgz"
readonly FLINK_VERSION="1.18.1"
readonly FLINK_MIN_FILE="flink-${FLINK_VERSION}-bin-scala_2.12.tgz"

# 路径配置
readonly BASE_PATH=$(cd "$(dirname "$0")" && pwd)
readonly TMP_DIR="${BASE_PATH}/resources/tmp"
readonly SPARK_MIN_DIR="${BASE_PATH}/spark-yun-dist/spark-min"
readonly FLINK_MIN_DIR="${BASE_PATH}/spark-yun-dist/flink-min"

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

# 检查文件是否存在
check_file_exists() {
    local file=$1
    local description=$2

    if [[ ! -f "$file" ]]; then
        echo "错误: $description 不存在: $file" >&2
        echo "请先运行 download.sh 下载所有依赖" >&2
        exit 1
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
    if ! command -v "pnpm" &>/dev/null; then
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

    create_dir "$SPARK_MIN_DIR"

    local spark_path="${TMP_DIR}/${SPARK_MIN_FILE}"
    check_file_exists "$spark_path" "Spark 压缩包"

    # 解压 Spark（如果尚未解压）
    if [[ ! -f "${SPARK_MIN_DIR}/README.md" ]]; then
        echo "解压 Spark 并清理不需要的文件..."
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
        echo "export SPARK_WORKER_OPTS=\"-Dspark.worker.cleanup.enabled=true -Dspark.worker.cleanup.appDataTtl=1800 -Dspark.worker.cleanup.interval=60\"" >> "${SPARK_MIN_DIR}/conf/spark-defaults.conf"

        cp "${SPARK_MIN_DIR}/conf/spark-defaults.conf.template" "${SPARK_MIN_DIR}/conf/spark-defaults.conf"
        echo "spark.master          spark://0.0.0.0:7077" >> "${SPARK_MIN_DIR}/conf/spark-defaults.conf"
        echo "spark.master.web.url  http://0.0.0.0:8081" >> "${SPARK_MIN_DIR}/conf/spark-defaults.conf"

        echo "Spark 解压和清理完成"
    else
        echo "Spark 已解压，跳过"
    fi
}

# 安装flink
install_flink() {
    echo "安装 Flink ${FLINK_VERSION}..."

    create_dir "$FLINK_MIN_DIR"

    local flink_path="${TMP_DIR}/${FLINK_MIN_FILE}"
    check_file_exists "$flink_path" "Flink 压缩包"

    # 解压 Flink（如果尚未解压）
    if [[ ! -f "${FLINK_MIN_DIR}/README.md" ]]; then
        echo "解压 Flink 并清理不需要的文件..."
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
        echo "Flink 解压和清理完成"
    else
        echo "Flink 已解压，跳过"
    fi
}

# 安装 Spark JAR 依赖
install_spark_jars() {
    echo "安装 Spark JAR 依赖..."

    local spark_jar_dir="${SPARK_MIN_DIR}/jars"
    local tmp_jar_dir="${TMP_DIR}/spark-jars"

    for jar in "${SPARK_JARS[@]}"; do
        local tmp_jar_path="${tmp_jar_dir}/${jar}"
        local target_jar_path="${spark_jar_dir}/${jar}"

        check_file_exists "$tmp_jar_path" "Spark JAR: $jar"

        if [[ ! -f "$target_jar_path" ]]; then
            cp "$tmp_jar_path" "$target_jar_path"
            echo "安装 Spark JAR: $jar"
        else
            echo "Spark JAR 已存在: $jar"
        fi
    done
}

# 安装 Flink JAR 依赖
install_flink_jars() {
    echo "安装 Flink JAR 依赖..."

    local flink_jar_dir="${FLINK_MIN_DIR}/lib"
    local tmp_jar_dir="${TMP_DIR}/flink-jars"

    for jar in "${FLINK_JARS[@]}"; do
        local tmp_jar_path="${tmp_jar_dir}/${jar}"
        local target_jar_path="${flink_jar_dir}/${jar}"

        check_file_exists "$tmp_jar_path" "Flink JAR: $jar"

        if [[ ! -f "$target_jar_path" ]]; then
            cp "$tmp_jar_path" "$target_jar_path"
            echo "安装 Flink JAR: $jar"
        else
            echo "Flink JAR 已存在: $jar"
        fi
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

    # 4. 安装Flink
    install_flink

    # 5. 安装Flink第三方依赖
    install_flink_jars

    echo "项目依赖安装完成！"
    echo "Spark 安装目录: $SPARK_MIN_DIR"
    echo "Flink 安装目录: $FLINK_MIN_DIR"
}

# =============================================================================
# 脚本入口
# =============================================================================

cd "$BASE_PATH" || {
    echo "无法切换到项目目录: $BASE_PATH" >&2
    exit 1
}

main