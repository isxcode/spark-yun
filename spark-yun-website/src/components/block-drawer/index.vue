<template>
  <el-drawer v-model="visible" :close-on-press-escape="false" :close-on-click-modal="false" :append-to-body="true" :destroy-on-close="true" :title="drawerConfig.title" :size="drawerConfig.width || '40%'" :class="drawerConfig.customClass + ' zqy-block-drawer'" @close="close">
    <template #default>
      <slot />
    </template>
    <template #footer>
      <slot name="customLeft" />
      <el-button v-if="drawerConfig.cancelConfig" :disabled="drawerConfig.cancelConfig.disabled || false" @click="clickToCancel">
        {{ drawerConfig.cancelConfig.title }}
      </el-button>
      <el-button v-if="drawerConfig.okConfig" type="primary" :loading="drawerConfig.okConfig.loading" :disabled="drawerConfig.okConfig.disabled || false" @click="clickToSave">
        {{ drawerConfig.okConfig.title }}
      </el-button>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import { defineProps, ref, watch } from "vue";

interface BtnConfig {
  title: string;
  disabled: boolean;
  loading: boolean;
}

interface OkBtnConfig extends BtnConfig {
  ok: () => void;
}

interface CancelBtnConfig extends BtnConfig {
  cancel: () => void;
}

interface DrawerConfig {
  title: string;
  visible: boolean;
  width: string | number;
  okConfig: OkBtnConfig;
  cancelConfig: CancelBtnConfig;
  zIndex?: number;
  customClass?: string;
  footerHidden?: boolean;
}

const visible = ref(false);

const props = defineProps<{
  drawerConfig: DrawerConfig;
}>();

watch(
  () => props.drawerConfig.visible,
  (newVal) => {
    visible.value = newVal;
  }
);

function clickToSave() {
  props.drawerConfig.okConfig.ok();
}

function clickToCancel() {
  props.drawerConfig.cancelConfig.cancel();
}

function close() {
  props.drawerConfig.cancelConfig.cancel();
}
</script>

<style lang="scss">
.zqy-block-drawer {
  overflow: unset !important;
  border-radius: 2px;
  .el-drawer__header {
    padding: 12px !important;
    width: 100%;
    box-sizing: border-box;
    cursor: default;
    border-bottom: 1px solid #ebeef5;
    margin: 0;
    .el-drawer__headerbtn {
      height: 50px;
      top: 0;
    }
    .el-drawer__title {
      font-size: 16px;
      color: $--app-base-font-color;
    }
  }
  .el-drawer__body {
    overflow: auto;
    max-height: calc(100vh - 96px);
    padding: 0;
  }
  .custom-header-btn {
    position: absolute;
    top: 13px;
    right: 44px;
    .scale-all-screen {
      cursor: pointer;
      &:hover {
        color: #005bac;
      }
    }
    .scale-exist-screen {
      height: 24px;
      width: 24px;
      cursor: pointer;
      position: absolute;
      right: -6px;
      top: -3px;
      &:hover {
        color: #005bac;
      }
    }
  }
  .el-drawer__footer {
    border-top: 1px solid #ebeef5;
    box-shadow: unset !important;
    border-radius: 0 0 4px 4px;
    padding: 12px 20px;
    display: flex;
    justify-content: flex-end;
    > span {
      display: flex;
      align-items: center;
    }
  }
}
</style>
