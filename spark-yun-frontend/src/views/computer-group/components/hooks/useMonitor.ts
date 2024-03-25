import { computed, ref, type Ref } from "vue"
import { queryClusterMonitorInfo } from "../../services/computer-group"
import type { ColonyInfo } from "../component"
import type { Frequency } from "./useFrequency"

export interface MonitorInfo {
  type: 'cpuPercent' | 'usedMemorySize' | 'diskIoWriteSpeed' | 'usedStorageSize'
  name: string
  value: number
  unit: '%' | 'GB' | 'KB/s' | 'MB/s'
  color: string
  data: Array<number>
}

export function useMonitor(currentColony: Ref<ColonyInfo | undefined>, currentFrequency: Ref<Frequency | undefined>) {
  const dateTimeList = ref<Array<string>>([])
  const cpuMonitorDataList = ref<Array<number>>([])
  const memoryMonitorDataList = ref<Array<number>>([])
  const diskIoMonitorList = ref<Array<number>>([])
  const storageMonitorDataList = ref<Array<number>>([])
  const currentInfo = ref<Record<MonitorInfo['type'], number>>({
    cpuPercent: 0,
    usedMemorySize: 0,
    diskIoWriteSpeed: 0,
    usedStorageSize: 0
  })

  const monitorDataList = computed<Array<MonitorInfo>>(() => {
    return [
      {
        type: 'cpuPercent',
        name: 'CPU',
        value: currentInfo.value.cpuPercent,
        unit: '%',
        color: '#2A82E4',
        data: cpuMonitorDataList.value
      },
      {
        type: 'usedMemorySize',
        name: '内存',
        value: currentInfo.value.usedMemorySize,
        unit: 'GB',
        color: '#FF8D1A',
        data: memoryMonitorDataList.value
      },
      {
        type: 'diskIoWriteSpeed',
        name: 'IO读写',
        value: currentInfo.value.diskIoWriteSpeed,
        unit: 'KB/s',
        color: '#00BAAD',
        data: diskIoMonitorList.value
      },
      {
        type: 'usedStorageSize',
        name: '存储',
        value: currentInfo.value.usedStorageSize,
        unit: 'GB',
        color: '#D43030',
        data: storageMonitorDataList.value
      }
    ]
  })

  function parseMonitorData(data?: string) {
    return data ? parseFloat(data) : 0
  }

  function queryMonitorData() {
    if (!currentColony.value || !currentFrequency.value) return

    queryClusterMonitorInfo({
      clusterId: currentColony.value.id,
      timeType: currentFrequency.value.value
    }).then(({ data }) => {
      let timeList: string[] = []
      let cpuPercentList: number[] = []
      let usedMemorySizeList: number[] = []
      let diskIoWriteSpeedList: number[] = []
      let usedStorageSizeList: number[] = []

      data.line.forEach(clusterMonitorInfo => {
        timeList.push(clusterMonitorInfo.dateTime)

        cpuPercentList.push(parseMonitorData(clusterMonitorInfo.cpuPercent))
        usedMemorySizeList.push(parseMonitorData(clusterMonitorInfo.usedMemorySize))
        diskIoWriteSpeedList.push(parseMonitorData(clusterMonitorInfo.diskIoWriteSpeed))
        usedStorageSizeList.push(parseMonitorData(clusterMonitorInfo.usedStorageSize))
      })

      dateTimeList.value = timeList
      cpuMonitorDataList.value = cpuPercentList
      memoryMonitorDataList.value = usedMemorySizeList
      diskIoMonitorList.value = diskIoWriteSpeedList
      storageMonitorDataList.value = usedStorageSizeList

      currentInfo.value = {
        cpuPercent: cpuMonitorDataList.value[cpuMonitorDataList.value.length - 1],
        usedMemorySize: memoryMonitorDataList.value[memoryMonitorDataList.value.length - 1],
        diskIoWriteSpeed: diskIoMonitorList.value[diskIoMonitorList.value.length - 1],
        usedStorageSize: storageMonitorDataList.value[storageMonitorDataList.value.length - 1]
      }
    })
  }

  return {
    monitorDataList,
    dateTimeList,

    queryMonitorData
  }
}