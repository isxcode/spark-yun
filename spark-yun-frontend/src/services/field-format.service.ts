import { http } from '@/utils/http'
interface SerchParams {
  page: number
  pageSize: number
  searchKeyWord: string
  parentLayerId?: string
}

// 列表分页查询
export function GetFieldFormatList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/pageColumnFormat',
    params: params
  })
}

export function SaveFieldFormatData(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/addColumnFormat',
    params: params
  })
}

export function UpdateFieldFormatData(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/updateColumnFormat',
    params: params
  })
}

// 删除
export function DeleteFieldFormatData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/deleteColumnFormat',
    params: params
  })
}

// 启用
export function EnableFieldFormatData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/enableColumnFormat',
    params: params
  })
}

// 禁用
export function DisabledFieldFormatData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/disableColumnFormat',
    params: params
  })
}
