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
        <span class="copy-url" id="api-path" :data-clipboard-text="formData.path" @click="copyUrlEvent('api-path')">复制</span>
      </el-form-item>
      <el-form-item label="请求头" prop="headerConfig" :class="{ 'show-screen__full': reqHeaderFullStatus }">
        <!-- <el-icon class="modal-full-screen" @click="fullScreenEvent('reqHeaderFullStatus')"><FullScreen v-if="!reqHeaderFullStatus" /><Close v-else /></el-icon>
        <code-mirror v-model="formData.headerConfig" basic :lang="jsonLang"/> -->
        <span class="add-btn">
          <el-icon @click="addNewOption(formData.headerConfig)"><CirclePlus /></el-icon>
        </span>
        <div class="form-options__list">
          <div class="form-options__item" v-for="(element, index) in formData.headerConfig">
            <div class="input-item">
              <span class="item-label">键</span>
              <el-input v-model="element.label" placeholder="请输入"></el-input>
            </div>
            <div class="input-item">
              <span class="item-label">值</span>
              <el-input v-model="element.value" placeholder="请输入"></el-input>
            </div>
            <div class="option-btn">
              <el-icon v-if="formData.headerConfig.length > 1" class="remove" @click="removeItem(index, formData.headerConfig)"><CircleClose /></el-icon>
            </div>
          </div>
        </div>
      </el-form-item>
      <el-form-item v-if="formData.method === 'POST'" label="请求体" prop="bodyParams" :class="{ 'show-screen__full': reqBodyFullStatus }">
        <span class="format-json" @click="formatterJsonEvent(formData, 'bodyParams')">格式化JSON</span>
        <el-icon class="modal-full-screen" @click="fullScreenEvent('reqBodyFullStatus')"><FullScreen v-if="!reqBodyFullStatus" /><Close v-else /></el-icon>
        <code-mirror v-model="formData.bodyParams" basic :lang="jsonLang"/>
      </el-form-item>
      <el-form-item v-if="formData.method === 'GET'" label="请求体" prop="bodyConfig">
        <span class="add-btn">
          <el-icon @click="addNewOption(formData.bodyConfig)"><CirclePlus /></el-icon>
        </span>
        <div class="form-options__list">
          <div class="form-options__item" v-for="(element, index) in formData.bodyConfig">
            <div class="input-item">
              <span class="item-label">键</span>
              <el-input v-model="element.label" placeholder="请输入"></el-input>
            </div>
            <div class="input-item">
              <span class="item-label">值</span>
              <el-input v-model="element.value" placeholder="请输入"></el-input>
            </div>
            <div class="option-btn">
              <el-icon v-if="formData.bodyConfig.length > 1" class="remove" @click="removeItem(index, formData.bodyConfig)"><CircleClose /></el-icon>
            </div>
          </div>
        </div>
      </el-form-item>
      <!-- 响应体 状态码 -->
      <el-form-item class="resp-http-body" label="响应体" prop="returnConfig" :class="{ 'show-screen__full': respBodyFullStatus }">
        <span class="format-json" @click="formatterJsonEvent(formData, 'bodyParams')">格式化JSON</span>
        <span class="resp-http-status" :style="{ color: httpStatus == 500 ? 'red' : '' }">
          {{ httpStatus }}
        </span>
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
import { GetCustomApiDetailData, TestCustomApiData } from '@/services/custom-api.service'
import Clipboard from 'clipboard'

const authStore = useAuthStore()

const form = ref<FormInstance>()
const jsonLang = ref<any>(json())
const responseBodyRef = ref()

const reqHeaderFullStatus = ref(false)
const reqBodyFullStatus = ref(false)
const respBodyFullStatus = ref(false)
const httpStatus = ref()

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
  headerConfig: [],   // 请求头
  bodyConfig: [],     // 请求体
  bodyParams: null,
  returnConfig: null    // 返回体
})
const rules = reactive<FormRules>({
  // url: [{ required: true, message: '请输入自定义访问路径', trigger: [ 'blur', 'change' ]}],
  // headerConfig: [{ required: true, message: '请输入请求头设置', trigger: [ 'blur', 'change' ]}],
  // bodyConfig: [{ required: true, message: '请输入请求体设置', trigger: [ 'blur', 'change' ]}]
})

function showModal(data: any): void {
  modelConfig.visible = true
  formData.id = data.id
  getApiDetailData(data.id)
  if (data.path.slice(0,1) !== '/') {
    data.path = '/' + data.path
  }
  formData.path = `${location.origin}/${authStore.tenantId}/api${data.path}`
  formData.method = data.apiType
  formData.headerConfig = [{label: '', value: ''}]
  formData.bodyConfig = [{label: '', value: ''}]
  formData.bodyParams = null
  formData.returnConfig = null
  nextTick(() => {
    form.value?.resetFields()
  })
}

function getApiDetailData(id: string) {
  GetCustomApiDetailData({
    id: id
  }).then((res: any) => {
    console.log('res', res)
    formData.headerConfig = res.data.reqHeader.map((item: any) => {
      item.value = ''
      return {
        ...item
      }
    })
    const bodyJsonObj = JSON.parse(res.data.reqBody)
    const bodyConfigArr = []
    Object.keys(bodyJsonObj).forEach((key: string) => {
      bodyJsonObj[key] = ''
      bodyConfigArr.push({
        label: key,
        value: ''
      })
    })
    if (res.data.apiType === 'POST') {
      formData.bodyParams = jsonFormatter(JSON.stringify(bodyJsonObj))
    } else {
      formData.bodyConfig = bodyConfigArr
    }
    //  JSON.parse(formData.bodyParams)
  }).catch(() => {
  })
}

function okEvent() {
  form.value?.validate((valid) => {
    if (valid) {
      const headerParams: any = {}
      formData.headerConfig.forEach(item => {
        headerParams[item.label] = item.value
      })
      try {
        let bodyParams: any = {}
        if (formData.method === 'GET') {
          formData.bodyConfig.forEach(item => {
            bodyParams[item.label] = item.value
          })
        } else {
          bodyParams = JSON.parse(formData.bodyParams)
        }
        modelConfig.okConfig.loading = true
        TestCustomApiData({
          id: formData.id,
          headerParams: headerParams,
          requestBody: bodyParams
        }).then((res: any) => {
          ElMessage.success(res.msg)
          modelConfig.okConfig.loading = false
          httpStatus.value = res.data.httpStatus
          if (res.data.httpStatus === 200) {
            formData.returnConfig = jsonFormatter(JSON.stringify(res.data.body))
          } else {
            formData.returnConfig = res.data.msg
          }
        }).catch(() => {
          modelConfig.okConfig.loading = false
        })
      } catch (error) {
        console.warn('请求体输入有误', error)
        ElMessage.warning('请求体格式输入有误')
      }
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}

function closeEvent() {
  modelConfig.visible = false
}
function formatterJsonEvent(formData: any, key: string) {
  try {
    formData[key] = jsonFormatter(formData[key])
  } catch (error) {
    console.error('请检查输入的JSON格式是否正确', error)
    ElMessage.error('请检查输入的JSON格式是否正确')
  }
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

function addNewOption(data: any) {
  data.push({
    label: '',
    value: ''
  })
}
function removeItem(index: number, data: any) {
  data.splice(index, 1)
}

function copyUrlEvent(id: string) {
    let clipboard = new Clipboard('#' + id)
    clipboard.on('success', () => {
        ElMessage.success('复制成功')
        clipboard.destroy()
    })
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
    .format-json {
      position: absolute;
      top: -34px;
      right: 20px;
      font-size: 12px;
      color: getCssVar('color', 'primary');
      cursor: pointer;
      &:hover {
        text-decoration: underline;
      }
    }
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
      flex-wrap: nowrap;
      justify-content: space-between;

      .copy-url {
          min-width: 24px;
          font-size: 12px;
          margin-left: 10px;
          color: getCssVar('color', 'primary');
          cursor: pointer;
          &:hover {
              text-decoration: underline;
          }

      }
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
  .resp-http-body {
    position: relative;
    .resp-http-status {
      position: absolute;
      top: -35px;
      left: 62px;
      z-index: 10;
      font-size: 12px;
    }
  }
}
</style>
