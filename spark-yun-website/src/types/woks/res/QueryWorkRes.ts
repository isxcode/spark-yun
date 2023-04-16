import { type BasePaginationRes } from '../../base/BasePaginationRes'
import { WorkRow } from '../info/WorkRow'

export interface QueryWorkRes extends BasePaginationRes {
  content: WorkRow[]
}
