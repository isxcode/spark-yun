import axios, { type AxiosResponse } from 'axios'
import { message } from 'antd'

const axiosInstance = axios.create({
  baseURL: process.env.API_PREFIX_URL,
  timeout: 5000,
  responseType: 'json',
  maxContentLength: 2000,
  maxBodyLength: 2000,
  maxRedirects: 1
})

axiosInstance.interceptors.request.use(
  function (config) {
    return config
  },
  async function (error) {
    return await Promise.reject(error)
  }
)

axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    if (response.status === 200) {
      if (response.data.code !== '200') {
        message.error(response.data.msg).then((r) => {})
      } else {
        return response.data
      }
    }
  },
  async (error: any) => {
    return await Promise.reject(error)
  }
)

export default axiosInstance
