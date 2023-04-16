import { type WorkflowRow } from '../info/WorkflowRow'
import { type BasePaginationRes } from '../../base/BasePaginationRes'

export interface WofQueryWorkflowRes extends BasePaginationRes {
  content: WorkflowRow[]
}
