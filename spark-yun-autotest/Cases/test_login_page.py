import re

import allure
import pytest
from Pages.login.login_page import LoginPage
from playwright.sync_api import sync_playwright, expect


@pytest.fixture(scope='class')
def init_browser_and_context():
  with sync_playwright() as playwright:
    browser = playwright.chromium.launch(headless=False, slow_mo=50)
    context = browser.new_context()
    blank_page = context.new_page()
    yield context
    blank_page.close()
    context.close()
    browser.close()


@pytest.fixture(scope='function')
def setup(init_browser_and_context, request):
  page = init_browser_and_context.new_page()
  login = LoginPage(page)
  login.goto_page('login')
  login.wait(2)

  test_name = request.node.name

  if test_name == 'test_login_wrong':
    teardown = False
  else:
    teardown = True

  yield login, teardown
  if teardown:
    login.logout()
    page.close()


@allure.suite("登录模块")
class TestLoginPage:
  @allure.title("登录管理员界面成功")
  def test_login_admin(self, setup):
    login_page, teardown = setup
    login_page.login('admin', 'admin123')
    expect(login_page.page).to_have_url(re.compile(".*/home/user-center"))

  @allure.title("登录用户成功")
  def test_login_user(self, setup):
    login_page, teardown = setup
    login_page.login(login_page.conf['test_user'], login_page.conf['test_password'])
    expect(login_page.page).to_have_url(re.compile(".*/home/index"))

  @allure.title("账号或密码不正确提示")
  def test_login_wrong(self, setup):
    login_page, teardown = setup
    login_page.login(login_page.conf['test_user'], '1')
    expect(login_page.wrong_toast()).to_be_visible()
