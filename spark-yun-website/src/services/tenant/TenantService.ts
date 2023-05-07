import axiosInstance from '../../config/axios'
import { message } from 'antd'
import { BaseResponse } from '../../types/base/BaseResponse'
import { UpdateUserReq } from '../../types/user/req/UpdateUserReq'
import { QueryTenantsReq } from '../../types/tenant/req/QueryTenantsReq'
import { QueryTenantsRes } from '../../types/tenant/res/QueryTenantsRes'
import { AddTenantReq } from '../../types/tenant/req/AddTenantReq'
import {GetTenantsRes} from "../../types/tenant/res/GetTenantsRes";
import {TenantRow} from "../../types/tenant/info/TenantRow";

const headerConfig = {
  headers: {
    Authorization: localStorage.getItem('Token'),
    Tenant: localStorage.getItem('Tenant')
  }
}

export const queryTenantsApi = async (data: QueryTenantsReq): Promise<QueryTenantsRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<QueryTenantsRes>('/tet/queryTenant', data, headerConfig)
  return response.data
}

export const queryUserTenantsApi = async (): Promise<TenantRow[]> => {
  const response = await axiosInstance.get<TenantRow[]>('/tet/queryUserTenant', headerConfig);
  return response.data
};

export const enableTenantApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/tet/enableTenant?tenantId=' + data, headerConfig)
  message.success(response.msg)
}

export const disableTenantApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/tet/disableTenant?tenantId=' + data, headerConfig)
  message.success(response.msg)
}

export const checkTenantApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/tet/checkTenant?tenantId=' + data, headerConfig)
  message.success(response.msg)
}

export const delTenantApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/tet/deleteTenant?tenantId=' + data, headerConfig)
  message.success(response.msg)
}

export const chooseTenantApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/tet/chooseTenant?tenantId=' + data, headerConfig)
  message.success(response.msg)
}

export const getTenantApi = async (data: string | undefined): Promise<GetTenantsRes> => {
  const response: BaseResponse = await axiosInstance.get('/tet/getTenant?tenantId=' + data, headerConfig)
  return response.data
};

export const updateTenantApi = async (data: UpdateUserReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/tet/updateTenantBySystemAdmin', data, headerConfig)
  message.success(response.msg)
}

export const addTenantApi = async (data: AddTenantReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/tet/addTenant', data, headerConfig)
  message.success(response.msg)
}

