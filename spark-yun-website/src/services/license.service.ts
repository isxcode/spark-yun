/*
 * @Author: fanciNate
 * @Date: 2023-04-26 17:01:16
 * @LastEditTime: 2023-05-03 21:36:23
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/services/computer-group.service.ts
 */
import { http } from "@/utils/http";
interface SerchParams {
  page: number;
  pageSize: number;
  searchKeyWord: string;
}

interface LicenseIdParam {
  licenseId: string;
}

export function GetLicenseList(params: SerchParams): Promise<any> {
  return http.request({
    method: "post",
    url: "/lic/queryLicense",
    params: params,
  });
}

// 上传license证书
export function UploadLicenseFile(params: any): Promise<any> {
  return http.uploadFile({
    method: "post",
    url: "/lic/uploadLicense",
    params,
  });
}

// 启用
export function DisableLicense(params: LicenseIdParam): Promise<any> {
  return http.request({
    method: "get",
    url: "/lic/disableLicense",
    params: params,
  });
}

// 禁用
export function EnableLicense(params: LicenseIdParam): Promise<any> {
  return http.request({
    method: "get",
    url: "/lic/enableLicense",
    params: params,
  });
}

// 删除
export function DeleteLicense(params: LicenseIdParam): Promise<any> {
  return http.request({
    method: "get",
    url: "/lic/deleteLicense",
    params: params,
  });
}
