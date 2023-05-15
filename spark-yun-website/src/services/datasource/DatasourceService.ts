import axiosInstance from '../../config/axios'
import { type QueryDatasourceReq } from '../../types/datasource/req/QueryDatasourceReq'
import { type QueryDatasourceRes } from '../../types/datasource/res/QueryDatasourceRes'
import { type AddDatasourceReq } from '../../types/datasource/req/AddDatasourceReq'
import { TestDatasourceReq } from '../../types/datasource/req/TestDatasourceReq'
import { TestDatasourceRes } from '../../types/datasource/res/TestDatasourceRes'
import { UpdateDatasourceReq } from '../../types/datasource/req/UpdateDatasourceReq'
import { message } from 'antd'
import { BaseResponse } from '../../types/base/BaseResponse'
import { GetConnectLogRes } from '../../types/datasource/res/GetConnectLogRes'

const headerConfig = {
  headers: {
    Tenant: localStorage.getItem('Tenant'),
    Authorization: localStorage.getItem('Token')
  }
}

export const addDatasourceApi = async (data: AddDatasourceReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/das/addDatasource', data, headerConfig)
  message.success(response.msg)
}

export const queryDatasourceApi = async (data: QueryDatasourceReq): Promise<QueryDatasourceRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<QueryDatasourceRes>('/das/queryDatasource', data, headerConfig)
  return response.data
}

export const delDatasourceApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/das/delDatasource?datasourceId=' + data, headerConfig)
  message.success(response.msg)
}

export const updateDatasourceApi = async (data: UpdateDatasourceReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/das/updateDatasource', data, headerConfig)
  message.success(response.msg)
}

export const testDatasourceApi = async (data: string): Promise<TestDatasourceRes> => {
  const response = await axiosInstance.get<TestDatasourceRes>('/das/testConnect?datasourceId=' + data, headerConfig)
  message.success(JSON.parse(JSON.stringify(response)).msg)
  return response.data
}

export const getConnectLogApi = async (data: string): Promise<GetConnectLogRes> => {
  const response = await axiosInstance.get<GetConnectLogRes>('/das/getConnectLog?datasourceId=' + data, headerConfig)
  message.success(JSON.parse(JSON.stringify(response)).msg)
  return response.data
}
