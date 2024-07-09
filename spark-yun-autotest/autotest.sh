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

# 测试所有
for case_file in "${CASE_FILES[@]}"; do
  execute_tests "$case_file"
done