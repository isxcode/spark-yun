import { http } from '@/utils/http'
interface SerchParams {
  page: number
  pageSize: number
  searchKeyWord: string
  parentLayerId?: string
}

// 列表分页查询
export function GetDataModelList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/layer/searchLayer',
    params: params
  })
}

// 数据分层树形结构查询
export function GetDataModelTreeData(params: SerchParams): Promise<any> {
    return http.request({
      method: 'post',
      url: '/vip/layer/pageLayer',
      params: params
    })
  }

export function SaveDataModelData(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/layer/addLayer',
    params: params
  })
}

export function UpdateDataModelData(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/layer/updateLayer',
    params: params
  })
}

// 删除
export function DeleteDataModelData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/layer/deleteLayer',
    params: params
  })
}
