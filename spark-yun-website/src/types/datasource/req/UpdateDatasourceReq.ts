export interface UpdateDatasourceReq {
  id: string
  name: string
  jdbcUrl: string
  username: string
  port: string
  type: string
  passwd: string
  comment: string
}
