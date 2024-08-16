<template>
  <BlockModal :model-config="modelConfig">
    <el-form ref="form" class="add-computer-group message-check-form" label-position="top" :model="formData"
      :rules="rules">
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" maxlength="200" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="类型" prop="alarmType">
        <el-select :disabled="!!formData.id" v-model="formData.alarmType" placeholder="请选择">
          <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="告警事件" prop="alarmEvent">
        <el-select v-model="formData.alarmEvent" placeholder="请选择">
          <el-option v-for="item in eventList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="消息通知" prop="msgId">
        <el-select v-model="formData.msgId" placeholder="请选择">
          <el-option v-for="item in messageNotiList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="通知人" prop="receiverList">
        <el-select multiple collapse-tags collapse-tags-tooltip v-model="formData.receiverList" placeholder="请选择">
          <el-option v-for="item in userList" :key="item.userId" :label="item.username" :value="item.userId" />
        </el-select>
      </el-form-item>
      <el-form-item label="通知内容" prop="alarmTemplate">
        <span class="format-json" @click="formatterJsonEvent(formData, 'alarmTemplate')">格式化JSON</span>
        <el-icon class="modal-full-screen" @click="fullScreenEvent()">
          <FullScreen v-if="!fullStatus" />
          <Close v-else />
        </el-icon>
        <code-mirror ref="responseBodyRef" v-model="formData.alarmTemplate" basic :lang="jsonLang" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="formData.remark" type="textarea" maxlength="200" :autosize="{ minRows: 4, maxRows: 4 }"
          placeholder="请输入" />
      </el-form-item>
    </el-form>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
// import CodeMirror from 'vue-codemirror6'
import { json } from '@codemirror/lang-json'
import { jsonFormatter } from '@/utils/formatter'
import { GetUserList } from '@/services/tenant-user.service'
import { GetMessagePagesList } from '@/services/message-center.service'

const form = ref<FormInstance>()
const jsonLang = ref<any>(json())
const callback = ref<any>()
const fullStatus = ref(false)
const userList = ref([])
const typeList = ref([
  {
    label: '作业',
    value: 'WORK'
  },
  {
    label: '作业流',
    value: 'WORKFLOW'
  }
])
const eventList = ref([
  {
    label: '开始运行',
    value: 'START_RUN'
  },
  {
    label: '运行结束',
    value: 'RUN_END'
  },
  {
    label: '运行成功',
    value: 'RUN_SUCCESS'
  },
  {
    label: '运行失败',
    value: 'RUN_FAIL'
  }
])
const messageNotiList = ref([])
const modelConfig = reactive({
  title: '测试',
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
const formData = reactive({
  name: '',
  alarmType: '',    // 类型
  alarmEvent: '',   // 告警事件
  msgId: '',        // 消息通知
  receiverList: [], // 通知人
  alarmTemplate: '',// 通知内容
  remark: '',
  id: ''
})
const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入名称', trigger: ['blur', 'change'] }],
  alarmType: [{ required: true, message: '请选择类型', trigger: ['blur', 'change'] }],
  alarmEvent: [{ required: true, message: '请选择告警事件', trigger: ['blur', 'change'] }],
  msgId: [{ required: true, message: '请选择消息通知', trigger: ['blur', 'change'] }],
  receiverList: [{ required: true, message: '请选择通知人', trigger: ['blur', 'change'] }],
  alarmTemplate: [{ required: true, message: '请输入通知内容', trigger: ['blur', 'change'] }],
})

function showModal(cb: () => void, data: any): void {
  getUserList()
  getMessageList()
  if (data) {
    Object.keys(formData).forEach((key: string) => {
      if (key == 'receiverList') {
        formData[key] = JSON.parse(data[key])
      } else {
        formData[key] = data[key]
      }
    })
    modelConfig.title = '编辑'
  } else {
    formData.name = ''
    formData.alarmType = ''
    formData.alarmEvent = ''
    formData.msgId = ''
    formData.receiverList = []
    formData.alarmTemplate = ''
    formData.remark = ''
    formData.id = ''
    modelConfig.title = '新增'
  }

  callback.value = cb
  modelConfig.visible = true
}

function okEvent() {
  form.value?.validate((valid) => {
    if (valid) {
      modelConfig.okConfig.loading = true
      callback.value(formData).then((res: any) => {
        modelConfig.okConfig.loading = false
        if (res === undefined) {
          modelConfig.visible = false
        } else {
          modelConfig.visible = true
        }
      }).catch((err: any) => {
        modelConfig.okConfig.loading = false
      })
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}
function fullScreenEvent() {
  fullStatus.value = !fullStatus.value
}
function formatterJsonEvent(formData: any, key: string) {
  try {
    formData[key] = jsonFormatter(formData[key])
  } catch (error) {
    console.error('请检查输入的JSON格式是否正确', error)
    ElMessage.error('请检查输入的JSON格式是否正确')
  }
}
function getUserList() {
  GetUserList({
    page: 0,
    pageSize: 10000,
    searchKeyWord: '',
    tenantId: ''
  }).then((res: any) => {
    userList.value = res.data.content
  }).catch(() => {
    userList.value = []
  })
}

function getMessageList() {
  GetMessagePagesList({
    page: 0,
    pageSize: 10000,
    searchKeyWord: '',
  }).then((res: any) => {
    messageNotiList.value = res.data.content
  }).catch(() => {
    messageNotiList.value = []
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
.message-check-form {
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
          color: getCssVar('color', 'primary');
          ;
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