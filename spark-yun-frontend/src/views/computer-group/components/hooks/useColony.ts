import { computed, ref } from "vue"
import type { ColonyInfo } from "../component"

export function useColony() {
  const colonyList = ref<Array<ColonyInfo>>([
    {
      id: 'default',
      name: '默认集群'
    },
    {
      id: 'test',
      name: '测试集群'
    },
  ])
  
  const currentColonyId = ref<string>('default')
  
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