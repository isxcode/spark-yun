/*
 * @Author: fanciNate
 * @Date: 2023-04-29 17:51:18
 * @LastEditTime: 2023-04-29 17:51:19
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/validator/port.validator.ts
 */
import RegexValidator from './regex.validator'

const PortValidator = function(errorMsg: string) {
  return RegexValidator(/^([1-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5][0-5][0-3][0-5])$/, errorMsg)
}

export default PortValidator
