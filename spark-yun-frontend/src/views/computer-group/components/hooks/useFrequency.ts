import { computed, ref } from "vue"

export interface Frequency {
  name: string
  value: 'THIRTY_MIN' | 'ONE_HOUR' | 'TWO_HOUR' | 'SIX_HOUR' | 'TWELVE_HOUR' | 'ONE_DAY' | 'SEVEN_DAY' | 'THIRTY_DAY'
}

export function useFrequency() {
  const frequencyList = ref<Array<Frequency>>([
    {
      name: '30min',
      value: 'THIRTY_MIN'
    },
    {
      name: '1hour',
      value: 'ONE_HOUR'
    },
    {
      name: '2hour',
      value: 'TWO_HOUR'
    },
    {
      name: '6hour',
      value: 'SIX_HOUR'
    },
    {
      name: '12hour',
      value: 'TWELVE_HOUR'
    },
    {
      name: '1day',
      value: 'ONE_DAY'
    },
    {
      name: '1week',
      value: 'SEVEN_DAY'
    },
    {
      name: '1month',
      value: 'THIRTY_DAY'
    }
  ])

  const currentFrequencyVal = ref('THIRTY_DAY')
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