<template>
  <div class="monitor-chart">
    <div class="monitor-chart__header">
      <span class="monitor-chart__title">{{ monitorData.name }}</span>
      <span class="monitir-chart__active">{{ monitorData.value + monitorData.unit }}</span>
    </div>
    <div class="monitor-chart__body">
      <div ref="chartContainerRef" class="monitor-chart__container"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { MonitorInfo } from './hooks/useMonitor'
import { computed, onMounted, ref, watch } from 'vue'
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
}>(), {})

const chartVm = ref<echarts.ECharts>()

const chartContainerRef = ref<HTMLDivElement>()

const options = computed<EChartsOption>(() => {
  return {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      top: '8%',
      left: '4%',
      right: '8%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      offset: 16,
      data: props.dateTimeList || [],
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
          return value + props.monitorData.unit
        }
      }
    },
    series: [
      {
        name: props.monitorData.type,
        type: 'line',
        color: props.monitorData.color,
        data: props.monitorData.data
      }
    ]
  }
})

watch(() => options.value, (val) => {
  if (chartVm.value) {
    chartVm.value.setOption(options.value)
  }
})

onMounted(() => {
  if (chartContainerRef.value) {
    chartVm.value = echarts.init(chartContainerRef.value)

    chartVm.value.setOption(options.value)
  }
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

  .monitor-chart__body {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
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
}
</style>