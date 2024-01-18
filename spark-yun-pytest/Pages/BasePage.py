import os
import time
import re



class BasePage:

  def __init__(self, page):
    self.page = page

  def wait(self, wait_time_by_second: float):
    self.page.wait_for_timeout(wait_time_by_second * 1000)

  def screenshots(self, full_page: bool = False, screenshot_dir: str = None,
                  screenshot_name: str = None):
    """
    :param full_page: 是否截取全部页面
    :param screenshot_dir: 文件存储的路径，默认路径为用例执行目录上层的Screenshots中
    :param screenshot_name: 截图名称，默认为截图当前时间
    :return: None
    """
    if screenshot_dir is None:
      screenshot_dir = os.path.dirname(os.getcwd())
    if screenshot_name is None:
      timestamp = str(time.strftime('%Y-%m-%d_%H:%M:%S', time.localtime(time.time())))
      screenshot_name = timestamp + ".jpg"
    screenshot_path = os.path.join(screenshot_dir,'Screenshots', screenshot_name)
    print(screenshot_path)
    self.page.screenshot(full_page=full_page, path=screenshot_path)

  def goto_homepage(self, url:str):
    self.page.goto(url)

  def goto_computer_group(self, url:str):
    self.page.goto(url + "/home/computer-group")

  def goto_datasource(self, url:str):
    self.page.goto(url + "/home/datasource")

  def goto_workflow(self, url:str):
    self.page.goto(url + "/home/workflow")

  def goto_driver_management(self, url:str):
    self.page.goto(url + "/home/driver-management")

  def goto_schedule(self, url:str):
    self.page.goto(url + "/home/scheduler")

  def goto_tenant_user(self, url:str):
    self.page.goto(url + "/home/tenant-user")

  def login(self, username: str, password: str):
    self.page.get_by_placeholder('账号/邮箱/手机号').fill(username)
    self.page.get_by_placeholder('密码').fill(password)
    self.page.get_by_role("button").click()

  def logout(self):
    self.page.click('.el-avatar')
    self.page.click('.zqy-home__menu-option:has-text("退出登录")')