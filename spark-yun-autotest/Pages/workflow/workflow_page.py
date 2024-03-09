from Pages.login.login_page import LoginPage


class WorkflowPage(LoginPage):
  def __init__(self, page):
    super().__init__(page)

  def workflow_form(self, element_type: str, index: int = 1):
    element_index = {
      '作业流名称': 5 * index + 1, '发布状态': 5 * index + 2, '备注': 5 * index + 3
    }
    if element_type in [ "编辑", "下线", "删除"]:
      return self.page.get_by_role("cell", name="编辑 删除 下线").get_by_text(f"{element_type}").nth(index - 1)
    elif element_type in element_index.keys():
      return self.page.get_by_role("cell").nth(element_index[element_type])
    elif element_type in "作业流标题":
      return self.page.get_by_role("cell").nth(element_index['作业流名称']).locator('span.name-click')
    else:
      raise ValueError('无效的元素')

  def workflow_add(self,element_type: str):
    if element_type == "button":
      return self.page.get_by_role("button", name="添加作业流")
    elif element_type == "标题":
      return self.page.get_by_role("heading", name="添加作业流")
    elif element_type in ["名称", "备注"]:
      return self.page.get_by_label(f"{element_type}")
    elif element_type in ["确定", "取消"]:
      return self.page.get_by_role("button", name=f'{element_type}')
    else:
      raise ValueError('无效的元素')

  def toast(self):
    return self.page.locator('.el-message.el-message--success')