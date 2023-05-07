import axiosInstance from '../../../config/axios'
import { QueryEngineNodeReq } from '../../../types/calculate/node/req/QueryEngineNodeReq'
import { QueryEngineNodeRes } from '../../../types/calculate/node/res/QueryEngineNodeRes'
import { AddEngineNodeReq } from '../../../types/calculate/node/req/AddEngineNodeReq'
import { UpdateEngineNodeReq } from '../../../types/calculate/node/req/UpdateEngineNodeReq'
import { message } from 'antd'
import { BaseResponse } from '../../../types/base/BaseResponse'

const headerConfig = {
  timeout: 600000,
  headers: {
    Authorization: localStorage.getItem('Token'),
    Tenant: localStorage.getItem('Tenant')
  }
}

export const addEngineNodeApi = async (data: AddEngineNodeReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/eno/addNode', data, headerConfig);
  message.success(response.msg)
}
export const updateEngineNodeApi = async (data: UpdateEngineNodeReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/eno/updateNode', data, headerConfig)
  message.success(response.msg)
}

export const queryEngineNodesApi = async (data: QueryEngineNodeReq): Promise<QueryEngineNodeRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<QueryEngineNodeRes>('/eno/queryNode', data, headerConfig)
  return response.data
}

export const delEngineNodeApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/eno/delNode?engineNodeId=' + data, headerConfig)
  message.success(response.msg)
}

export const checkAgentApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/eno/checkAgent?engineNodeId=' + data, headerConfig)
  message.success(response.msg)
}

export const installAgentApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/eno/installAgent?engineNodeId=' + data, headerConfig)
  message.success(response.msg)
}

export const removeAgentApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/eno/removeAgent?engineNodeId=' + data, headerConfig);
  message.success(response.msg)
}
