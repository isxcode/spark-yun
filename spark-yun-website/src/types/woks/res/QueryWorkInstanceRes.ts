import {type BasePaginationRes} from '../../base/BasePaginationRes'
import {WorkInstanceRow} from "../info/WorkInstanceRow";

export interface QueryWorkInstanceRes extends BasePaginationRes {

  content: WorkInstanceRow[]
}
