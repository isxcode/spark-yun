<template>
  <BlockModal :model-config="modelConfig">
    <el-upload class="license-upload" action="" :limit="1" :multiple="false" :drag="true" :auto-upload="false" :on-change="handleChange">
      <el-icon class="el-icon--upload">
        <upload-filled />
      </el-icon>
      <div class="el-upload__text">上传企业许可证 <em>点击上传</em></div>
    </el-upload>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref } from "vue";
import BlockModal from "@/components/block-modal/index.vue";
import { ElMessage } from "element-plus";

const callback = ref<any>();
const fileData = ref(null);
const modelConfig = reactive({
  title: "上传证书",
  visible: false,
  width: "520px",
  okConfig: {
    title: "确定",
    ok: okEvent,
    disabled: false,
    loading: false,
  },
  cancelConfig: {
    title: "取消",
    cancel: closeEvent,
    disabled: false,
  },
  needScale: false,
  zIndex: 1100,
  closeOnClickModal: false,
});

function showModal(cb: () => void): void {
  callback.value = cb;
  modelConfig.visible = true;
}

function okEvent() {
  modelConfig.okConfig.loading = true;
  callback
    .value(fileData.value)
    .then((res: any) => {
      modelConfig.okConfig.loading = false;
      if (res === undefined) {
        modelConfig.visible = false;
      } else {
        modelConfig.visible = true;
      }
    })
    .catch((err: any) => {
      modelConfig.okConfig.loading = false;
      ElMessage.error(err);
    });
}

function closeEvent() {
  modelConfig.visible = false;
}

function handleChange(e: any) {
  fileData.value = e.raw;
}

defineExpose({
  showModal,
});
</script>

<style lang="scss">
.license-upload {
  margin: 20px;
  .el-upload {
    .el-upload-dragger {
      border-radius: $--app-border-radius;
      .el-upload__text {
        font-size: $--app-small-font-size;
      }
    }
  }
}
</style>
