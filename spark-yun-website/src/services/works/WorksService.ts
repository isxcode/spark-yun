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

export const queryWorkApi = async (data: QueryWorkReq): Promise<QueryWorkRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<WofQueryWorkflowRes>('/wok/queryWork', data)
  return response.data
}

export const delWorkApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/wok/delWork?workId=' + data)
  message.success(response.msg)
}

export const addWorkApi = async (data: AddWorkReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/wok/addWork', data)
  message.success(response.msg)
}

export const updateWorkApi = async (data: UpdateWorkReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/wok/updateWork', data)
  message.success(response.msg)
}

export const getWorkApi = async (workId: string): Promise<WorkInfo> => {
  const response = await axiosInstance.get<WorkInfo>('/wok/getWork?workId=' + workId)
  return response.data
}

export const getWorkLogApi = async (workId: string, applicationId: string | undefined): Promise<RunWorkRes> => {
  const response = await axiosInstance.post<RunWorkRes>('/wok/getWorkLog', {
    workId,
    applicationId
  })
  return response.data
}

export const getWorkDataApi = async (workId: string, applicationId: string | undefined): Promise<RunWorkRes> => {
  const response = await axiosInstance.post<RunWorkRes>('/wok/getData', {
    workId,
    applicationId
  })
  return response.data
}

export const getWorkStatusApi = async (workId: string, applicationId: string | undefined): Promise<RunWorkRes> => {
  const response = await axiosInstance.post<RunWorkRes>('/wok/getStatus', {
    workId,
    applicationId
  })
  return response.data
}

export const stopWorkApi = async (workId: string, applicationId: string | undefined): Promise<RunWorkRes> => {
  const response = await axiosInstance.get<RunWorkRes>(
    '/wok/stopJob?workId=' + workId + '&applicationId=' + applicationId
  )
  message.success(JSON.parse(JSON.stringify(response)).msg)
  return response.data
}

export const configWorkApi = async (data: ConfigWorkReq): Promise<void> => {
  const response = await axiosInstance.post('/woc/configWork', data)
  message.success(JSON.parse(JSON.stringify(response)).msg)
  return response.data
}

export const runWorkApi = async (workId: string): Promise<RunWorkRes> => {
  const response = await axiosInstance.get<RunWorkRes>('/wok/runWork?workId=' + workId, { timeout: 600000 })
  message.success(JSON.parse(JSON.stringify(response)).msg)
  return response.data
}
