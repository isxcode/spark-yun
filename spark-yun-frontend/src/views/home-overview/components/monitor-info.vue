<template>
  <div class="monitor-info">
    <div class="monitor-info__header">
      <el-dropdown class="monitor-info__dropdown" trigger="click" @command="handleColonyChange">
        <span class="monitor-info__active">
          {{ currentColony?.name }}
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <template v-for="colony in colonyList" :key="colony.id">
              <el-dropdown-item :command="colony.id">{{ colony.name }}</el-dropdown-item>
            </template>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <div class="monitor-info__ops">
        <el-dropdown class="monitor-info__dropdown" trigger="click" @command="handleFrequencyChange">
        <span class="monitor-info__active">
          {{ currentFrequency?.name }}
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <template v-for="frequency in frequencyList" :key="frequency.value">
              <el-dropdown-item :command="frequency.value">{{ frequency.name }}</el-dropdown-item>
            </template>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
        <el-icon class="sys-info__icon" @click="queryMonitorData"><RefreshRight /></el-icon>
        <!-- <el-icon class="sys-info__icon"><Setting /></el-icon> -->
      </div>
    </div>
    <div class="monitor-info__body">
      <template v-for="monitorData in monitorDataList" :key="monitorData.type">
        <monitor-chart :monitorData="monitorData" :dateTimeList="dateTimeList" @showDetailEvent="showDetailEvent(monitorData, dateTimeList)"></monitor-chart>
      </template>
    </div>
    
    <PreviewReport ref="previewReportRef"></PreviewReport>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { useColony } from './hooks/useColony'
import { useFrequency } from './hooks/useFrequency'
import { useMonitor } from './hooks/useMonitor'
import { MonitorInfo } from './hooks/useMonitor'
import MonitorChart from './monitor-chart.vue'
import PreviewReport from './preview-report/index.vue'

const { currentColony, colonyList, onColonyChange, queryColonyData } = useColony()
const { currentFrequency, frequencyList, onFrequencyChange } = useFrequency()
const { monitorDataList, dateTimeList, queryMonitorData } = useMonitor(currentColony, currentFrequency)

const previewReportRef = ref<any>()

function handleColonyChange(colonyId: string) {
  onColonyChange(colonyId)
  queryMonitorData()
}

function handleFrequencyChange(frequency: string) {
  onFrequencyChange(frequency)
  queryMonitorData()
}

function showDetailEvent(monitorData: MonitorInfo, dateTimeList: string[]) {
  previewReportRef.value.showModal(monitorData, dateTimeList)
}

onMounted(async () => {
  await queryColonyData()
  await queryMonitorData()
})
</script>

<style lang="scss">
.monitor-info {
  margin-bottom: 24px;

  .monitor-info__active {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 12px;
    cursor: pointer;
  }

  .monitor-info__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 40px;
  }
  
  .monitor-info__ops { 
    display: flex;
    align-items: center;
  }
}
</style>