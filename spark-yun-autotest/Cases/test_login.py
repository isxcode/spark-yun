import re

import allure
import pytest
import yaml
from playwright.sync_api import sync_playwright, expect

from Pages.LoginPage import LoginPage

with open('../test_data.yaml', 'r') as file:
  test_data = yaml.safe_load(file)


@pytest.fixture(scope='class')
def browser_init():
  with sync_playwright() as playwright:
    browser = playwright.chromium.launch(headless=False, slow_mo=50)
    context = browser.new_context()
    yield context
    context.close()
    browser.close()



@pytest.fixture(scope='function')
def setup(browser_init):
  page = browser_init.new_page()
  login = LoginPage(page)
  login.goto_homepage(test_data['url'])
  login.wait(1)
  yield login


@allure.feature('测试登录部分')
class TestLoginPage:
  @allure.title("测试没有账户密码直接登录提示")
  def test_login_without_content(self, setup):
    self.p = setup
    self.p.wait(3)
    self.p.click_login_button()
    expect(self.p.locator_user_filled_prompt()).to_be_visible()
    expect(self.p.locator_password_filled_prompt()).to_be_visible()

  @allure.title("测试成功登录普通用户")
  @pytest.mark.parametrize('username,password', [(test_data['test_user'], test_data['test_passwd'])])
  def test_login_user_success(self, setup, username, password):
    self.p = setup
    self.p.input_user(username)
    self.p.input_password(password)
    self.p.click_login_button()
    self.p.wait(1)
    expect(self.p.page).to_have_url(re.compile(".*/home/computer-group"))
    self.p.logout()

  @allure.title("测试错误密码登录账户")
  @pytest.mark.parametrize('username,password', [(test_data['test_user'], test_data['admin_passwd'])])
  def test_login_wrong_password(self, setup, username, password):
    self.p = setup
    self.p.input_user(username)
    self.p.input_password(password)
    self.p.wait(3)
    self.p.click_login_button()
    expect(self.p.locator_login_toast()).to_contain_text("账号或者密码不正确")

  @allure.title("测试错误用户名登录账户")
  def test_login_wrong_username(self, setup):
    self.p = setup
    self.p.input_user('toast_test_aaaa')
    self.p.input_password('toast_test_bbb')
    self.p.wait(3)
    self.p.click_login_button()
    expect(self.p.locator_login_toast()).to_contain_text("账号或者密码不正确")  # print the context of toast /n print(locator.inner_text())
