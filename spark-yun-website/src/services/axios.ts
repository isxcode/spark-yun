import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'

class Axios {
  private readonly instance: AxiosInstance

  constructor (config?: AxiosRequestConfig) {
    this.instance = axios.create(config)
    this.instance.interceptors.response.use(this.handleResponse, this.handleError)
  }

  private handleResponse (response: AxiosResponse) {
    return response.data
  }

  private handleError (error: any) {
    console.error('API request error:', error)
    throw error
  }

  public async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.instance.get(url, config)
    return response.data
  }

  public async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.instance.post(url, data, config)
    return response.data
  }

  public async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.instance.put(url, data, config)
    return response.data
  }

  public async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.instance.delete(url, config)
    return response.data
  }
}

export default axios
