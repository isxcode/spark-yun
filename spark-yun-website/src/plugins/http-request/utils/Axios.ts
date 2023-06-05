import type { AxiosRequestConfig, AxiosInstance, AxiosResponse } from 'axios'
import axios from 'axios'
import { AxiosCanceler } from './axiosCancel'
import { isFunction, cloneDeep } from '@/utils/checkType'
import type { RequestOptions, CreateAxiosOptions, Result, UploadFileParams } from './types'
import { ContentTypeEnum } from './httpEnum'

// axios模块
export class VAxios {
  private axiosInstance: AxiosInstance
  private options: CreateAxiosOptions

  constructor(options: CreateAxiosOptions) {
    this.options = options
    this.axiosInstance = axios.create(options)
    this.setupInterceptors()
  }

  getAxios(): AxiosInstance {
    return this.axiosInstance
  }

  // 重新配置axios
  configAxios(config: CreateAxiosOptions) {
    if (!this.axiosInstance) {
      return
    }
    this.createAxios(config)
  }

  // 设置通用header
  setHeader(headers: any): void {
    if (!this.axiosInstance) {
      return
    }
    Object.assign(this.axiosInstance.defaults.headers, headers)
  }

  // 请求方法
  request<T = any>(config: AxiosRequestConfig, options?: RequestOptions): Promise<T> {
    let conf: AxiosRequestConfig = cloneDeep(config)
    const {
      transform, requestOptions 
    } = this.options
    const opt: RequestOptions = Object.assign({
    }, requestOptions, options)

    const {
      beforeRequestHook, requestCatch, transformRequestData 
    } = transform || {
    }
    if (beforeRequestHook && isFunction(beforeRequestHook)) {
      conf = beforeRequestHook(conf, opt)
    }

    // 这里重新赋值成最新的配置
    // @ts-ignore
    conf.requestOptions = opt

    return new Promise((resolve, reject) => {
      this.axiosInstance
        .request<any, AxiosResponse<Result>>(conf)
        .then((res: AxiosResponse<Result>) => {
          // 请求是否被取消
          const isCancel = axios.isCancel(res)
          if (transformRequestData && isFunction(transformRequestData) && !isCancel) {
            try {
              const ret = transformRequestData(res, opt)
              resolve(ret)
            } catch (err) {
              reject(err || new Error('request error!'))
            }
            return
          }
          resolve(res as unknown as Promise<T>)
        })
        .catch((e: Error) => {
          if (requestCatch && isFunction(requestCatch)) {
            reject(requestCatch(e))
            return
          }
          reject(e)
        })
    })
  }

  // 创建axios实例
  private createAxios(config: CreateAxiosOptions): void {
    this.axiosInstance = axios.create(config)
  }

  // 文件上传
  uploadFile<T = any>(config: AxiosRequestConfig, params?: any) {
    let conf: AxiosRequestConfig = cloneDeep(config)
    const {
      transform, requestOptions 
    } = this.options
    const opt: RequestOptions = Object.assign({
    }, requestOptions, {
    })
    const {
      beforeRequestHook, requestCatch, transformRequestData 
    } = transform || {
    }
    if (beforeRequestHook && isFunction(beforeRequestHook)) {
      conf = beforeRequestHook(conf, opt)
    }

    return this.axiosInstance.request<T>({
      method: 'POST',
      data: params,
      headers: {
        'Content-type': ContentTypeEnum.FORM_DATA,
        ignoreCancelToken: true
      },
      ...conf
    })
  }

  // 拦截器配置
  private setupInterceptors() {
    const {
      transform, requestOptions 
    } = this.options
    if (!transform || !requestOptions) {
      return
    }
    const {
      requestInterceptors, requestInterceptorsCatch, responseInterceptors, responseInterceptorsCatch 
    } = transform

    const axiosCanceler = new AxiosCanceler()

    // 请求拦截器配置处理
    this.axiosInstance.interceptors.request.use((config: any) => {
      const {
        headers: {
          ignoreCancelToken 
        }
      } = config as any
      const ignoreCancel = ignoreCancelToken !== undefined ? ignoreCancelToken : this.options.requestOptions?.ignoreCancelToken

      !ignoreCancel && axiosCanceler.addPending(config)
      if (requestInterceptors && isFunction(requestInterceptors)) {
        config = requestInterceptors(config, this.options)
      }
      return config
    }, undefined)

    // 请求拦截器错误捕获
    requestInterceptorsCatch &&
      isFunction(requestInterceptorsCatch) &&
      this.axiosInstance.interceptors.request.use(undefined, (error) => {
        return requestInterceptorsCatch(error, requestOptions)
      })

    // 响应结果拦截器处理
    this.axiosInstance.interceptors.response.use((res: AxiosResponse<any>) => {
      res && axiosCanceler.removePending(res.config)
      if (responseInterceptors && isFunction(responseInterceptors)) {
        res = responseInterceptors(res)
      }
      return res
    }, undefined)

    // 响应结果拦截器错误捕获
    responseInterceptorsCatch &&
      isFunction(responseInterceptorsCatch) &&
      this.axiosInstance.interceptors.response.use(undefined, (error) => {
        return responseInterceptorsCatch(error, requestOptions)
      })
  }
}
