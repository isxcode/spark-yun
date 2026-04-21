export interface PersonalModel {
  username: string
  phone: string
  email: string
  remark: string
}

export interface PasswordModel {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}
