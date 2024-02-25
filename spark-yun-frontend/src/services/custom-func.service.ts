import { http } from '@/utils/http'
interface SerchParams {
  page: number
  pageSize: number
  searchKeyWord: string
}

export function GetCustomFuncList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/func/pageFunc',
    params: params
  })
}

export function SaveCustomFuncData(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/func/addFunc',
    params: params
  })
}

export function UpdateCustomFuncData(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/func/updateFunc',
    params: params
  })
}


// 删除
export function DeleteFuncData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/func/deleteFunc',
    params: params
  })
}
