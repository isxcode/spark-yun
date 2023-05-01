import { type BasePaginationRes } from '../../base/BasePaginationRes'
import {UserRow} from "../info/UserRow";

export interface QueryAllUsersRes extends BasePaginationRes {

  content: UserRow[]
}
