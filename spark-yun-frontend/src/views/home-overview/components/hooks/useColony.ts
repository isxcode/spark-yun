import { computed, ref } from "vue"
import type { ColonyInfo } from "../component"
import { queryAllClusterInfo } from "../../services/computer-group"

export function useColony() {
  const colonyList = ref<Array<ColonyInfo>>([])
  
  const currentColonyId = ref<string>()
  
  const currentColony = computed<ColonyInfo | undefined>(() => {
    return colonyList.value.find(item => item.id === currentColonyId.value)
  })

  const onColonyChange = (command: string) => {
    currentColonyId.value = command
  }

  function queryColonyData() {
    return queryAllClusterInfo().then(({ data }) => {
      colonyList.value = data

      let defaultCluster = data.find(item => item.defaultCluster) || data[0]

      currentColonyId.value = defaultCluster.id
    })
  }

  return {
    currentColony,

    colonyList,

    queryColonyData,
    onColonyChange
  }
}