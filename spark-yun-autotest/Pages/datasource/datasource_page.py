from Pages.login.login_page import LoginPage


class DatasourcePage(LoginPage):
  def __init__(self, page):
    super().__init__(page)

  def datasource_form(self, element_type: str, index: int = 1):
    element_index = {
      '数据源名称': 9 * index + 1, '类型': 9 * index + 2, '连接信息': 9 * index + 3,
      '用户名': 9 * index + 4, '状态': 9 * index + 5, '检测时间': 9 * index + 6,
      '备注': 9 * index + 7
    }
    if element_type in ["日志", "编辑", "检测", "删除"]:
      return self.page.get_by_role("cell", name="日志 编辑 检测 删除").get_by_text(f"{element_type}").nth(index - 1)
    elif element_type in element_index.keys():
      return self.page.get_by_role("cell").nth(element_index[element_type])
    else:
      raise ValueError('无效的元素')

  def datasource_add(self, element_type: str, option_name: str = None, option_index: int = None):
    if element_type == "button":
      return self.page.get_by_role("button", name="添加数据源")
    elif element_type == "标题":
      return self.page.get_by_role("heading", name="添加数据源")
    elif element_type in ["名称", "类型", "数据源驱动", "连接信息", "用户名", "密码", "备注"]:
      return self.page.get_by_label(f"{element_type}")
    elif element_type == "下拉框选项":
      if option_index:
        return self.page.get_by_role("list").locator("li").nth(option_index)
      if option_name:
        return self.page.get_by_role("list").locator("li", has_text=f'{option_name}')
    elif element_type in ["确定", "取消"]:
      return self.page.get_by_role("button", name=f'{element_type}')
    else:
      raise ValueError('无效的元素')
