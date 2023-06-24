/*
 * @Author: fanciNate
 * @Date: 2023-04-29 17:51:10
 * @LastEditTime: 2023-04-29 21:05:12
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/validator/host.validator.ts
 */
import RegexValidator from './regex.validator'

const HostValidator = function(errorMsg: string) {
  // return RegexValidator(/^(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5])$/, errorMsg)
  return RegexValidator(/^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/, errorMsg)
}

export default HostValidator
