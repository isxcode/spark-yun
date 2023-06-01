import { http } from "@/utils/http";

interface LoginParam {
  account: string;
  passwd: string;
}

interface TenantParams {
  tenantId: string;
}

// 登录接口
export function LoginUserInfo(params: LoginParam): Promise<any> {
  return http.request({
    method: "post",
    url: "/usr/open/login",
    params: params,
  });
}

// 查询租户信息
export function QueryTenantList(): Promise<any> {
  return http.request({
    method: "get",
    url: "/tet/queryUserTenant",
  });
}

// 切换租户
export function ChangeTenantData(params: TenantParams): Promise<any> {
  return http.request({
    method: "get",
    url: "/tet/chooseTenant",
    params: params,
  });
}
