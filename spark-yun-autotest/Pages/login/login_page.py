"""
Created on 2024/3/6 17:30
@author: Sailboats
@description:
"""
from playwright.sync_api import Locator

from Pages.base.base_page import BasePage


class LoginPage(BasePage):
  def __init__(self, page):
    super().__init__(page)

  def input_username(self, user: str):
    self.page.get_by_placeholder('账号/邮箱/手机号').fill(user)

  def input_password(self, password: str):
    self.page.get_by_placeholder('密码').fill(password)

  def click_login_button(self):
    self.page.get_by_role("button").click()

  def login(self,username,password):
    self.input_username(username)
    self.input_password(password)
    self.click_login_button()
  def logout(self):
    self.page.click('.el-avatar')
    self.page.click('.zqy-home__menu-option:has-text("退出登录")')

  def wrong_toast(self):
    return self.page.get_by_text("账号或者密码不正确")


