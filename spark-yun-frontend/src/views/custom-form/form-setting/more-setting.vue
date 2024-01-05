<template>
    <BlockModal :model-config="modelConfig">
        <div class="form-setting-more">
            <div class="item-title">分享设置</div>
            <div class="set-item-container">
                <div class="set-item">
                    <span class="label">匿名分享</span>
                    <el-switch v-model="shareConfig.nmShareStatus" />
                </div>
                <div class="set-item">
                    <span class="label">列表分享</span>
                    <el-switch v-model="shareConfig.listShareStatus" />
                </div>
                <div class="set-item">
                    <span class="label">登录分享</span>
                    <el-switch v-model="shareConfig.loginShareStatus" />
                </div>
            </div>
            <!-- <div class="item-title">权限设置</div>
            <div class="set-item-container">
                暂无
            </div> -->
        </div>
    </BlockModal>
</template>
  
<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'

const callback = ref()
const modelConfig = reactive({
    title: '高级设置',
    visible: false,
    width: '520px',
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
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})
const shareConfig = reactive({
    nmShareStatus: false,
    listShareStatus: false,
    loginShareStatus: false
})

function showModal(cb: () => void, data?: any): void {
    callback.value = cb
    modelConfig.visible = true
}

function okEvent() {
    // form.value?.validate((valid) => {
    //     if (valid) {
    //         modelConfig.okConfig.loading = true
    //         callback
    //             .value({
    //                 ...formData
    //             })
    //             .then((res: any) => {
    //                 modelConfig.okConfig.loading = false
    //                 if (res === undefined) {
    //                     modelConfig.visible = false
    //                 } else {
    //                     modelConfig.visible = true
    //                 }
    //             })
    //             .catch(() => {
    //                 modelConfig.okConfig.loading = false
    //             })
    //     } else {
    //         ElMessage.warning('请将表单输入完整')
    //     }
    // })
}

function closeEvent() {
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>
  
<style lang="scss">
.form-setting-more {
    padding: 12px 20px 0 20px;
    box-sizing: border-box;
    .item-title {
      font-size: 13px;
      padding-bottom: 12px;
      margin-bottom: 12px;
      box-sizing: border-box;
      border-bottom: 1px solid #ebeef5;
      font-weight: bolder;
      color: getCssVar('color', 'primary');
    }
    .set-item-container {
        margin-bottom: 12px;
        .set-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            .label {
                font-size: 12px;
            }
        }
    }
}
</style>
  