from playwright.sync_api import Locator

from Pages.LoginPage import LoginPage


class AdminPage(LoginPage):
  def __init__(self, page):
    super().__init__(page)

  # 新建按钮及弹窗元素
  def locator_add_user_button(self):
    return self.page.locator('xpath=/html/body/div[1]/div/div[2]/div[2]/div[1]/button/span')

  def locator_add_user_popup(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/header/span')

  def locator_add_user_name(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[1]/div/div[1]/div/input')
  def locator_add_user_account(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[2]/div/div[1]/div/input')

  def locator_add_user_password(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[3]/div/div[1]/div/input')

  def locator_add_user_phone(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[4]/div/div/div/input')

  def locator_add_user_email(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[5]/div/div/div/input')

  def locator_add_user_remark(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[6]/div/div/textarea')

  def locator_add_user_confirm(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/footer/button[2]')

  def locator_add_user_cancel(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/footer/button[1]')

  #编辑弹窗元素
  def locator_added_user_name(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[1]/div/div/div/input')
  def locator_added_user_account(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[2]/div/div/div/input')
  def locator_added_user_phone(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[3]/div/div/div/input')
  def locator_added_user_email(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[4]/div/div/div/input')
  def locator_added_user_remark(self):
    return self.page.locator('xpath=/html/body/div[4]/div/div/div/div/form/div[5]/div/div/textarea')

  # 用户中心详情页元素
  def locator_user_name(self):
    return self.page.locator(
      'xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[2]')

  def locator_user_account(self):
    return self.page.locator(
      'xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[3]')

  def locator_user_phone(self):
    return self.page.locator(
      'xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[4]')

  def locator_user_email(self):
    return self.page.locator(
      'xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[5]')

  def locator_user_status(self):
    return self.page.locator(
      'xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[6]')

  def locator_user_remark(self):
    return self.page.locator(
      'xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[7]')

  def locator_user_edit(self):
    return self.page.locator(
      'xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[8]/div/div/span[1]')

  def locator_user_isdisable(self):
    return self.page.locator(
      'xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[8]/div/div/span[2]')

  def locator_user_delete(self):
    self.page.locator(
      'xpath=/html/body/div[1]/div/div[2]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/table/tbody/tr[1]/td[8]/div/div/span[3]').click()
    return self.page.locator('xpath=/html/body/div[5]/div/div/div[3]/button[2]/span')

  # 异常提示
  def locator_user_duplication_toast(self):
    return self.page.locator('xpath=/html/body/div[5]')
