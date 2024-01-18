<template>
  <div class="sys-info">
    <div class="sys-info__header">
      <span class="sys-info__title">系统信息</span>
      <div class="sys-info__ops">
        <el-icon class="sys-info__icon"><RefreshRight /></el-icon>
        <el-icon class="sys-info__icon"><Setting /></el-icon>
      </div>
    </div>
    <div class="sys-info__body">
      <template v-for="sysInfo in sysInfoData" :key="sysInfo.type">
        <sys-chart :chart-data="sysInfo"></sys-chart>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import SysChart from './sys-chart.vue'
import { ChartInfo } from './component'
import { ref } from 'vue'

const sysInfoData = ref<Array<ChartInfo>>([
  {
    type: 'computer-group',
    title: '计算机群',
    mix: 75,
    color: '#ED7B09'
  },
  {
    type: 'data-source',
    title: '数据源',
    mix: 35,
    color: '#1967BF'
  },
  {
    type: 'publish-job',
    title: '发布作业',
    mix: 65,
    color: '#03A89D'
  },
  {
    type: 'publish-interface',
    title: '发布接口',
    mix: 99,
    color: '#4B19BF'
  }
])

</script>

<style lang="scss">
.sys-info {
  margin-bottom: 24px;
  .sys-info__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 40px;
  }

  .sys-info__body {
    margin-top: 24px;
    display: flex;
    justify-content: space-between;

    .sys-chart {
      margin-right: 24px;
      &:last-child {
        margin-right: 0;
      }
    }
  }

  .sys-info__title {
    font-size: getCssVar('font-size', 'medium');
    font-weight: bold;
  }

  .sys-info__icon {
    margin-right: 12px;
    cursor: pointer;

    &:hover {
      color: getCssVar('color', 'primary');
    }

    &:last-child {
      margin-right: 0;
    }
  }
}
</style>