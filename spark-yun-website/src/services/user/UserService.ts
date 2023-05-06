import axiosInstance from '../../config/axios'
import { message } from 'antd'
import { BaseResponse } from '../../types/base/BaseResponse'
import { QueryAllUsersRes } from '../../types/user/res/QueryAllUsersRes'
import { QueryAllUserReq } from '../../types/user/req/QueryAllUserReq'
import { UpdateUserReq } from '../../types/user/req/UpdateUserReq'
import { AddUserReq } from '../../types/user/req/AddUserReq'

const headerConfig = {
  headers: {
    Authorization: localStorage.getItem('Token'),
    Tenant: localStorage.getItem('Tenant')
  }
}

export const queryAllUsersApi = async (data: QueryAllUserReq): Promise<QueryAllUsersRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<QueryAllUsersRes>('/usr/queryAllUsers', data, headerConfig)
  return response.data
}

export const queryAllEnableUsersApi = async (data: QueryAllUserReq): Promise<QueryAllUsersRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<QueryAllUsersRes>('/usr/queryAllEnableUsers', data, headerConfig)
  return response.data
}

export const enableUserApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/usr/enableUser?userId=' + data, headerConfig)
  message.success(response.msg)
}

export const disableUserApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/usr/disableUser?userId=' + data, headerConfig)
  message.success(response.msg)
}

export const delUserApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/usr/deleteUser?userId=' + data, headerConfig)
  message.success(response.msg)
}

export const updateUserApi = async (data: UpdateUserReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/usr/updateUser', data, headerConfig)
  message.success(response.msg)
}

export const addUserApi = async (data: AddUserReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/usr/addUser', data, headerConfig)
  message.success(response.msg)
}
