"""
Created on 2024/3/4 01:16
@author: Sailboats
@description:
"""
import os
import time

import yaml

current_dir = os.path.dirname(os.path.abspath(__file__))

# 构造 conf.yaml 文件的相对路径
conf_file_path = os.path.join(current_dir, '..', '..', 'conf.yaml')

class BasePage(object):
  # 获取当前脚本所在目录

  def __init__(self, page):
    self.page = page
    self.page_url = {
      'login': '/auth',
      'home': '/home/index',
      'engine': '/home/computer-group',
      'engine-detail': '/home/computer-pointer',
      'datasource': '/home/datasource',
      'workflow': '/home/workflow',
      'driver': '/home/driver-management',
      'schedule': '/home/schedule',
      'form-list': '/home/custom-form/form-list',
      'api': '/home/custom-api',
      'tenant-user': '/home/tenant-user'

    }
    with open(conf_file_path, 'r') as file:
      self.conf = yaml.safe_load(file)
      self.url = self.conf['url']

  def wait(self, wait_time_by_second: float):
    self.page.wait_for_timeout(wait_time_by_second * 1000)

  def screenshots(self, full_page: bool = False, screenshot_dir: str = None,
                  screenshot_name: str = None):
    """
    截图功能
    :param full_page: 是否为全页面
    :param screenshot_dir: 截图所在的位置，如果不手动指定则为用例上层目录中Screenshots文件夹中
    :param screenshot_name:截图名称，如果不指定则为当前时间戳
    :return:tuple(path,name)
    """
    screenshot_dir = screenshot_dir if screenshot_dir is not None else os.path.dirname(os.getcwd())
    screenshot_name = screenshot_name if screenshot_name is not None else str(
      time.strftime('%Y-%m-%d_%H:%M:%S', time.localtime(time.time())))
    screenshot_path = os.path.join(screenshot_dir, 'Screenshots', screenshot_name + '.jpg')
    self.page.screenshot(full_page=full_page, path=screenshot_path)
    return screenshot_path, screenshot_name

  def goto_page(self, page_name):
    if page_name in self.page_url:
      self.page.goto(self.url + self.page_url[page_name])
    else:
      raise ValueError(f"Invalid page name: {page_name}")
