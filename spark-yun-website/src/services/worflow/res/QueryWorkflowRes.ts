import { type Workflow } from './Workflow'

export interface QueryWorkflowRes {
  username: string

  content: Workflow[]

  totalElements: number

  currentPage: number

  pageSize: number
}
