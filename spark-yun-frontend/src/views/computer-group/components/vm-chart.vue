<template>
  <div class="vm-chart">
    <div class="vm-chart__header">
      <span class="vm-chart__title">实例图</span>
      <div class="vm-chart__ops">
        <el-date-picker v-model="currentDate" @change="queryVmChartData"></el-date-picker>
      </div>
    </div>
    <div class="vm-chart__body">
      <div ref="chartContainerRef" class="vm-chart__container"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import dayjs from 'dayjs'
import * as echarts from 'echarts/core';
import {
  TooltipComponent,
  TooltipComponentOption,
  GridComponent,
  GridComponentOption,
  LegendComponent,
  LegendComponentOption
} from 'echarts/components';
import { LineChart, LineSeriesOption } from 'echarts/charts';
import { UniversalTransition } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';
import { queryVmChartInfo } from '@/views/computer-group/services/computer-group';

echarts.use([
  TooltipComponent,
  GridComponent,
  LegendComponent,
  LineChart,
  CanvasRenderer,
  UniversalTransition
])

type EChartsOption = echarts.ComposeOption<
  | TooltipComponentOption
  | GridComponentOption
  | LegendComponentOption
  | LineSeriesOption
>

const chartVm = ref<echarts.ECharts>()

const currentDate = ref<string>(dayjs().format('YYYY-MM-DD'))
const chartContainerRef = ref<HTMLDivElement>()

const chartSuccessData = ref<number[]>([])
const chartRunningData = ref<number[]>([])
const chartFailData = ref<number[]>([])
const chartXAxisData = ref<string[]>([])

const options = computed<EChartsOption>(() => {
  return {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      top: '6%',
      right: '4%',
      data: ['成功', '失败', '运行中']
    },
    grid: {
      left: '4%',
      right: '4%',
      bottom: '16%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      offset: 16,
      data: chartXAxisData.value,
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '成功',
        type: 'line',
        color: '#43CF7C',
        data: chartSuccessData.value
      },
      {
        name: '失败',
        type: 'line',
        color: '#FA541C',
        data: chartFailData.value
      },
      {
        name: '运行中',
        type: 'line',
        color: '#2A82E4',
        data: chartRunningData.value
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

  queryVmChartData()
})

function queryVmChartData() {
  queryVmChartInfo({
    localDate: currentDate.value
  }).then(({ data }) => {
    if (data.instanceNumLine) {
      let successData: number[] = []
      let runningData: number[] = []
      let failData: number[] = []
      let localTime: string[] = []
      data.instanceNumLine.forEach(vm => {
        successData.push(vm.successNum)
        runningData.push(vm.runningNum)
        failData.push(vm.failNum)
        localTime.push(vm.localTime)
      })
      chartSuccessData.value = successData
      chartRunningData.value = runningData
      chartFailData.value = failData
      chartXAxisData.value = localTime
    }
  })
}

</script>

<style lang="scss">
.vm-chart {
  margin-bottom: 24px;
  .vm-chart__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 40px;
  }

  .vm-chart__title {
    font-size: getCssVar('font-size', 'medium');
    font-weight: bold;
  }

  .vm-chart__body {
    height: 280px;
    margin-top: 24px;
    border-radius: 8px;
    background-color: getCssVar('color', 'white');
    box-shadow: getCssVar('box-shadow', 'lighter');

    .vm-chart__container {
      width: 100%;
      height: 100%;
    }
  }

  .vm-chart__ops {
    .el-date-editor {
      --el-date-editor-width: 160px
    }
  }
}
</style>