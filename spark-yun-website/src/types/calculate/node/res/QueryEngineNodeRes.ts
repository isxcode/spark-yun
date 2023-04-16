import { type EngineNodeRow } from '../info/EngineNodeRow'
import { type BasePaginationRes } from '../../../base/BasePaginationRes'

export interface QueryEngineNodeRes extends BasePaginationRes {
  content: EngineNodeRow[]
}
