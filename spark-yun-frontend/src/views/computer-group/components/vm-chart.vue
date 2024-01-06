<template>
  <div class="vm-chart">
    <div class="vm-chart__header">
      <span class="vm-chart__title">实例图</span>
      <div class="vm-chart__ops">
        <el-date-picker v-model="currentDate"></el-date-picker>
      </div>
    </div>
    <div class="vm-chart__body">
      <div ref="chartContainerRef" class="vm-chart__container"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
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


const currentDate = ref<string>(dayjs().format('YYYY-MM-DD'))
const chartContainerRef = ref<HTMLDivElement>()

const options: EChartsOption = {
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
    data: ['01:00', '02:00', '03:00', '04:00', '05:00', '06:00', '07:00', '08:00', '09:00', '10:00'],
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
      data: [10, 15, 20, 30, 20, 10, 10, 30, 20, 30]
    },
    {
      name: '失败',
      type: 'line',
      color: '#FA541C',
      data: [40, 35, 30, 30, 30, 20, 20, 10, 10, 20]
    },
    {
      name: '运行中',
      type: 'line',
      color: '#2A82E4',
      data: [30, 20, 30, 10, 10, 40, 40, 30, 30, 40]
    }
  ]
}

onMounted(() => {
  if (chartContainerRef.value) {
    var myChart = echarts.init(chartContainerRef.value)

    myChart.setOption(options)
  }
})

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
}
</style>