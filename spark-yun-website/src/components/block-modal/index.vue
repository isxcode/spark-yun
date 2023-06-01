<template>
  <el-dialog v-model="visible" :title="modelConfig.title" :width="modelConfig.width" :class="modelConfig.customClass + ' zqy-block-modal'" :close-on-press-escape="false" :close-on-click-modal="false" :append-to-body="true" :destroy-on-close="true" :center="true" @close="close">
    <div class="modal-content">
      <slot />
    </div>
    <template v-if="!modelConfig.footerHidden" #footer>
      <slot name="customLeft" />
      <el-button v-if="modelConfig.cancelConfig" :disabled="modelConfig.cancelConfig.disabled || false" @click="clickToCancel">
        {{ modelConfig.cancelConfig.title }}
      </el-button>
      <el-button v-if="modelConfig.okConfig" type="primary" :loading="modelConfig.okConfig.loading" :disabled="modelConfig.okConfig.disabled || false" @click="clickToSave">
        {{ modelConfig.okConfig.title }}
      </el-button>
    </template>
  </el-dialog>
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

interface ModalConfig {
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
  modelConfig: ModalConfig;
}>();

watch(
  () => props.modelConfig.visible,
  (newVal) => {
    visible.value = newVal;
  }
);

function clickToSave() {
  props.modelConfig.okConfig.ok();
}

function clickToCancel() {
  props.modelConfig.cancelConfig.cancel();
}

function close() {
  props.modelConfig.cancelConfig.cancel();
}
</script>

<style lang="scss">
.zqy-block-modal {
  overflow: unset !important;
  border-radius: 2px;
  .el-dialog__header {
    padding: 12px !important;
    width: 100%;
    box-sizing: border-box;
    cursor: default;
    border-bottom: 1px solid #ebeef5;
    .el-dialog__headerbtn {
      height: 50px;
      top: 0;
    }
    .el-dialog__title {
      font-size: 16px;
      color: $--app-base-font-color;
    }
  }
  .el-dialog__body {
    overflow: auto;
    max-height: calc(100vh - 96px);
    padding: 0 !important;
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
  .el-dialog__footer {
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
