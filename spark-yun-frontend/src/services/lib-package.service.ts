import { http } from '@/utils/http'

interface SearchParams {
  page: number
  pageSize: number
  searchKeyWord?: string
}

// 分页查询依赖包
export function PageLibPackage(params: SearchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/file/pageLibPackage',
    params: params
  })
}

// 添加依赖包
export function AddLibPackage(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/file/addLibPackage',
    params: params
  })
}

// 编辑依赖包
export function UpdateLibPackage(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/file/updateLibPackage',
    params: params
  })
}

// 配置依赖包
export function ConfigLibPackage(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/file/configLibPackage',
    params: params
  })
}

// 获取依赖包信息
export function GetLibPackage(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/file/getLibPackage',
    params: params
  })
}

// 删除依赖包
export function DeleteLibPackage(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/file/deleteLibPackage',
    params: params
  })
}

