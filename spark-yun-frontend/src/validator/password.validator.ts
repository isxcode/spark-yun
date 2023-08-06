/*
 * @Author: fanciNate
 * @Date: 2023-04-29 17:46:03
 * @LastEditTime: 2023-04-29 17:50:43
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/validator/password.validator.ts
 */
import RegexValidator from './regex.validator'

const PasswordValidator = function(errorMsg: string) {
  return RegexValidator(/^(?![0-9]+$)(?![a-zA-Z]+$)(?![^a-zA-Z]+$).{8,}$/, errorMsg)
}

export default PasswordValidator
