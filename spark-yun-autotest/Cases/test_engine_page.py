import re

import allure
import pytest
import yaml
from playwright.sync_api import sync_playwright, expect

from Pages.engine.engine_page import EnginePage



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
def setup(init_browser_and_context):
    page = init_browser_and_context.new_page()
    engine = EnginePage(page)
    engine.goto_page('login')
    engine.login(engine.conf['test_user'], engine.conf['test_password'])
    engine.wait(1)
    engine.goto_page('engine')
    yield engine
    engine.logout()
    page.close()

@allure.feature("测试计算引擎部分")
class TestEnginePage:
  @allure.title("测试添加计算引擎弹窗正常弹出")
  def test_show_popup(self, setup):
    EnginePage = setup
    EnginePage.engine_add('button').click()
    expect(EnginePage.engine_add('标题')).to_be_visible()
    EnginePage.engine_add('取消').click()



  @allure.title("添加计算引擎成功")
  @pytest.mark.parametrize('name,types,remark', [('测试计算引擎2', 'Yarn', '测试计算引擎1备注')])
  def test_add_engine_success(self, setup, name, types, remark):
    EnginePage = setup
    EnginePage.engine_add('button').click()
    EnginePage.engine_add('集群名称').fill(name)
    EnginePage.engine_add('类型').click()
    EnginePage.engine_add('下拉框选项',option_name=types).click()
    EnginePage.engine_add('备注').fill(remark)
    EnginePage.engine_add('确定').click()
    expect(EnginePage.engine_form('集群名称')).to_contain_text(name)
    expect(EnginePage.engine_form('类型')).to_contain_text('yarn')
    expect(EnginePage.engine_form('备注')).to_contain_text(remark)
    expect(EnginePage.engine_form('状态')).to_have_text('待配置')


  @allure.title("检测计算引擎配置状态")
  def test_engine_status_detection(self, setup):
    EnginePage = setup
    EnginePage.engine_form('检测').click()
    expect(EnginePage.toast()).to_have_text('检测成功')
    expect(EnginePage.engine_form('状态')).to_have_text('不可用')
  @allure.title("删除已添加的计算引擎")
  def test_delete_engine(self, setup):
    EnginePage = setup
    EnginePage.engine_form('删除').click()
    EnginePage.engine_add('确定').click()







