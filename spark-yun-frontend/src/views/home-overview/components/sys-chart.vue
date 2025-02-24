<template>
  <div class="sys-chart">
    <span class="sys-chart__title">{{ chartData.title }}</span>
    <div class="sys-chart__body">
      <div v-if="!isEmpty" ref="bodyContainer" class="sys-chart__container"></div>
      <empty-page v-else></empty-page>
    </div>
    <div class="sys-chart__footer" v-if="!isEmpty">
      <span class="sys-chart__mix">{{ chartData.mix }}/{{ chartData.total }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { ChartInfo } from './component'
import * as echarts from 'echarts/core'
import { GaugeChart } from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'
import { EChartsOption } from 'echarts'

echarts.use([
  GaugeChart,
  CanvasRenderer
])

const props = withDefaults(defineProps<{
  chartData: ChartInfo
}>(), {
  chartData: () => ({} as ChartInfo)
})

const isEmpty = computed(() => {
  return !props.chartData.mix
})

watch(() => isEmpty.value, (newVal) => {
  if (!newVal && bodyContainer.value) {
    chartVm.value = echarts.init(bodyContainer.value)
    chartVm.value.setOption(options.value)
  }
}, {
  flush: 'post'
})

const chartVm = ref<echarts.ECharts>()

const options = computed<EChartsOption>(() => {
  const options: EChartsOption = {
    series: [
      {
        type: 'gauge',
        startAngle: 0,
        endAngle: 360,
        max: props.chartData.total,
        pointer: {
          show: false
        },
        progress: {
          show: true,
          overlap: true,
          roundCap: false,
          clip: true,
          itemStyle: {
            borderCap: 'square',
            borderWidth: 12,
            borderColor: props.chartData.color
          }
        },
        axisLine: {
          show: false,
          lineStyle: {
            width: 4,
            color: [[1, props.chartData.color]]
          }
        },
        splitLine: {
          show: false,
          distance: 0,
          length: 10
        },
        axisTick: {
          show: false
        },
        axisLabel: {
          show: false,
          distance: 50
        },
        data: [
          {
            value: props.chartData.mix
          }
        ],
        title: {
          fontSize: 14
        },
        detail: {
          fontSize: 16,
          height: 80,
          lineHeight: 80,
          width: 68,
          offsetCenter: [0, 0],
          borderRadius: 1000,
          borderColor: props.chartData.color,
          borderWidth: 2,
          formatter: (value) => {
            return Math.round(value/props.chartData.total * 100) + '%'
          } 
        }
      }
    ]
  }

  return options
})

watch(() => options.value, (val) => {
  if (chartVm.value) {
    chartVm.value.setOption(options.value)
  }
})

const bodyContainer = ref<HTMLDivElement>()

</script>

<style lang="scss" scoped>
.sys-chart {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 160px;
  height: 220px;
  border-radius: 8px;
  background-color: getCssVar('color', 'white');
  box-shadow: getCssVar('box-shadow', 'lighter');
  padding: 12px;

  .sys-chart__title {
    font-size: getCssVar('font-size', 'medium');
    font-weight: bold;
  }

  .sys-chart__mix {
    font-size: getCssVar('font-size', 'large');
    font-weight: bold;
  }

  .sys-chart__body {
    display: flex;
    justify-content: center;
    flex: 1;
    position: relative;
  }

  .sys-chart__container {
    height: 100%;
    width: 160px;
  }

  .sys-chart__footer {
    display: flex;
    justify-content: center;
  }

  .sys-chart__empty {
    padding: 0;
    
    --el-empty-image-width: 60px;
  }
}
</style>