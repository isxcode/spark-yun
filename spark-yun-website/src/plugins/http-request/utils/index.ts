import { VAxios } from './Axios'
import { AxiosTransform } from './axiosTransform'
import axios, { AxiosResponse } from 'axios'
import { showMessage, checkStatus } from './handler'
import { joinTimestamp, formatRequestDate } from './helper'
import { RequestEnum, ResultEnum, ContentTypeEnum } from './httpEnum'
import { isString, isUrl, setObjToUrlParams, merge } from '@/utils/checkType'
import { RequestOptions, Result, CreateAxiosOptions } from './types'

// 数据处理，方便区分多种处理方式
const transform: AxiosTransform = {
  // 处理请求数据
  transformRequestData: (res: AxiosResponse<Result>, options: RequestOptions) => {
    const {
      isReturnNativeResponse, isTransformResponse, isShowMessage, isShowSuccessMessage, successMessageText, showSuccessMessage, isShowErrorMessage, errorMessageText, showErrorMessage 
    } = options

    // 是否返回原生响应头，比如：需要获取响应头时使用该属性
    if (isReturnNativeResponse) {
      return res
    }

    // 不进行任何处理，直接返回
    if (!isTransformResponse) {
      return res.data
    }

    if (!res.data) {
      throw new Error('请求出错，请稍候重试')
    }

    // 统一处理后台返回的内容
    const {
      code, msg 
    } = res.data

    // 接口请求成功
    if (code == ResultEnum.SUCCESS || code == ResultEnum.SUCCESS2) {
      // 提示成功信息
      const successMsg = successMessageText || msg || '操作成功！'
      if (isShowMessage && isShowSuccessMessage) {
        showSuccessMessage(successMsg)
      }

      // 返回接口响应数据
      return res.data
    }
    // 接口请求错误
    else {
      // 提示错误信息
      const errorMsg = msg || errorMessageText || '操作失败！'
      if (isShowMessage && isShowErrorMessage) {
        showErrorMessage(errorMsg)
      }

      // 抛出错误，确保在实际调用后走的是catch
      throw new Error(errorMsg)
    }
  },

  // 请求之前处理config
  beforeRequestHook: (config, options) => {
    const {
      apiUrl, joinPrefix, joinParamsToUrl, formatDate, joinTime = true, urlPrefix 
    } = options
    const isUrlStr = isUrl(config.url as string)

    if (!isUrlStr && joinPrefix) {
      config.url = `${urlPrefix}${config.url}`
    }
    if (!isUrlStr && apiUrl && isString(apiUrl)) {
      config.url = `${apiUrl}${config.url}`
    }

    // GET请求
    if (config.method?.toUpperCase() === RequestEnum.GET) {
      const params = config.params || config.data || {
      }

      if (!isString(params)) {
        // 给get请求加上时间戳参数，避免从缓存中拿数据
        config.params = Object.assign(params, joinTimestamp(joinTime, false))
      } else {
        // 兼容restful风格
        config.url = config.url + params + `${joinTimestamp(joinTime, true)}`
        config.params = undefined
      }
    }
    // POST请求
    else {
      const params = config.params || {
      }
      const data = config.data || false

      if (!isString(params)) {
        formatDate && formatRequestDate(params)
        if (Reflect.has(config, 'data') && config.data) {
          config.data = data
          config.params = params
        } else {
          config.data = params
          config.params = undefined
        }
        if (joinParamsToUrl) {
          config.url = setObjToUrlParams(config.url as string, Object.assign({
          }, config.params, config.data))
        }
      } else {
        // 兼容restful风格
        config.url = config.url + params
        config.params = undefined
      }
    }

    return config
  },

  // 请求拦截器处理
  requestInterceptors: (config) => {
    return config
  },

  // 响应拦截器处理
  responseInterceptors: (config: any) => {
    return config
  },

  // 响应错误处理
  responseInterceptorsCatch: (error: any, options: RequestOptions) => {
    const {
      code, message, response 
    } = error || {
    }
    const {
      showErrorMessage, checkStatus 
    } = options

    try {
      if (code === 'ECONNABORTED' && message.indexOf('timeout') !== -1) {
        showErrorMessage('接口请求超时，请刷新页面重试!')
        return
      }
      if (error && error.toString().includes('Network Error')) {
        showErrorMessage('网络异常')
        return Promise.reject(error)
      }
    } catch (error) {
      throw new Error(error as any)
    }
    // 请求是否被取消
    const isCancel = axios.isCancel(error)
    if (!isCancel && response) {
      const status = response.status
      const msg = response.data && response.data.message ? response.data.message : message
      checkStatus(status, msg, showErrorMessage)
    } else {
      console.warn(error, '请求被取消！')
    }

    // return Promise.reject(error);
    return Promise.reject(response?.data)
  }
}

const originOptions = {
  // 接口前缀
  prefixUrl: '',
  // 请求头
  headers: {
    'Content-Type': ContentTypeEnum.JSON
  },
  // 当前请求为跨域类型时是否提供凭据信息
  withCredentials: false,
  // 超时时长
  timeout: 10 * 1000,
  // 数据处理方式
  transform: transform,
  // 请求的配置项，下面的选项都可以在独立的接口请求中覆盖
  requestOptions: {
    isReturnNativeResponse: false,
    isTransformResponse: true,
    joinParamsToUrl: false,
    formatDate: true,
    apiUrl: '',
    urlPrefix: '',
    joinPrefix: true,
    joinTime: true,
    ignoreCancelToken: true,
    withToken: true,
    isShowMessage: true,
    isShowSuccessMessage: false,
    successMessageText: '',
    showSuccessMessage: showMessage,
    isShowErrorMessage: true,
    errorMessageText: '',
    showErrorMessage: showMessage,
    checkStatus: checkStatus
  }
}
// 导出一个工厂函数，根据传入的配置，生成对应的请求实例
export function createAxios(opt?: Partial<CreateAxiosOptions>) {
  return new VAxios(
    merge(
      {
      },
      {
        ...originOptions
      },
      opt || {
      }
    )
  )
}

// 导出一个默认配置的请求实例
export const http = createAxios()
