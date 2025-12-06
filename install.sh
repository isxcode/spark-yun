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
readonly TMP_SPARK_MIN_JARS="${TMP_DIR}/spark-min/jars"
readonly TMP_FLINK_MIN_LIB="${TMP_DIR}/flink-min/lib"
readonly LIBS_DIR="${BASE_PATH}/resources/libs"
readonly RESOURCE_DIR="${BASE_PATH}/spark-yun-backend/spark-yun-main/src/main/resources"

# =============================================================================
# 工具函数
# =============================================================================

# 创建目录
create_dir() {
    local dir=$1
    if [[ ! -d "$dir" ]]; then
        mkdir -p "$dir"
        echo "创建目录: $dir"
    fi
}

# =============================================================================
# 安装函数
# =============================================================================

# 安装spark
install_spark() {
    echo "安装 Spark ${SPARK_VERSION}..."

    # 创建必要目录
    create_dir "$SPARK_MIN_DIR"

    # 解压 Spark（如果尚未解压）
    if [[ ! -f "${SPARK_MIN_DIR}/README.md" ]]; then
        echo "解压 Spark 并清理不需要的文件..."
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

    # 创建必要目录
    create_dir "$FLINK_MIN_DIR"

    # 解压 Flink（如果尚未解压）
    if [[ ! -f "${FLINK_MIN_DIR}/README.md" ]]; then
        echo "解压 Flink 并清理不需要的文件..."
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
        echo "Flink 解压和清理完成"
    else
        echo "Flink 已解压，跳过"
    fi
}

# 拷贝 Spark JAR 依赖
copy_spark_jars() {
    echo "拷贝 Spark JAR 依赖..."

    cp "${TMP_SPARK_MIN_JARS}"/*.jar "${SPARK_MIN_DIR}"/jars/
}

# 拷贝 Flink Lib 依赖
copy_flink_libs() {
    echo "拷贝 Flink JAR 依赖..."

    cp "${TMP_FLINK_MIN_LIB}"/*.jar "${FLINK_MIN_DIR}"/lib/
}

# 拷贝项目依赖
copy_resources_libs() {
    echo "拷贝项目依赖..."

    cp "${LIBS_DIR}"/libprql_java-osx-arm64.dylib "${RESOURCE_DIR}"/
    cp "${LIBS_DIR}"/libprql_java-linux64.so "${RESOURCE_DIR}"/
}

# =============================================================================
# 主要安装流程
# =============================================================================

main() {
    echo "开始安装至轻云项目依赖..."

    # 1. 安装Spark
    install_spark

    # 2. 拷贝Spark第三方依赖
    copy_spark_jars

    # 3. 安装Fink
    install_flink

    # 4. 拷贝Flink第三方依赖
    copy_flink_libs

    # 5. 拷贝项目依赖
    copy_resources_libs

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