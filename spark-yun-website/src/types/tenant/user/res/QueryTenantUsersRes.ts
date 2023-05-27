import { type BasePaginationRes } from '../../../base/BasePaginationRes'
import { TenantUserRow } from '../info/TenantUserRow'

export interface QueryTenantUsersRes extends BasePaginationRes {

  content: TenantUserRow[]
}
