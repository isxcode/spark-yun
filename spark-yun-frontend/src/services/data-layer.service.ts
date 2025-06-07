import { http } from '@/utils/http'
interface SerchParams {
  page: number
  pageSize: number
  searchKeyWord: string
  parentLayerId?: string
}

// 列表分页查询
export function GetDataLayerList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/layer/searchLayer',
    params: params
  })
}

// 数据分层树形结构查询
export function GetDataLayerTreeData(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/layer/pageLayer',
    params: params
  })
}

// 查询父级分层
export function GetParentLayerNode(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/layer/getParentParentLayer',
    params: params
  })
}

export function SaveDataLayerData(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/layer/addLayer',
    params: params
  })
}

export function UpdateDataLayerData(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/layer/updateLayer',
    params: params
  })
}

// 删除
export function DeleteDataLayerData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/layer/deleteLayer',
    params: params
  })
}
