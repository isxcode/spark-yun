import { type BasePaginationRes } from '../../base/BasePaginationRes'
import { WorkRow } from '../info/WorkRow'

export interface RunWorkRes {
  message?: string
  log?: string
  yarnLog?: string
  data: [[]]
  applicationId?: string
  yarnApplicationState?: string
  finalApplicationStatus?: string
  trackingUrl?: string
}
