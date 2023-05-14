export interface ConfigWorkReq {
  workId: string | undefined
  sqlScript?: string
  datasourceId: string
  calculateEngineId: string
  corn?: string
}
