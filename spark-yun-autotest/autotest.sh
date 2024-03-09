#!/bin/bash

# 配置
CASES_DIR="Cases"
LOGIN_CASE_FILE="test_login_page.py"
ENGINE_CASE_FILE="test_engine_page.py"
DATASOURCE_CASE_FILE="test_datasource_page.py"
WORKFLOW_CASE_FILE='test_workflow_page.py'
CASE_FILES=("$LOGIN_CASE_FILE" "$ENGINE_CASE_FILE" "$DATASOURCE_CASE_FILE" "$WORKFLOW_CASE_FILE")
REPORTS_DIR="./Reports"
REQUIREMENTS_FILE="requirements.txt"

# 函数: 执行测试用例并生成报告
execute_tests() {
    local target="$1"
    local case_file="$CASES_DIR/$target"

    if [ -e "$case_file" ]; then
        pytest "$case_file" -s -q --alluredir="$REPORTS_DIR"
        show_report
    else
        log_error "目标文件或目录 '$target' 不存在"
    fi
}

# 函数: 显示测试报告
show_report() {
    read -r -p "是否运行测试报告? (Y/n) " answer
    answer=${answer:-y}  # 设置默认值为 "y"

    if [[ "$answer" =~ ^[Yy]?$ ]]; then
        echo "运行测试报告中... 按 Ctrl+C 退出"
        allure serve "$REPORTS_DIR"
    else
        echo "已退出"
    fi
}

# 函数: 执行初始化操作
initialize() {
    echo "执行初始化操作..."
    pip install -r "$REQUIREMENTS_FILE"
    playwright install
}

# 函数: 记录错误日志
log_error() {
    local message="$1"
    echo "错误: $message" >&2
}

# 验证命令行参数
if [ "$#" -ne 1 ]; then
    echo "用法: $0 <选项>"
    echo "选项:"
    echo "  init        - 执行初始化操作"
    echo "  all         - 运行所有测试用例"
    echo "  login       - 运行登录界面测试用例"
    echo "  engine      - 运行计算引擎测试用例"
    echo "  datasource  - 运行数据源测试用例"
    echo "  workflow    - 运行作业流测试用例"
    echo "  report      - 运行测试报告"
    exit 1
fi

# 根据命令行参数执行相应操作
case "$1" in
    init)
        initialize
        ;;
    all)
        for case_file in "${CASE_FILES[@]}"; do
            execute_tests "$case_file"
        done
        ;;
    login)
        execute_tests "${LOGIN_CASE_FILE}"
        ;;
    engine)
        execute_tests "${ENGINE_CASE_FILE}"
        ;;
    datasource)
        execute_tests "${DATASOURCE_CASE_FILE}"
        ;;
    workflow)
        execute_tests "${WORKFLOW_CASE_FILE}"
        ;;
    report)
        show_report
        ;;
    *)
        log_error "无效的选项: $1"
        exit 1
        ;;
esac