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

interface UserParam {
  userId: string;
}

interface User {
  account: string;
  email: string;
  passwd?: string;
  phone: string;
  remark: string;
  username: string;
  id?: string;
}

// 用户中心-查询用户成员
export function GetUserCenterList(params: SerchParams): Promise<any> {
  return http.request({
    method: "post",
    url: "/usr/queryAllUsers",
    params: params,
  });
}

// 用户中心-启用
export function DisableUser(params: UserParam): Promise<any> {
  return http.request({
    method: "get",
    url: "/usr/disableUser",
    params: params,
  });
}

// 用户中心-禁用
export function EnableUser(params: UserParam): Promise<any> {
  return http.request({
    method: "get",
    url: "/usr/enableUser",
    params: params,
  });
}

// 用户中心-添加用户
export function AddUserData(params: User): Promise<any> {
  return http.request({
    method: "post",
    url: "/usr/addUser",
    params: params,
  });
}

// 用户中心-编辑用户
export function UpdateUserData(params: User): Promise<any> {
  return http.request({
    method: "post",
    url: "/usr/updateUser",
    params: params,
  });
}

// 用户中心-删除用户
export function DeleteUser(params: UserParam): Promise<any> {
  return http.request({
    method: "get",
    url: "/usr/deleteUser",
    params: params,
  });
}
