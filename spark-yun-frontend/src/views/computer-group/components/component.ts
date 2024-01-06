export interface ChartInfo {
  type: 'computer-group' | 'data-source' | 'publish-job' | 'publish-interface',
  title: string,
  mix: number,
  color: string
}

export interface VmData {
  name: string
  status: 'SUCCESS' | 'FAILED'
  publisherName: string
  publisherUrl?: string
  startTime: string
  endTime: string
}

export interface ColonyInfo {
  id: string
  name: string
}