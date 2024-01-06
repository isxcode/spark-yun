import { ref } from "vue"

export interface MonitorInfo {
  type: 'CPU' | 'MEMORY' | 'IO' | 'DISK'
  name: string
  value: number
  unit: '%' | 'GB' | 'M/s' | 'TB'
  color: string
  data: Array<number>
}

export function useMonitor() {
  const monitorDataList = ref<Array<MonitorInfo>>([
    {
      type: 'CPU',
      name: 'CPU',
      value: 10,
      unit: '%',
      color: '#2A82E4',
      data: [30, 55, 30, 90, 0]
    },
    {
      type: 'MEMORY',
      name: '内存',
      value: 90,
      unit: 'GB',
      color: '#FF8D1A',
      data: [10, 60, 60, 75, 100]
    },
    {
      type: 'IO',
      name: 'IO读写',
      value: 1.9,
      unit: 'M/s',
      color: '#00BAAD',
      data: [1.5, 2, 1, 1.5, 0]
    },
    {
      type: 'DISK',
      name: '存储',
      value: 1,
      unit: 'TB',
      color: '#D43030',
      data: [0.5, 0.7, 1, 2, 1.5]
    }
  ])

  return {
    monitorDataList
  }
}