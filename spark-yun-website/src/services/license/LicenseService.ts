import axiosInstance from '../../config/axios'
import {message} from 'antd'
import {BaseResponse} from '../../types/base/BaseResponse'
import {QueryLicenseReq} from "../../types/license/req/QueryLicenseReq";
import {QueryLicenseRes} from "../../types/license/res/QueryLicenseRes";

const headerConfig = {
  headers: {
    "Authorization": localStorage.getItem("Token"),
    "Tenant": localStorage.getItem("Tenant"),
  }
};

export const queryLicenseApi = async (data: QueryLicenseReq): Promise<QueryLicenseRes> => {
  data.page = data.page - 1
  const response = await axiosInstance.post<QueryLicenseRes>('/lic/queryLicense', data, headerConfig);
  return response.data
};

export const enableLicenseApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/lic/enableLicense?licenseId=' + data, headerConfig);
  message.success(response.msg)
}

export const deleteLicenseApi = async (data: string | undefined): Promise<void> => {
  const response: BaseResponse = await axiosInstance.get('/lic/deleteLicense?licenseId=' + data, headerConfig);
  message.success(response.msg)
}
