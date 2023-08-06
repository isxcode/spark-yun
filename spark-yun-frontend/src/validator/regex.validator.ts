/*
 * @Author: fanciNate
 * @Date: 2023-04-29 17:46:43
 * @LastEditTime: 2023-05-03 21:14:27
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/validator/regex.validator.ts
 */
import { isString } from '@/utils/checkType'

/**
 * 正则表达式校验
 * @param {*} regex 正则或正则字符串
 * @param {*} errorMsg 错误提示
 */
const RegexValidator = (regex: RegExp, errorMsg: string) => {
  let reg: RegExp
  if (isString(regex)) {
    reg = new RegExp(regex)
  } else {
    reg = regex
  }
  return (rule: any, value: any, callback: any) => {
    if (!reg.test(value)) {
      callback(new Error(errorMsg))
    } else {
      callback()
    }
  }
}

export default RegexValidator
