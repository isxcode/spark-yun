<template>
    <BlockModal :model-config="modelConfig">
        <LoadingPage :visible="loading">
            <z-form-engine
                ref="formEngineRef"
                v-model="formData"
                renderSence="new"
                :formConfigList="formConfigList"
            ></z-form-engine>
        </LoadingPage>
    </BlockModal>
</template>
  
<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import LoadingPage from '@/components/loading/index.vue'
import ZFormEngine from '@/lib/packages/z-form-engine/index.vue'
import { ElMessage } from 'element-plus'
import { QueryFormConfigById } from '@/services/custom-form.service';
import { useRoute } from 'vue-router'

const route = useRoute()

const callback = ref<any>()
const formEngineRef = ref()
const formData = ref({})
const formConfigList = ref([])

const loading = ref(false)
const networkError = ref(false)

const modelConfig = reactive({
    title: '添加',
    visible: false,
    width: '80%',
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
    customClass: 'custom-form-add',
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})

function showModal(cb: () => void, data?: any): void {
    callback.value = cb
    formData.value = {}
    getFormConfigById()
    if (data) {
        formData.value = {
            ...data
        }
      modelConfig.title = '编辑'
    } else {
      modelConfig.title = '添加'
    }
    modelConfig.visible = true
}

function getFormConfigById(tableLoading?: boolean) {
    loading.value = tableLoading ? false : true
    networkError.value = networkError.value || false
    QueryFormConfigById({
        formId: route.query.id
    }).then((res: any) => {
        formConfigList.value = res.data?.components
        loading.value = false
        networkError.value = false
    }).catch(() => {
        loading.value = false
        networkError.value = true
    })
}

function okEvent() {
  formEngineRef.value.validateForm((valid: boolean) => {
    if (valid) {
        modelConfig.okConfig.loading = true
        callback.value(formData.value).then((res: any) => {
            modelConfig.okConfig.loading = false
            if (res === undefined) {
                modelConfig.visible = false
            } else {
                modelConfig.visible = true
            }
        })
        .catch(() => {
            modelConfig.okConfig.loading = false
        })
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}

function closeEvent() {
    modelConfig.visible = false
}

defineExpose({
    showModal
})
</script>
  
<style lang="scss">
.custom-form-add {
    .zqy-loading {
        height: calc(60vh - 20px);
    }
}
</style>
  