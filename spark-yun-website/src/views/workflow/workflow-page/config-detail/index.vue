<template>
    <BlockDrawer :drawer-config="drawerConfig">
    </BlockDrawer>
</template>

<script lang="ts" setup>
import BlockDrawer from '@/components/block-drawer/index.vue'
import { reactive } from 'vue'
import { FormInstance, FormRules } from 'element-plus'

// const form = ref<FormInstance>()
// const callback = ref<any>()
const drawerConfig = reactive({
  title: '配置',
  visible: false,
  width: '500',
  okConfig: {
    title: '确定',
    ok: okEvent,
    disabled: false,
    loading: false
  },
  cancelConfig: {
    title: '取消',
    cancel: closeEvent,
    disabled: false
  },
  zIndex: 1100,
  closeOnClickModal: false
})

function showModal() {
  drawerConfig.visible = true
}

function okEvent() {
  drawerConfig.visible = false
}

function closeEvent() {
  drawerConfig.visible = false
}

defineExpose({
    showModal
})
</script>