export interface UpdateWorkReq {
  id: string
  workflowId: string | undefined
  name: string
  workType: string
  comment: string
}
