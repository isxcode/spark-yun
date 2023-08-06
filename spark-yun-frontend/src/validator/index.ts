/*
 * @Author: fanciNate
 * @Date: 2023-04-29 17:44:44
 * @LastEditTime: 2023-04-29 21:21:56
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/validator/index.ts
 */
import { isFunction } from '@/utils/checkType'
import HostValidator from './host.validator'
import PortValidator from './port.validator'

export const Validator = {
  HostValidator,
  PortValidator
}

export function _validate(type: any, message: string, trigger: string[]) {
  // 生成验证器
  const validator = {
    trigger: trigger,
    validator: null
  }
  if (isFunction(type)) {
    validator.validator = type
  }

  return validator
}
