export interface ConfigWorkReq {
  workId: string | undefined
  sql?: string
  datasourceId: string
  engineId: string
}
