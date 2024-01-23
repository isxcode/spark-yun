<template>
  <BlockModal :model-config="modelConfig">
    <el-form
      ref="form"
      class="custom-api-form"
      label-position="top"
      :model="formData"
      :rules="rules"
    >
      <el-form-item label="请求路径">
        <el-input
          v-model="formData.path"
          maxlength="1000"
          placeholder="请输入"
          :disabled="true"
        />
      </el-form-item>
      <el-form-item label="请求头" prop="headerConfig" :class="{ 'show-screen__full': reqHeaderFullStatus }">
        <el-icon class="modal-full-screen" @click="fullScreenEvent('reqHeaderFullStatus')"><FullScreen v-if="!reqHeaderFullStatus" /><Close v-else /></el-icon>
        <code-mirror v-model="formData.headerConfig" basic :lang="jsonLang"/>
      </el-form-item>
      <el-form-item label="请求体" prop="bodyConfig" :class="{ 'show-screen__full': reqBodyFullStatus }">
        <el-icon class="modal-full-screen" @click="fullScreenEvent('reqBodyFullStatus')"><FullScreen v-if="!reqBodyFullStatus" /><Close v-else /></el-icon>
        <code-mirror v-model="formData.bodyConfig" basic :lang="jsonLang"/>
      </el-form-item>
      <el-form-item label="响应体" prop="returnConfig" :class="{ 'show-screen__full': respBodyFullStatus }">
        <el-icon class="modal-full-screen" @click="fullScreenEvent('respBodyFullStatus')">
          <FullScreen v-if="!respBodyFullStatus" />
          <Close v-else />
        </el-icon>
        <code-mirror ref="responseBodyRef" v-model="formData.returnConfig" :disabled="true" basic :lang="jsonLang"/>
      </el-form-item>
    </el-form>
  </BlockModal>
</template>
  
<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import CodeMirror from 'vue-codemirror6'
import {json} from '@codemirror/lang-json'
import { jsonFormatter } from '@/utils/formatter'
import { useAuthStore } from '@/store/useAuth'
import { TestCustomApiData } from '@/services/custom-api.service'

const authStore = useAuthStore()

const form = ref<FormInstance>()
const jsonLang = ref<any>(json())
const responseBodyRef = ref()

const reqHeaderFullStatus = ref(false)
const reqBodyFullStatus = ref(false)
const respBodyFullStatus = ref(false)

const modelConfig = reactive({
  title: '测试接口',
  visible: false,
  width: '60%',
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
const formData = reactive({
  id: '',
  path: '',              // 自定义访问路径
  method: '',
  headerConfig: null,   // 请求头
  bodyConfig: null,     // 请求体
  returnConfig: null    // 返回体
})
const rules = reactive<FormRules>({
  // url: [{ required: true, message: '请输入自定义访问路径', trigger: [ 'blur', 'change' ]}],
  headerConfig: [{ required: true, message: '请输入请求头设置', trigger: [ 'blur', 'change' ]}],
  bodyConfig: [{ required: true, message: '请输入请求体设置', trigger: [ 'blur', 'change' ]}]
})

function showModal(data: any): void {
  modelConfig.visible = true
  formData.id = data.id
  formData.path = data.path
  formData.method = data.apiType
  formData.headerConfig = null
  formData.bodyConfig = null
  formData.returnConfig = null
  nextTick(() => {
    form.value?.resetFields()
  })
}


function okEvent() {
  form.value?.validate((valid) => {
    if (valid) {
      let headerParams: any
      try {
        headerParams = JSON.parse(formData.headerConfig)
        modelConfig.okConfig.loading = true
        TestCustomApiData({
          id: formData.id,
          headerParams: headerParams,
          requestBody: formData.bodyConfig
        }).then((res: any) => {
          ElMessage.success(res.msg)
          modelConfig.okConfig.loading = false
          if (res.data.httpStatus === 200) {
            formData.returnConfig = jsonFormatter(JSON.stringify(res.data.body))
          } else {
            formData.returnConfig = res.data.msg
          }
        }).catch(() => {
          modelConfig.okConfig.loading = false
        })
      } catch (error) {
        ElMessage.error('请检查请求头是否输入正确')
      }
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}

function closeEvent() {
  modelConfig.visible = false
}

function fullScreenEvent(type: string) {
  if (type === 'reqHeaderFullStatus') {
    reqHeaderFullStatus.value = !reqHeaderFullStatus.value
  } else if (type === 'reqBodyFullStatus') {
    reqBodyFullStatus.value = !reqBodyFullStatus.value
  } else {
    respBodyFullStatus.value = !respBodyFullStatus.value
  }
}

defineExpose({
  showModal
})
</script>

<style lang="scss">
.custom-api-form {
  box-sizing: border-box;
  padding: 12px 20px 0 20px;
  width: 100%;

  .api-item {
    .item-title {
      font-size: 14px;
      padding-bottom: 12px;
      margin-bottom: 12px;
      box-sizing: border-box;
      border-bottom: 1px solid #ebeef5;
      font-weight: bolder;
      color: getCssVar('color', 'primary');
    }
  }
  .el-form-item {
    &.show-screen__full {
      position: fixed;
      width: 100%;
      height: 100%;
      top: 0;
      left: 0;
      background-color: #ffffff;
      padding: 12px 20px;
      box-sizing: border-box;
      transition: all 0.15s linear;
      z-index: 10;
      .el-form-item__content {
        align-items: flex-start;
        height: 100%;
        .vue-codemirror {
          height: calc(100% - 36px);
        }
      }
    }
    .el-form-item__content {
      position: relative;
      .modal-full-screen {
        position: absolute;
        top: -26px;
        right: 0;
        cursor: pointer;
        &:hover {
          color: getCssVar('color', 'primary');;
        }
      }
      .vue-codemirror {
        height: 130px;
        width: 100%;

        .cm-editor {
          height: 100%;
          outline: none;
          border: 1px solid #dcdfe6;
        }

        .cm-gutters {
          font-size: 12px;
          font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
        }

        .cm-content {
          font-size: 12px;
          font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
        }

        .cm-tooltip-autocomplete {
          ul {
            li {
              height: 40px;
              display: flex;
              align-items: center;
              font-size: 12px;
              background-color: #ffffff;
              font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
            }

            li[aria-selected] {
              background: #409EFF;
            }

            .cm-completionIcon {
              margin-right: -4px;
              opacity: 0;
            }
          }
        }
      }
    }
  }
}
</style>
