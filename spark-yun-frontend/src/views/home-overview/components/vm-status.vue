<template>
  <div :class="vmStatusClass">
    <div class="vm-status__pointer"></div>
    <span class="vm-status__text">{{ vmStatus.name }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { VmData } from './component'

const status = ref({
  SUCCESS: '成功',
  FAIL: '失败',
  ABORT: '已中止',
  ABORTING: '中止中',
  RUNNING: '运行中',
  PENDING: '等待中'
})

const props = withDefaults(defineProps<{
  status: VmData['status']
}>(), {
  status: 'SUCCESS'
})

const vmStatusClass = computed(() => {
  return [
    'vm-status',
    'is-' + props.status.toLowerCase()
  ]
})

const vmStatus = computed<{ status: VmData['status'], name: string }>(() => {
  return {
    status: props.status,
    name: status.value[props.status]
  }
})

</script>

<style scoped lang="scss">
.vm-status {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  line-height: 22px;
  &.is-success {
    color: #43CF7C;
    .vm-status__pointer {
      background-color: #43CF7C;
    }
  }
  &.is-fail {
    color: #FA541C;
    .vm-status__pointer {
      background-color: #FA541C;
    }
  }
  &.is-abort {
    color: #9f26e1;
    .vm-status__pointer {
      background-color: #9f26e1;
    }
  }
  &.is-aborting {
    color: #b2b2b2;
    .vm-status__pointer {
      background-color: #b2b2b2;
    }
  }
  &.is-running {
    color: #1890ff;
    .vm-status__pointer {
      background-color: #1890ff;
    }
  }
  &.is-pending {
    color: #F5B041;
    .vm-status__pointer {
      background-color: #F5B041;
    }
  }

  &__pointer {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    margin-right: 4px;
  }
}
</style>