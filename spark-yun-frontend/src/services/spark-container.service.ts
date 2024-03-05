import { http } from '@/utils/http'
interface SerchParams {
  page: number;
  pageSize: number;
  searchKeyWord: string;
}

export function GetDatasourceList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/datasource/pageDatasource',
    params: params
  })
}

// 添加
export function AddDatasourceData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/datasource/addDatasource',
    params: params
  })
}

// 更新
export function UpdateDatasourceData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/datasource/updateDatasource',
    params: params
  })
}

// 检测
export function CheckDatasourceData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/datasource/testConnect',
    params: params
  })
}

// 删除
export function DeleteDatasourceData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/datasource/deleteDatasource',
    params: params
  })
}

// // 获取数据源驱动
// export function GetDriverListData(params: any): Promise<any> {
//   return http.request({
//     method: 'post',
//     url: '/datasource/pageDatabaseDriver',
//     params: params
//   })
// }
// // 获取默认驱动
// export function GetDefaultDriverData(params: any): Promise<any> {
//   return http.request({
//     method: 'post',
//     url: '/datasource/getDefaultDatabaseDriver',
//     params: params
//   })
// }


