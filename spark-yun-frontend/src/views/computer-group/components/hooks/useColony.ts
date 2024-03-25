import { computed, ref } from "vue"
import type { ColonyInfo } from "../component"

export function useColony() {
  const colonyList = ref<Array<ColonyInfo>>([
    {
      id: 'sy_1764148070322429952',
      name: '默认集群'
    }
  ])
  
  const currentColonyId = ref<string>('sy_1764148070322429952')
  
  const currentColony = computed<ColonyInfo | undefined>(() => {
    return colonyList.value.find(item => item.id === currentColonyId.value)
  })

  const onColonyChange = (command: string) => {
    currentColonyId.value = command
  }

  return {
    currentColony,

    colonyList,

    onColonyChange
  }
}