import axios from 'axios'
import { type LoginReq } from '../../types/login/req/LoginReq'
import { type LoginRes } from '../../types/login/res/LoginRes'
import axiosInstance from '../../config/axios'

export const loginApi = async (data: LoginReq): Promise<LoginRes> => {
  const response = await axiosInstance.post<LoginRes>('/usr/login', data)
  return response.data
}
