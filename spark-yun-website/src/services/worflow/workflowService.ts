import { type QueryWorkflowReq } from './req/QueryWorkflowReq'
import { type QueryWorkflowRes } from './res/QueryWorkflowRes'
import axiosInstance from '../axios'
import axios from 'axios/index'
import { message } from 'antd'
import { type AddWorkflowReq } from './req/AddWorkflowReq'

export const queryWorkflowApi = async (data: QueryWorkflowReq): Promise<QueryWorkflowRes> => {
  const response = await axiosInstance.post<QueryWorkflowRes>('/wof/queryWorkflow', data)
  return response.data
}

export const delWorkflowApi = async (data: string): Promise<void> => {
  await axiosInstance.get<QueryWorkflowRes>('/wof/delWorkflow?workflowId=' + data)
}

export const addWorkflowApi = async (data: AddWorkflowReq): Promise<void> => {
  await axiosInstance.post<QueryWorkflowRes>('/wof/addWorkflow', data)
}
