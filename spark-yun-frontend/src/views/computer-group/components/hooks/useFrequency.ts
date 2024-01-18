import { computed, ref } from "vue"

export interface Frequency {
  name: string
  value: string
}

export function useFrequency() {
  const frequencyList = ref<Array<Frequency>>([
    {
      name: '30min',
      value: '30min'
    },
    {
      name: '60min',
      value: '60min'
    }
  ])

  const currentFrequencyVal = ref('30min')
  const currentFrequency = computed<Frequency | undefined>(() => frequencyList.value.find(item => item.value === currentFrequencyVal.value))

  const onFrequencyChange = (command: string) => {
    currentFrequencyVal.value = command
  }


  return {
    frequencyList,
    currentFrequency,

    onFrequencyChange
  }
}