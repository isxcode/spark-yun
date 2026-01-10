<template>
  <BlockModal :model-config="modelConfig">
    <el-form
      ref="form"
      class="add-computer-group"
      label-position="top"
      :model="formData"
      :rules="rules"
    >
      <el-form-item
        label="用户名"
        prop="username"
      >
        <el-input
          v-model="formData.username"
          maxlength="100"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        label="账号"
        prop="account"
      >
        <el-input
          v-model="formData.account"
          maxlength="100"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        v-if="renderSence === 'new'"
        label="密码"
        prop="passwd"
      >
        <el-input
          v-model="formData.passwd"
          maxlength="100"
          type="password"
          show-password
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item label="有效期" prop="validEndDateTime">
        <el-date-picker
          v-model="formData.validEndDateTime"
          type="datetime"
          placeholder="请选择"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          :editable="false"
        />
      </el-form-item>
      <el-form-item
        label="手机号"
        prop="phone"
      >
        <el-input
          v-model="formData.phone"
          maxlength="11"
          placeholder="请输入手机号"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        label="邮箱"
        prop="email"
      >
        <el-input
          v-model="formData.email"
          maxlength="100"
          placeholder="请输入邮箱"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="备注">
        <el-input
          v-model="formData.remark"
          show-word-limit
          type="textarea"
          maxlength="200"
          :autosize="{ minRows: 4, maxRows: 4 }"
          placeholder="请输入"
        />
      </el-form-item>
    </el-form>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import dayjs from 'dayjs'

const form = ref<FormInstance>()
const callback = ref<any>()
const renderSence = ref('new')
const modelConfig = reactive({
  title: '添加用户',
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
  username: '',
  account: '',
  passwd: '',
  validStartDateTime: '',
  validEndDateTime: '',
  phone: '',
  email: '',
  remark: '',
  id: ''
})
// 中国手机号验证函数
const validatePhone = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback()
    return
  }
  // 中国手机号正则：1开头，第二位3-9，总共11位数字
  const phoneReg = /^1[3-9]\d{9}$/
  if (!phoneReg.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

// 邮箱验证函数
const validateEmail = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback()
    return
  }
  // 更严格的邮箱正则验证
  const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  if (!emailReg.test(value)) {
    callback(new Error('请输入正确的邮箱格式'))
  } else {
    callback()
  }
}

const rules = reactive<FormRules>({
  username: [
    {
      required: true,
      message: '请输入用户名',
      trigger: [ 'change' ]
    }
  ],
  account: [
    {
      required: true,
      message: '请输入账号',
      trigger: [ 'change' ]
    }
  ],
  passwd: [
    {
      required: true,
      message: '请输入密码',
      trigger: [ 'change' ]
    }
  ],
  validEndDateTime: [
    {
      required: true,
      message: '请选择有效期',
      trigger: [ 'change', 'blur' ]
    }
  ],
  phone: [
    {
      validator: validatePhone,
      trigger: ['blur', 'change']
    }
  ],
  email: [
    {
      validator: validateEmail,
      trigger: ['blur', 'change']
    }
  ]
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  modelConfig.visible = true
  if (data) {
    (formData.username = data.username), (formData.account = data.account)
    formData.phone = data.phone
    formData.email = data.email
    formData.remark = data.remark
    formData.validStartDateTime = data.validStartDateTime
    formData.validEndDateTime = data.validEndDateTime
    formData.id = data.id
    modelConfig.title = '编辑用户'
    renderSence.value = 'edit'
  } else {
    formData.username = ''
    formData.account = ''
    formData.passwd = ''
    formData.validStartDateTime = ''
    formData.validEndDateTime = ''
    formData.phone = ''
    formData.email = ''
    formData.remark = ''
    formData.id = ''
    modelConfig.title = '添加用户'
    renderSence.value = 'new'
  }
  if (!formData.validStartDateTime) {
    formData.validStartDateTime = dayjs(new Date()).format('YYYY-MM-DD HH:mm:ss')
  }
  nextTick(() => {
    form.value?.resetFields()
  })
}

function okEvent() {
  form.value?.validate((valid) => {
    if (valid) {
      modelConfig.okConfig.loading = true
      callback
        .value({
          ...formData,
          id: formData.id ? formData.id : undefined
        })
        .then((res: any) => {
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
.add-computer-group {
  padding: 12px 20px 0 20px;
  box-sizing: border-box;
}
</style>
