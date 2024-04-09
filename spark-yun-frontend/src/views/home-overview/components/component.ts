export interface ChartInfo {
  type: 'clusterMonitor' | 'datasourceMonitor' | 'workflowMonitor' | 'apiMonitor'
  title: string
  mix?: number
  total: number
  color: string
}

export interface ColonyInfo {
  id: string
  name: string
  status: string
  defaultCluster: boolean
}