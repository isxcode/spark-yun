import re
from datetime import date

import allure
import pytest
from playwright.sync_api import sync_playwright, expect

from Pages.datasource.datasource_page import DatasourcePage

today=date.today().strftime("%m%d")
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
    datasource = DatasourcePage(page)
    datasource.goto_page('login')
    datasource.login(datasource.conf['test_user'], datasource.conf['test_password'])
    datasource.wait(1)
    datasource.goto_page('datasource')
    yield datasource
    datasource.logout()
    page.close()

@allure.suite("数据源模块")
class TestDatasourcePage:

    @allure.title('测试打开添加数据源弹窗成功')
    def test_datasource_page_popup(self, setup):
        datasource_page = setup
        datasource_page.datasource_add("button").click()
        expect(datasource_page.datasource_add('标题')).to_be_visible()
        datasource_page.wait(1)
        datasource_page.datasource_add("取消").click()

    @allure.title('测试打开添加数据源成功')
    @pytest.mark.parametrize('db_type',['Clickhouse'])
    def test_added_datasource(self, setup,db_type):
      datasource_page = setup

      datasource_page.datasource_add("button").click()
      datasource_page.datasource_add("名称").fill(db_type+today)
      datasource_page.datasource_add("类型").click()
      datasource_page.datasource_add("下拉框选项",option_name='Clickhouse').click()
      datasource_page.datasource_add("数据源驱动").click()
      datasource_page.datasource_add("下拉框选项",option_index=1).click()
      datasource_page.datasource_add("连接信息").fill(datasource_page.conf[db_type]['jdbc'])
      datasource_page.datasource_add("用户名").fill(datasource_page.conf[db_type]['username'])
      datasource_page.datasource_add("密码").fill(datasource_page.conf[db_type]['password'])
      datasource_page.datasource_add('确定').click()

      expect(datasource_page.datasource_form("数据源名称")).to_have_text(db_type+today)
      expect(datasource_page.datasource_form("类型")).to_have_text('CLICKHOUSE')
      expect(datasource_page.datasource_form("状态")).to_have_text('待检测')

    @pytest.mark.parametrize('db_type', ['Clickhouse'])
    def test_datasource_status_detection(self, db_type, setup):
      datasource_page =setup
      datasource_page.datasource_form('检测').click()
      datasource_page.wait(1)
      expect(datasource_page.datasource_form('状态')).to_have_text('可用')

    @pytest.mark.parametrize('db_type', ['Clickhouse'])
    def test_datasource_delete(self, db_type, setup):
      datasource_page = setup
      datasource_page.datasource_form('删除').click()
      datasource_page.datasource_add('确定').click()
      expect(datasource_page.datasource_form('数据源名称')).not_to_have_text(db_type+today)




