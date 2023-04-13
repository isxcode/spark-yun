export interface ConfigWorkReq {
  workId: string | undefined
  sql?: string
  datasourceId: string
  calculateEngineId: string
}
