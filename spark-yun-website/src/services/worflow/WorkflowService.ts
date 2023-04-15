import { type WofQueryWorkflowReq } from '../../types/workflow/req/WofQueryWorkflowReq'
import axiosInstance from '../../config/axios'
import { type AddWorkflowReq } from '../../types/workflow/req/AddWorkflowReq'
import { type UpdateWorkflowReq } from '../../types/workflow/req/UpdateWorkflowReq'
import { QueryEngineRes } from '../../types/calculate/engine/res/QueryEngineRes'
import { message } from 'antd'
import { BaseResponse } from '../../types/base/BaseResponse'

export const queryWorkflowApi = async (data: WofQueryWorkflowReq): Promise<QueryEngineRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<QueryEngineRes>('/wof/queryWorkflow', data)
  return response.data
}

export const delWorkflowApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/wof/delWorkflow?workflowId=' + data)
  message.success(response.msg)
}

export const addWorkflowApi = async (data: AddWorkflowReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/wof/addWorkflow', data)
  message.success(response.msg)
}

export const updateWorkflowApi = async (data: UpdateWorkflowReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/wof/updateWorkflow', data)
  message.success(response.msg)
}
