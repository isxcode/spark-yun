import axiosInstance from '../../../config/axios'
import { QueryEngineNodeReq } from '../../../types/calculate/node/req/QueryEngineNodeReq'
import { QueryEngineNodeRes } from '../../../types/calculate/node/res/QueryEngineNodeRes'
import { AddEngineNodeReq } from '../../../types/calculate/node/req/AddEngineNodeReq'
import { UpdateEngineNodeReq } from '../../../types/calculate/node/req/UpdateEngineNodeReq'
import { message } from 'antd'
import { BaseResponse } from '../../../types/base/BaseResponse'

export const addEngineNodeApi = async (data: AddEngineNodeReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/eno/addNode', data)
  message.success(response.msg)
}
export const updateEngineNodeApi = async (data: UpdateEngineNodeReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/eno/updateNode', data)
  message.success(response.msg)
}

export const queryEngineNodesApi = async (data: QueryEngineNodeReq): Promise<QueryEngineNodeRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<QueryEngineNodeRes>('/eno/queryNode', data)
  return response.data
}

export const delEngineNodeApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/eno/delNode?engineNodeId=' + data)
  message.success(response.msg)
}

export const checkAgentApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/eno/checkAgent?engineNodeId=' + data, { timeout: 600000 })
  message.success(response.msg)
}

export const installAgentApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/eno/installAgent?engineNodeId=' + data, { timeout: 600000 })
  message.success(response.msg)
}

export const removeAgentApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/eno/removeAgent?engineNodeId=' + data)
  message.success(response.msg)
}
