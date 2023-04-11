import { type WofQueryWorkflowReq } from '../../types/workflow/req/WofQueryWorkflowReq'
import { type WofQueryWorkflowRes } from '../../types/workflow/res/WofQueryWorkflowRes'
import axiosInstance from '../../config/axios'
import { type AddWorkflowReq } from '../../types/workflow/req/AddWorkflowReq'
import { type UpdateWorkflowReq } from '../../types/workflow/req/UpdateWorkflowReq'

export const queryWorkflowApi = async (data: WofQueryWorkflowReq): Promise<WofQueryWorkflowRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<WofQueryWorkflowRes>('/wof/queryWorkflow', data)
  return response.data
}

export const delWorkflowApi = async (data: string): Promise<void> => {
  await axiosInstance.get<WofQueryWorkflowRes>('/wof/delWorkflow?workflowId=' + data)
}

export const addWorkflowApi = async (data: AddWorkflowReq): Promise<void> => {
  await axiosInstance.post<WofQueryWorkflowRes>('/wof/addWorkflow', data)
}

export const updateWorkflowApi = async (data: UpdateWorkflowReq): Promise<void> => {
  await axiosInstance.post<WofQueryWorkflowRes>('/wof/updateWorkflow', data)
}
