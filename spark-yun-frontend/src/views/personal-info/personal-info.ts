export interface PersonalModel {
  username: string
  account: string
  phone: string
  email: string
  remark: string
}

export interface PasswordModel {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}
