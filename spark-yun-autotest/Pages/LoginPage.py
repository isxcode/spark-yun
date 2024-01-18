from playwright.sync_api import Locator

from Pages.BasePage import BasePage


class LoginPage(BasePage):
  def __init__(self, page):
    super().__init__(page)

  def input_user(self, user: str):
    self.page.get_by_placeholder('账号/邮箱/手机号').fill(user)

  def input_password(self, password: str):
    self.page.get_by_placeholder('密码').fill(password)

  def click_login_button(self):
    self.page.get_by_role("button").click()



  def locator_user_filled_prompt(self) -> Locator:
    return self.page.locator('xpath=//*[@id="app"]/div/div[2]/div[2]/div/form/div[1]/div/div[2]',
                             has_text='请输入账号')


  def locator_password_filled_prompt(self) -> Locator:
    return self.page.locator('xpath=//*[@id="app"]/div/div[2]/div[2]/div/form/div[2]/div/div[2]',
                             has_text='请输入密码')


  def locator_login_toast(self) -> Locator:
    return self.page.locator('xpath=/html/body/div[4]')

