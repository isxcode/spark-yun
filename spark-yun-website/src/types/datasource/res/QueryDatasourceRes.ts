import { type BasePaginationRes } from '../../base/BasePaginationRes'
import { DatasourceRow } from '../info/DatasourceRow'

export interface QueryDatasourceRes extends BasePaginationRes {
  content: DatasourceRow[]
}
