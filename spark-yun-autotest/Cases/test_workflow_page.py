import re

import allure
import pytest
import yaml
from playwright.sync_api import sync_playwright, expect

from Pages.workflow.workflow_page import WorkflowPage



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
    workflow = WorkflowPage(page)
    workflow.goto_page('login')
    workflow.login(workflow.conf['test_user'], workflow.conf['test_password'])
    workflow.wait(1)
    workflow.goto_page('workflow')
    yield workflow
    workflow.logout()
    page.close()

@allure.feature("测试作业流部分")
class TestWorkflowPage:
  @allure.title("测试添加作业流弹窗正常弹出")
  def test_show_popup(self,setup):
    workflow = setup
    workflow.workflow_add('button').click()
    expect(workflow.workflow_add('标题')).to_be_visible()
    workflow.workflow_add('取消').click()

  @allure.title("测试添加作业流成功")
  @pytest.mark.parametrize('name,remark', [('测试作业流1', '测试作业流备注')])
  def test_add_workflow(self,setup,name,remark):
    workflow = setup
    workflow.workflow_add('button').click()
    workflow.workflow_add('名称').fill(name)
    workflow.workflow_add('备注').fill(remark)
    workflow.workflow_add('确定').click()
    expect(workflow.workflow_form('作业流名称')).to_contain_text(name)
    expect(workflow.workflow_form('状态')).to_contain_text('未运行')
    expect(workflow.workflow_form('备注')).to_contain_text(remark)


  @allure.title("测试删除作业流成功")
  @pytest.mark.parametrize('name,remark', [('测试作业流1', '测试作业流备注')])
  def test_delete_workflow(self,setup,name,remark):
    workflow = setup
    workflow.workflow_form('删除').click()
    workflow.workflow_add('确定').click()
    expect(workflow.toast()).to_be_visible()

  @allure.title("测试进入作业流成功")
  def test_into_workflow(self, setup):
    workflow = setup
    workflow.workflow_form("作业流标题").click()
    expect(workflow.page).to_have_url(re.compile(r'.*\/home\/workflow-page'))