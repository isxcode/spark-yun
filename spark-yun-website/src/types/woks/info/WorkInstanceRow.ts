export interface WorkInstanceRow {
  id?: string
  workName?: string
  workflowName?: string
  instanceType?: string
  status?: string
  execStartDateTime?: string
  execEndDateTime?: string
  nextPlanDateTime?: string
  planStartDateTime?: string
  workType?:string
}
