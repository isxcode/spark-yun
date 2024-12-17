import { http } from '@/utils/http'
interface SerchParams {
  page: number
  pageSize: number
  searchKeyWord: string
  type: string
}

interface fileCenterParam {
  licenseId: string;
}

export function GetFileCenterList(params: SerchParams): Promise<any> {
  return http.request({
    method: 'post',
    url: '/file/pageFile',
    params: params
  })
}

// 上传
export function UploadFileData(params: any): Promise<any> {
  return http.uploadFile({
    method: 'post',
    url: '/file/uploadFile',
    params
  })
}

export function DownloadFileData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/file/downloadFile',
    responseType: 'blob',
    params
  })
}

// 删除
export function DeleteFileData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: '/file/deleteFile',
    params: params
  })
}

// 更新
export function UpdateFileData(params: any): Promise<any> {
  return http.request({
    method: 'post',
    url: `/file/updateFile?fileId=${params.fileId}&remark=${params.remark}`
  })
}
