import { type WofQueryWorkflowRes } from '../../types/workflow/res/WofQueryWorkflowRes'
import axiosInstance from '../../config/axios'
import { type QueryWorkReq } from '../../types/woks/req/QueryWorkReq'
import { type QueryWorkRes } from '../../types/woks/res/QueryWorkRes'
import { type AddWorkReq } from '../../types/woks/req/AddWorkReq'
import { UpdateWorkReq } from '../../types/woks/req/UpdateWorkReq'
import { message } from 'antd'
import { WorkInfo } from '../../types/woks/info/WorkInfo'
import { RunWorkRes } from '../../types/woks/res/RunWorkRes'
import { ConfigWorkReq } from '../../types/woks/req/ConfigWorkReq'
import { BaseResponse } from '../../types/base/BaseResponse'
import { QueryWorkInstanceReq } from '../../types/woks/req/QueryWorkInstanceReq'
import { QueryWorkInstanceRes } from '../../types/woks/res/QueryWorkInstanceRes'
import { GetSubmitLogRes } from '../../types/woks/res/GetSubmitLogRes'

const headerConfig = {
  timeout: 600000,
  headers: {
    Tenant: localStorage.getItem('Tenant'),
    Authorization: localStorage.getItem('Token')
  }
}

export const queryWorkApi = async (data: QueryWorkReq): Promise<QueryWorkRes> => {
  data.page = data.page - 1
    const response = await axiosInstance.post<WofQueryWorkflowRes>('/wok/queryWork', data, headerConfig)
  return response.data
}

export const queryWorkInstanceApi = async (data: QueryWorkInstanceReq): Promise<QueryWorkInstanceRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<WofQueryWorkflowRes>('/vip/woi/queryInstance', data, headerConfig)
  return response.data
}

export const delWorkApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/wok/delWork?workId=' + data, headerConfig)
  message.success(response.msg)
}

export const addWorkApi = async (data: AddWorkReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/wok/addWork', data, headerConfig)
  message.success(response.msg)
}

export const updateWorkApi = async (data: UpdateWorkReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/wok/updateWork', data, headerConfig)
  message.success(response.msg)
}

export const getWorkApi = async (workId: string): Promise<WorkInfo> => {
  const response = await axiosInstance.get<WorkInfo>('/wok/getWork?workId=' + workId, headerConfig)
  return response.data
}

export const getWorkLogApi = async (instanceId: string): Promise<RunWorkRes> => {
  const response = await axiosInstance.get<RunWorkRes>('/wok/getYarnLog?instanceId=' + instanceId, headerConfig)
  return response.data
}

export const getWorkDataApi = async (instanceId: string): Promise<RunWorkRes> => {
  const response = await axiosInstance.get<RunWorkRes>('/wok/getData?instanceId=' + instanceId, headerConfig)
  return response.data
}

export const getWorkStatusApi = async (instanceId: string): Promise<RunWorkRes> => {
  const response = await axiosInstance.get<RunWorkRes>('/wok/getStatus?instanceId=' + instanceId, headerConfig)
  return response.data
}

export const stopWorkApi = async (workId: string, applicationId: string | undefined): Promise<RunWorkRes> => {
  const response = await axiosInstance.get<RunWorkRes>(
    '/wok/stopJob?workId=' + workId + '&applicationId=' + applicationId, headerConfig
  )
  message.success(JSON.parse(JSON.stringify(response)).msg)
  return response.data
}

export const configWorkApi = async (data: ConfigWorkReq): Promise<void> => {
  const response = await axiosInstance.post('/woc/configWork', data, headerConfig)
  message.success(JSON.parse(JSON.stringify(response)).msg)
  return response.data
}

export const runWorkApi = async (workId: string): Promise<RunWorkRes> => {
  const response = await axiosInstance.get<RunWorkRes>('/wok/runWork?workId=' + workId, headerConfig)
  message.success(JSON.parse(JSON.stringify(response)).msg)
  return response.data
}

export const depolyWorkApi = async (workId: string): Promise<void> => {
  const response = await axiosInstance.get<RunWorkRes>('/vip/wok/deployWork?workId=' + workId, headerConfig)
  message.success(JSON.parse(JSON.stringify(response)).msg)
}

export const pauseWorkApi = async (workId: string): Promise<void> => {
  const response = await axiosInstance.get<RunWorkRes>('/vip/wok/pauseWork?workId=' + workId, headerConfig)
  message.success(JSON.parse(JSON.stringify(response)).msg)
}

export const resumeWorkApi = async (workId: string): Promise<void> => {
  const response = await axiosInstance.get<RunWorkRes>('/vip/wok/resumeWork?workId=' + workId, headerConfig)
  message.success(JSON.parse(JSON.stringify(response)).msg)
}

export const deleteWorkApi = async (workId: string): Promise<void> => {
  const response = await axiosInstance.get<RunWorkRes>('/vip/wok/deleteWork?workId=' + workId, headerConfig)
  message.success(JSON.parse(JSON.stringify(response)).msg)
}

export const deleteWorkInstanceApi = async (instanceId: string): Promise<void> => {
  const response = await axiosInstance.get<RunWorkRes>('/vip/woi/deleteInstance?instanceId=' + instanceId, headerConfig)
  message.success(JSON.parse(JSON.stringify(response)).msg)
}

export const restartWorkInstanceApi = async (instanceId: string): Promise<void> => {
  const response = await axiosInstance.get<RunWorkRes>('/vip/woi/restartInstance?instanceId=' + instanceId, headerConfig)
  message.success(JSON.parse(JSON.stringify(response)).msg)
}

export const getSubmitLogApi = async (instanceId: string): Promise<GetSubmitLogRes> => {
  const response = await axiosInstance.get<GetSubmitLogRes>('/wok/getSubmitLog?instanceId=' + instanceId, headerConfig)
  return response.data
}
