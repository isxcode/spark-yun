import { type BasePaginationRes } from '../../base/BasePaginationRes'
import {TenantRow} from "../info/TenantRow";

export interface QueryTenantsRes extends BasePaginationRes {

  content: TenantRow[]
}
