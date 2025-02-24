import { computed, ref } from "vue"

export interface Frequency {
  name: string
  value: 'THIRTY_MIN' | 'ONE_HOUR' | 'TWO_HOUR' | 'SIX_HOUR' | 'TWELVE_HOUR' | 'ONE_DAY' | 'SEVEN_DAY' | 'THIRTY_DAY'
}

export function useFrequency() {
  const frequencyList = ref<Array<Frequency>>([
    {
      name: '30分钟',
      value: 'THIRTY_MIN'
    },
    {
      name: '1小时',
      value: 'ONE_HOUR'
    },
    {
      name: '2小时',
      value: 'TWO_HOUR'
    },
    {
      name: '6小时',
      value: 'SIX_HOUR'
    },
    {
      name: '12小时',
      value: 'TWELVE_HOUR'
    },
    {
      name: '1天',
      value: 'ONE_DAY'
    },
    {
      name: '1周',
      value: 'SEVEN_DAY'
    },
    {
      name: '1月',
      value: 'THIRTY_DAY'
    }
  ])

  const currentFrequencyVal = ref('THIRTY_MIN')
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