import { type BasePaginationReq } from '../../base/BasePaginationReq'

export interface QueryWorkReq extends BasePaginationReq {
  workflowId: string | undefined
}
