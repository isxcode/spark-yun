import re

from Pages.login.login_page import LoginPage
class EnginePage(LoginPage):
  def __init__(self, page):
    super().__init__(page)

  def engine_add(self, element_type,option_name:str=None):
    if element_type == 'button':
      return self.page.get_by_role("button", name="添加集群")
    elif element_type=='标题':
      return self.page.get_by_role("heading", name="添加集群")
    elif element_type=='集群名称':
      return self.page.get_by_label("集群名称")
    elif element_type=='类型':
      return self.page.get_by_label("类型")
    elif element_type=='备注':
      return self.page.get_by_label("备注")
    elif element_type=='确定':
      return self.page.get_by_role("button", name="确定")
    elif element_type=='取消':
      return self.page.get_by_role("button", name="取消")
    elif element_type=="下拉框选项":
      return self.page.locator("li").filter(has_text=f'{option_name}')
  def engine_form(self, element_type: str, index: int = 1):
    """
    用于查询计算引擎相关的信息
    :param element_type: 输入需要查询的计算引擎的信息
    :param index: 需要返回的计算引擎的顺序
    :return: 返回对应元素的locator
    """
    if element_type == '编辑':
      return self.page.locator(f"tr:nth-child({index}) > td:nth-child(10) > .vxe-cell > .btn-group > span").nth(0)
    elif element_type == '检测':
      return self.page.locator(f"tr:nth-child({index}) > td:nth-child(10) > .vxe-cell > .btn-group > span").nth(1)
    elif element_type == '删除':
      return self.page.locator(f"tr:nth-child({index}) > td:nth-child(10) > .vxe-cell > .btn-group > span").nth(2)
    else:
      element_index = {'集群名称': 11 * index - 2, '类型': 11 * index - 1, '节点': 11 * index,
                       '内存': 11 * index + 1, '存储': 11 * index + 2,
                       '状态': 11 * index + 3, '检测日期': 11 * index + 5,
                       '备注': 11 * index + 6}
      return self.page.get_by_role("cell").locator("div").nth(element_index[element_type])
  def toast(self):
    return self.page.locator('.el-message.el-message--success')