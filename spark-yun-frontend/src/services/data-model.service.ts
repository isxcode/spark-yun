import { http } from '@/utils/http'
interface SerchParams {
  page: number
  pageSize: number
  searchKeyWord: string
  parentLayerId?: string
}

// 分页查询数据模型
export function GetDataModelList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/pageDataModel',
    params: params
  })
}

// 分页查询分层下对应的数据模型
export function GetDataModelTreeData(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/pageDataModelByLayer',
    params: params
  })
}

// 新建数据模型
export function SaveDataModelData(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/addDataModel',
    params: params
  })
}

// 更新数据模型
export function UpdateDataModelData(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/updateDataModel',
    params: params
  })
}

// 删除数据模型
export function DeleteDataModelData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/deleteDataModel',
    params: params
  })
}

// 重置数据模型
export function ResetDataModel(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/resetDataModel',
    params: params
  })
}

// 一键复制数据模型
export function CopyDataModelData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/copyDataModel',
    params: params
  })
}

// 模型字段 --------------------
// 查询模型字段
export function GetModelFieldList(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/pageDataModelColumn',
    params: params
  })
}
// 添加模型字段
export function AddModelFieldData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/addDataModelColumn',
    params: params
  })
}
// 更新模型字段
export function UpdateModelFieldData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/updateDataModelColumn',
    params: params
  })
}
// 删除模型字段
export function DeleteModelField(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/deleteDataModelColumn',
    params: params
  })
}

// 更新模型字段排序
export function UpdateModelFieldList(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/vip/model/updateDataModelColumnIndex',
    params: params
  })
}
