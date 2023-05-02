import axiosInstance from '../../../config/axios'
import {message} from 'antd'
import {BaseResponse} from '../../../types/base/BaseResponse'
import {UpdateUserReq} from "../../../types/user/req/UpdateUserReq";
import {QueryTenantsRes} from "../../../types/tenant/res/QueryTenantsRes";
import {AddTenantReq} from "../../../types/tenant/req/AddTenantReq";
import {QueryTenantUserReq} from "../../../types/tenant/user/req/QueryTenantUserReq";
import {QueryTenantUsersRes} from "../../../types/tenant/user/res/QueryTenantUsersRes";
import {AddTenantUserReq} from "../../../types/tenant/user/req/AddTenantUserReq";

const headerConfig = {
  headers: {
    "Authorization": localStorage.getItem("Token"),
    "Tenant": localStorage.getItem("Tenant"),
  }
};

export const queryTenantUserApi = async (data: QueryTenantUserReq): Promise<QueryTenantUsersRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<QueryTenantsRes>('/tur/queryTenantUser', data, headerConfig);
  return response.data
};

export const setTenantAdminApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/tur/setTenantAdmin?tenantUserId=' + data, headerConfig);
  message.success(response.msg)
};

export const  removeTenantAdminApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/tur/removeTenantAdmin?tenantUserId=' + data, headerConfig);
  message.success(response.msg)
};

export const  removeTenantUserApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/tur/removeTenantUser?tenantUserId=' + data, headerConfig);
  message.success(response.msg)
};

export const addTenantUserApi = async (data: AddTenantUserReq): Promise<void> => {
  const response: BaseResponse = await axiosInstance.post('/tur/addTenantUser', data, headerConfig);
  message.success(response.msg)
};
