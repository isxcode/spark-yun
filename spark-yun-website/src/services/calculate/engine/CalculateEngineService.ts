import { type QueryEngineReq } from '../../../types/calculate/engine/req/QueryEngineReq'
import { type QueryDatasourceRes } from '../../../types/datasource/res/QueryDatasourceRes'
import axiosInstance from '../../../config/axios'
import { type AddEngineReq } from '../../../types/calculate/engine/req/AddEngineReq'
import { type UpdateEngineReq } from '../../../types/calculate/engine/req/UpdateEngineReq'
import { message } from 'antd'
import { BaseResponse } from '../../../types/base/BaseResponse'

export const addEngineApi = async (data: AddEngineReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/cae/addEngine', data)
  message.success(response.msg)
}

export const updateEngineApi = async (data: UpdateEngineReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/cae/updateEngine', data)
  message.success(response.msg)
}

export const queryEnginesApi = async (data: QueryEngineReq): Promise<QueryDatasourceRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<QueryDatasourceRes>('/cae/queryEngine', data)
  return response.data
}

export const delEngineApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/cae/delEngine?engineId=' + data)
  message.success(response.msg)
}

export const checkEngineApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/cae/checkEngine?engineId=' + data)
  message.success(response.msg)
}
