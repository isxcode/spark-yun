from Pages.LoginPage import LoginPage


class EnginePage(LoginPage):
  def __init__(self, page):
    super().__init__(page)

  # 页面元素操作
  def locator_add_engine_button(self):
    return self.page.locator('xpath=/html/body/div[1]/div/div[2]/div[2]/div[1]/button')

  def locator_add_engine_popup(self):
    return self.page.locator('xpath=/html/body/div[3]/div/div/header/span')

  def locator_added_engine_name(self):
    return self.page.locator(
      'xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[2]/div/span')

  def locator_added_engine_type(self):
    return self.page.locator(
      'xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[3]/div/span')
  def locator_added_engine_status(self):
    return self.page.locator(
      'xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[7]')

  def locator_added_engine_remark(self):
    return self.page.locator(
      'xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[9]/div/span')

  def locator_added_engine_detection(self):
    return self.page.locator('xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[10]/div/div/span[2]')

  # 弹窗元素操作
  def locator_popup_input_name(self):
    return self.page.locator('xpath=/html/body/div[3]/div/div/div/div/form/div[1]/div/div[1]/div/input')

  def input_remark(self, strs):
    self.page.locator('xpath=/html/body/div[3]/div/div/div/div/form/div[3]/div/div/textarea').fill(strs)

  def choose_type(self, strs):
    self.page.locator('xpath=/html/body/div[3]/div/div/div/div/form/div[2]/div/div/div/div/div/input').click()
    if strs == 'kubernetes':
      self.page.locator('xpath=/html/body/div[2]/div[4]/div/div/div[1]/ul/li[1]').click()
    if strs == 'yarn':
      self.page.locator('xpath=/html/body/div[2]/div[4]/div/div/div[1]/ul/li[2]').click()
    if strs == 'standalone':
      self.page.locator('xpath=/html/body/div[2]/div[4]/div/div/div[1]/ul/li[3]').click()

  def click_confirm(self):
    self.page.locator('xpath=/html/body/div[3]/div/div/footer/button[2]').click()

  def click_cancel(self):
    self.page.locator('xpath=/html/body/div[3]/div/div/footer/button[1]').click()


  # 计算引擎详细页
  def locator_add_node_button(self):
    return self.page.locator('xpath=/html/body/div[1]/div/div[2]/div[2]/div[1]/button')

  def locator_add_node_name(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[1]/div/div[1]/div/input')

  def locator_add_node_host(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[2]/div/div[1]/div/input')

  def locator_add_node_username(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[3]/div/div/div/input')

  def choose_add_node_type(self, types: str, password: str = None):
    if types == '密码':
      self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[4]/div/div/label[1]/span[1]/span').click()
      if password is not None:
        self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[5]/div/div/div/input').fill(
          password)
    if types == '令牌':
      self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[4]/div/div/label[2]/span[1]/span').click()
      if password is not None:
        self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[5]/div/div/textarea').fill(password)

  def locator_add_node_remark(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[6]/div/div/textarea')

  def click_add_node_confirm(self):
    self.page.locator('xpath=/html/body/div[4]/div/div/footer/button[2]').click()
  def click_add_node_cancel(self):
    self.page.locator('xpath=/html/body/div[4]/div/div/footer/button[1]').click()
  def locator_added_node_name(self):
    return self.page.locator('xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[2]/div/span')
  def locator_added_node_host(self):
    return self.page.locator('xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[3]/div/span')
  def locator_added_node_status(self):
    return self.page.locator('xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[7]/div/div/span')
  def locator_added_node_operation(self):
    return self.page.locator('xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[10]/div/div/div/span')
  def locator_added_node_detection(self):
    return self.page.locator('xpath=/html/body/div[2]/div[4]/div/div[1]/div/ul/li[2]')
