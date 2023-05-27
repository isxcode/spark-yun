import { type LoginReq } from '../../types/login/req/LoginReq'
import { type LoginRes } from '../../types/login/res/LoginRes'
import axiosInstance from '../../config/axios'
import { message } from 'antd'

export const loginApi = async (data: LoginReq): Promise<LoginRes> => {
  const response = await axiosInstance.post<LoginRes>('/usr/open/login', data)
  message.success(JSON.parse(JSON.stringify(response)).msg)
  return response.data
}
