import os
import platform
import subprocess


def requirement_init(systems, current_dir):
    if systems == 'Windows':
        commands = f'pip3 install -r {current_dir}/requirements.txt && playwright install'
    else:
        commands = f'pip3 install -r {current_dir}/requirements.txt; playwright install'
    return commands


def run_case(systems, current_dir, file_index):
    if systems == 'Windows':
        commands = f'cd {current_dir}/Cases && pytest {file_index} -s -q --alluredir={current_dir}/Reports --clean-alluredir'
    else:
        commands = f'cd {current_dir}/Cases; pytest {file_index} -s -q --alluredir={current_dir}/Reports --clean-alluredir'
    return commands


def run_allure(current_dir):
    allure_server_command = f'allure serve {current_dir}/Reports'
    process = subprocess.Popen(allure_server_command, shell=True)
    process.wait()


if __name__ == '__main__':
    fi = {1: '.', 2: 'test_login.py', 3: 'test_admin.py', 4: 'test_engine'}
    a = eval(input("选择要执行的用例：\n0.初始化requirements\n1.全部用例\n2.登录界面\n3.后台管理\n4.计算引擎\n->:"))
    current_dir = os.path.dirname(os.path.abspath(__file__))
    system = platform.system()
    if a == 0:
        command = requirement_init(system, current_dir)
    else:
        command = run_case(system, current_dir, fi[a])

    result = subprocess.run(command, shell=True, capture_output=True, text=True)
    # 检查命令是否执行成功
    if result.returncode == 0:
        output = result.stdout
        print(output)
        run_allure(current_dir)
    else:
        # 如果命令执行失败，获取错误信息
        error = result.stderr
        print(f'命令执行失败：{error}')