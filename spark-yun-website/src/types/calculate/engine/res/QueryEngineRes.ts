import { type BasePaginationRes } from '../../../base/BasePaginationRes'
import { type CalculateEngineRow } from '../info/CalculateEngineRow'

export interface QueryEngineRes extends BasePaginationRes {
  content: CalculateEngineRow[]
}
