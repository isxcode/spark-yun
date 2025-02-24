<template>
  <div class="monitor-chart">
    <div class="monitor-chart__header">
      <span class="monitor-chart__title">{{ monitorData.name }}</span>
      <span class="monitor-chart__active" v-if="!isEmpty">{{ monitorData.value + monitorData.unit }}</span>
    </div>
    <div class="monitor-chart__body">
      <div v-if="!isEmpty" ref="chartContainerRef" class="monitor-chart__container"></div>
      <empty-page v-else></empty-page>
    </div>
  </div>
</template>

<script setup lang="ts">
import { MonitorInfo } from './hooks/useMonitor'
import { computed, nextTick, onMounted, onUnmounted, ref, watch, defineEmits } from 'vue'
import * as echarts from 'echarts/core';
import {
  TooltipComponent,
  TooltipComponentOption,
  GridComponent,
  GridComponentOption,
} from 'echarts/components';
import { LineChart, LineSeriesOption } from 'echarts/charts';
import { UniversalTransition } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';

import allScreen from "@/assets/imgs/fullScreen.svg";

echarts.use([
  TooltipComponent,
  GridComponent,
  LineChart,
  CanvasRenderer,
  UniversalTransition
])

type EChartsOption = echarts.ComposeOption<
  | TooltipComponentOption
  | GridComponentOption
  | LineSeriesOption
>

const props = withDefaults(defineProps<{
    monitorData: MonitorInfo
    dateTimeList: Array<string>
    hideFull: boolean
}>(), {})

const chartVm = ref<echarts.ECharts>()
const chartContainerRef = ref<HTMLDivElement>()

const emit = defineEmits([ 'showDetailEvent' ])

const isEmpty = computed(() => {
  return props.monitorData.data.length === 0
})

const options = computed<EChartsOption>(() => {
  const status = !props?.hideFull
  const gridBottom = props?.hideFull ? '16%' : '0'
  return {
    tooltip: {
      trigger: 'item',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    toolbox: {
      show: status,
      top: 0,
      right: 18,
      feature: {
        myFullScreen: {
          title: '',
          icon: "image://".concat(allScreen),
          onclick: () => {
            emit('showDetailEvent')
          }
        }
      }
    },
    grid: {
      top: '18%',
      left: '4%',
      right: '8%',
      bottom: gridBottom,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      offset: 16,
      data: props.dateTimeList,
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter (value) {
          return value + props.monitorData?.unit
        }
      }
    },
    series: [
      {
        name: props.monitorData?.type,
        type: 'line',
        areaStyle: {},
        smooth: true,
        color: props.monitorData?.color,
        data: props.monitorData?.data
      }
    ]
  }
})

watch(() => options.value, (val) => {
  if (chartVm.value) {
    chartVm.value.setOption(options.value)
  }
})

watch(() => isEmpty.value, (newVal) => {
  if (!newVal && chartContainerRef.value) {
    chartVm.value = echarts.init(chartContainerRef.value)
    chartVm.value.setOption(options.value)
  }
}, {
  flush: 'post'
})

function resizeChart() {
  console.log('初始化')
  nextTick(() => {
    chartVm.value?.resize()
  })
}

onMounted(() => {
  if (chartContainerRef.value) {
    chartVm.value = echarts.init(chartContainerRef.value)
    chartVm.value.setOption(options.value)

    window.addEventListener('resize', resizeChart)
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeChart)
})
</script>

<style scoped lang="scss">
.monitor-chart {
  margin-top: 24px;
  height: 200px;
  border-radius: 8px;
  background-color: getCssVar('color', 'white');
  box-shadow: getCssVar('box-shadow', 'lighter');
  padding: 12px;
  transition: all 0.3s linear;

  .monitor-chart__body {
    display: flex;
    align-items: center;
    justify-content: center;
    height: calc(100% - 40px);
    position: relative;
  }

  .monitor-chart__title, .monitor-chart__active {
    font-size: getCssVar('font-size', 'medium');
  }

  .monitor-chart__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 12px;
    height: 40px;
  }

  .monitor-chart__container {
    width: 300px;
    height: 100%;
  }

  .monitor-chart__empty {
    padding: 0;
    
    --el-empty-image-width: 60px;
  }
}
</style>