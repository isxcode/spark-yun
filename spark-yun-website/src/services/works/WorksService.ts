import { type WofQueryWorkflowRes } from '../../types/workflow/res/WofQueryWorkflowRes'
import axiosInstance from '../../config/axios'
import { type QueryWorkReq } from '../../types/woks/req/QueryWorkReq'
import { type QueryWorkRes } from '../../types/woks/res/QueryWorkRes'
import { type AddWorkReq } from '../../types/woks/req/AddWorkReq'
import { UpdateWorkReq } from '../../types/woks/req/UpdateWorkReq'

export const queryWorkApi = async (data: QueryWorkReq): Promise<QueryWorkRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<WofQueryWorkflowRes>('/wok/queryWork', data)
  return response.data
}

export const delWorkApi = async (data: string | undefined): Promise<void> => {
  await axiosInstance.get<WofQueryWorkflowRes>('/wok/delWork?workId=' + data)
}

export const addWorkApi = async (data: AddWorkReq): Promise<void> => {
  await axiosInstance.post('/wok/addWork', data)
}

export const updateWorkApi = async (data: UpdateWorkReq): Promise<void> => {
  await axiosInstance.post('/wok/updateWork', data)
}
