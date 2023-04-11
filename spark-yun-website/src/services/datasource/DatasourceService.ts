import axiosInstance from '../../config/axios'
import { type QueryDatasourceReq } from '../../types/datasource/req/QueryDatasourceReq'
import { type QueryDatasourceRes } from '../../types/datasource/res/QueryDatasourceRes'
import { type AddDatasourceReq } from '../../types/datasource/req/AddDatasourceReq'
import { TestDatasourceReq } from '../../types/datasource/req/TestDatasourceReq'
import { TestDatasourceRes } from '../../types/datasource/res/TestDatasourceRes'
import { UpdateWorkReq } from '../../types/woks/req/UpdateWorkReq'
import { UpdateDatasourceReq } from '../../types/datasource/req/UpdateDatasourceReq'

export const addDatasourceApi = async (data: AddDatasourceReq): Promise<void> => {
  await axiosInstance.post('/das/addDatasource', data)
}

export const queryDatasourceApi = async (data: QueryDatasourceReq): Promise<QueryDatasourceRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<QueryDatasourceRes>('/das/queryDatasource', data)
  return response.data
}

export const delDatasourceApi = async (data: string | undefined): Promise<void> => {
  await axiosInstance.get('/das/delDatasource?datasourceId=' + data)
}

export const updateDatasourceApi = async (data: UpdateDatasourceReq): Promise<void> => {
  await axiosInstance.post('/das/updateDatasource', data)
}

export const testDatasourceApi = async (data: string): Promise<TestDatasourceRes> => {
  const testDatasourceReq: TestDatasourceReq = {
    datasourceId: data
  }
  const response = await axiosInstance.post<TestDatasourceRes>('/das/testConnect', testDatasourceReq)
  return response.data
}
