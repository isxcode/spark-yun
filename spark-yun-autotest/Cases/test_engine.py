import re

import allure
import pytest
import yaml
from playwright.sync_api import sync_playwright, expect

from Pages.EnginePage import EnginePage

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
  engine = EnginePage(page)
  engine.goto_homepage(test_data['url'])
  engine.login(test_data['test_user'], test_data['test_passwd'])
  engine.goto_computer_group(test_data['url'])
  yield engine
  engine.logout()

@allure.feature("测试计算引擎部分")
class TestEnginePage:
  @allure.title("测试添加计算引擎弹窗正常弹出")
  def test_show_popup(self, setup):
    self.p = setup
    expect(self.p.locator_add_engine_button()).to_be_attached()
    self.p.locator_add_engine_button().click()
    self.p.wait(1)
    expect(self.p.locator_add_engine_popup()).to_be_visible()
    self.p.click_cancel()



  @allure.title("添加计算引擎成功")
  @pytest.mark.parametrize('name,types,remark', [('测试计算引擎1', 'yarn', '测试计算引擎1备注')])
  def test_add_engine_success(self, setup, name, types, remark):
    self.p = setup
    self.p.wait(2)
    self.p.locator_add_engine_button().click()
    self.p.locator_popup_input_name().fill(name)
    self.p.choose_type(types)
    self.p.input_remark(remark)
    self.p.click_confirm()
    self.p.wait(1)
    expect(self.p.locator_added_engine_name()).to_contain_text(name)
    expect(self.p.locator_added_engine_type()).to_contain_text(types)
    expect(self.p.locator_added_engine_remark()).to_contain_text(remark)

  @allure.title("进入计算引擎详情页并配置节点")
  @pytest.mark.parametrize('nodename',['测试计算引擎节点'])
  def test_conf_engine_success(self, setup,nodename):
    self.p = setup
    self.p.locator_added_engine_name().click()
    self.p.wait(2)
    expect(self.p.page).to_have_url(re.compile(".*/home/computer-pointer"))
    self.p.locator_add_node_button().click()
    self.p.locator_add_node_name().fill(nodename)
    self.p.locator_add_node_host().fill(test_data['engine_host'])
    self.p.locator_add_node_username().fill(test_data['engine_username'])
    self.p.choose_add_node_type(types=test_data['engine_type'],password=test_data['engine_password'])
    self.p.locator_add_node_remark().fill('自动化测试')
    self.p.click_add_node_confirm()
    self.p.wait(1)
    expect(self.p.locator_added_node_name()).to_contain_text(nodename)
    expect(self.p.locator_added_node_host()).to_contain_text(str(test_data['engine_host']))
    expect(self.p.locator_added_node_status()).to_contain_text('未安装')

  @allure.title("测试节点检测功能")
  def test_conf_engine_status(self,setup):
    self.p = setup
    self.p.locator_added_engine_name().click()
    self.p.wait(2)
    expect(self.p.page).to_have_url(re.compile(".*/home/computer-pointer"))
    self.p.locator_added_node_operation().click()
    self.p.locator_added_node_detection().click()
    self.p.wait(2)
    expect(self.p.locator_added_node_status()).to_contain_text('启动')
    self.p.goto_computer_group(test_data['url'])
    self.p.wait(1)
    self.p.locator_added_engine_detection().click()
    self.p.wait(3)
    expect(self.p.locator_added_engine_status()).to_contain_text('可用')





