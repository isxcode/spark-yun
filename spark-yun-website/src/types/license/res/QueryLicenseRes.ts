import { type BasePaginationRes } from '../../base/BasePaginationRes'
import {LicenseRow} from "../info/LicenseRow";

export interface QueryLicenseRes extends BasePaginationRes {

  content: LicenseRow[]
}
