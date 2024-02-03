<template>
    <div class="z-share-form">
        <Header />
        <LoadingPage :visible="loading" :network-error="networkError" @loading-refresh="getFormConfigById">
          <div class="share-form-button">
            <el-button :disabled="renderSence === 'readonly'" :loading="saveLoading" type="primary" @click="saveData">保存</el-button>
          </div>
          <div class="share-form-container">
              <z-form-engine
                  ref="formEngineRef"
                  v-model="formData"
                  :renderSence="renderSence"
                  :formConfigList="formConfigList"
              ></z-form-engine>
          </div>
        </LoadingPage>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, watch, reactive } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import { useRoute } from 'vue-router'
import Header from '@/layout/header/index.vue'
import ZFormEngine from '@/lib/packages/z-form-engine/index.vue'
import { ElMessage } from 'element-plus'
import { AddFormData, GetFormLinkInfoConfig, ShareFormGetCustomToken, ShareFormGetFormConfig } from '@/services/custom-form.service'

interface baseParam {
  formId: string
  formVersion: string
  tenantId: string
  formToken: string
}

const route = useRoute()

const loading = ref(false)
const networkError = ref(false)
const breadCrumbList = ref([
    {
        name: '分享表单',
        code: 'share-form'
    }
])
const renderSence = ref('edit')
const formEngineRef = ref()
const formConfigList = ref([])
const saveLoading = ref(false)

const shareLinkId = ref('')
const shareFormConfig = ref<baseParam>({
  formId: '',
  formVersion: '',
  tenantId: '',
  formToken: ''
})
const token = ref('')

const formData = reactive({})

// watch(() => route.params, (e) => {
//     console.log('路由', e)
// }, {
//     immediate: true,
//     deep: true
// })
// function getToken(tableLoading?: boolean) {
//   loading.value = tableLoading ? false : true
//   networkError.value = networkError.value || false
//   ShareFormGetCustomToken({
//     validDay: 1
//   }).then((res: any) => {
//     token.value = res.data.token
//     getFormConfigById()
//   }).catch(() => {
//     loading.value = false
//     networkError.value = true
//   })
// }

function getFormConfigById(tableLoading?: boolean) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  ShareFormGetFormConfig({
    formId: shareFormConfig.value.formId
  }, {
    authorization: shareFormConfig.value.formToken,
    tenant: shareFormConfig.value.tenantId
  }).then((res: any) => {
    formConfigList.value = res.data?.components
    loading.value = false
  }).catch(() => {
    loading.value = false
    networkError.value = true
  })
}

function saveData() {
  formEngineRef.value.validateForm((valid: boolean) => {
    if (valid) {
      saveLoading.value = true
      AddFormData({
        formId: shareFormConfig.value.formId,
        formVersion: shareFormConfig.value.formVersion,
        data: formData
      }, {
        authorization: shareFormConfig.value.formToken,
        tenant: shareFormConfig.value.tenantId
      }).then((res: any) => {
        saveLoading.value = false
        ElMessage.success(res.msg)
        renderSence.value = 'readonly'
      }).catch((error: any) => {
        saveLoading.value = false
      })
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}

function getlinkParams() {
  return new Promise((resolve, reject) => {
    loading.value = true
    GetFormLinkInfoConfig({
      formLinkId: shareLinkId.value
    }).then((res: any) => {
      shareFormConfig.value = res.data
      resolve()
    }).catch((error: any) => {
      loading.value = false
      reject(error)
    })
  })
}

onMounted(() => {
  const params = route.params.shareParam
  if (params) {
    shareLinkId.value = params
    console.log('shareFormConfig.value', shareLinkId.value)
    getlinkParams().then(() => {
      getFormConfigById()
    })
  }
})
</script>

<style lang="scss">
.z-share-form {
    width: 100%;
    background-color: #ffffff;
    height: 100vh;
    overflow: auto;
    position: relative;
    .zqy-breadcrumb {
        box-shadow: getCssVar('box-shadow', 'lighter');
    }
    .share-form-button {
      position: absolute;
      top: 0;
      right: 0;
      height: 60px;
      display: flex;
      align-items: center;
      z-index: 100;
      padding-right: 20px;
      box-sizing: border-box;
    }
    .share-form-container {
        // padding: 0 8%;
        box-sizing: border-box;
        height: 100%;
        max-height: calc(100vh - 60px);
        position: absolute;
        top: 60px;
        left: 0;
        width: 100%;

        .zqy-form-engine {
            .form-components {
                .el-scrollbar {
                    .el-scrollbar__view {
                        padding: 0 10%;
                    }
                }
            }
        }
    }
}
</style>
