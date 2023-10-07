import { http } from '@/utils/http'

export interface UpdateUserInfoParams {
  username: string
  phone?: string
  email?: string
  remark?: string
}

export function UpdateUserInfo(params: UpdateUserInfoParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/user/updateUserInfo',
    params: params
  })
}
