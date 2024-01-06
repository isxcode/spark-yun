<template>
  <div class="computer-group">
    <div class="computer-group__left">
      <sys-info></sys-info>
      <vm-chart></vm-chart>
      <vm-list></vm-list>
    </div>
    <div class="computer-group__right">
      <monitor-info></monitor-info>
    </div>
  </div>
</template>

<script lang="ts" setup>
import VmChart from './components/vm-chart.vue'
import VmList from './components/vm-list.vue'
import MonitorInfo from './components/monitor-info.vue'
import SysInfo from './components/sys-info.vue'


</script>

<style lang="scss">
.computer-group {
  display: flex;
  padding: 24px;
  overflow-x: hidden;
  overflow-y: auto;
  box-sizing: border-box;
  height: 100%;

  &__left {
    flex: 1;
    margin-right: 32px;
  }

  &__right {
    width: 340px;
  }
}
</style>