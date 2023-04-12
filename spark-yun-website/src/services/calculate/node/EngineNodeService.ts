import axiosInstance from '../../../config/axios'
import { type UpdateEngineReq } from '../../../types/calculate/engine/req/UpdateEngineReq'
import { QueryEngineNodeReq } from '../../../types/calculate/node/req/QueryEngineNodeReq'
import { QueryEngineNodeRes } from '../../../types/calculate/node/res/QueryEngineNodeRes'
import { AddEngineNodeReq } from '../../../types/calculate/node/req/AddEngineNodeReq'
import { UpdateEngineNodeReq } from '../../../types/calculate/node/req/UpdateEngineNodeReq'

export const addEngineNodeApi = async (data: AddEngineNodeReq): Promise<void> => {
  await axiosInstance.post('/eno/addNode', data)
}
export const updateEngineNodeApi = async (data: UpdateEngineNodeReq): Promise<void> => {
  await axiosInstance.post('/eno/updateNode', data)
}

export const queryEngineNodesApi = async (data: QueryEngineNodeReq): Promise<QueryEngineNodeRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<QueryEngineNodeRes>('/eno/queryNode', data)
  return response.data
}

export const delEngineNodeApi = async (data: string | undefined): Promise<void> => {
  await axiosInstance.get('/eno/delNode?engineNodeId=' + data)
}

export const checkAgentApi = async (data: string | undefined): Promise<void> => {
  await axiosInstance.get('/eno/checkAgent?engineNodeId=' + data)
}

export const installAgentApi = async (data: string | undefined): Promise<void> => {
  await axiosInstance.get('/eno/installAgent?engineNodeId=' + data, { timeout: 120000 })
}

export const removeAgentApi = async (data: string | undefined): Promise<void> => {
  await axiosInstance.get('/eno/removeAgent?engineNodeId=' + data)
}
