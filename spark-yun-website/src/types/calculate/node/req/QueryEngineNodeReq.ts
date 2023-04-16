import { type BasePaginationReq } from '../../../base/BasePaginationReq'

export interface QueryEngineNodeReq extends BasePaginationReq {
  calculateEngineId: string
}
