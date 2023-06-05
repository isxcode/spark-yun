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
        label="名称"
        prop="name"
      >
        <el-input
          v-model="formData.name"
          maxlength="20"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        label="Host"
        prop="host"
      >
        <el-input
          v-model="formData.host"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        label="Port"
        prop="port"
      >
        <el-input
          v-model="formData.port"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        label="用户名"
        prop="username"
      >
        <el-input
          v-model="formData.username"
          maxlength="20"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        label="密码"
        prop="password"
      >
        <el-input
          v-model="formData.password"
          type="password"
          show-password
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item
        label="YunHome"
        prop="agentHomePath"
      >
        <el-input
          v-model="formData.agentHomePath"
          maxlength="100"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        label="YunPort"
        prop="agentPort"
      >
        <el-input
          v-model="formData.agentPort"
          maxlength="20"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        label="Hadoop"
        prop="hadoopHomePath"
      >
        <el-input
          v-model="formData.hadoopHomePath"
          maxlength="100"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="备注">
        <el-input
          v-model="formData.comment"
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
import { Validator } from '@/validator/index'

const form = ref<FormInstance>()
const callback = ref<any>()
const modelConfig = reactive({
  title: '添加节点',
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
  host: '',
  port: '',
  username: '',
  password: '',
  agentHomePath: '',
  agentPort: '',
  hadoopHomePath: '',
  comment: '',
  id: ''
})
const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      message: '请输入节点名称',
      trigger: [ 'blur', 'change' ]
    }
  ],
  host: [
    {
      required: true,
      message: '请输入Host',
      trigger: [ 'blur', 'change' ]
    },
    {
      validator: Validator.HostValidator('请输入正确的host'),
      trigger: [ 'blur', 'change' ]
    }
  ],
  port: [
    {
      required: true,
      message: '请输入port',
      trigger: [ 'blur', 'change' ]
    },
    {
      validator: Validator.PortValidator('请输入正确的端口号'),
      trigger: [ 'blur', 'change' ]
    }
  ]
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  if (data) {
    formData.name = data.name
    formData.host = data.host
    formData.port = data.port
    formData.username = data.username
    formData.password = data.password
    formData.agentHomePath = data.agentHomePath
    formData.agentPort = data.agentPort
    formData.hadoopHomePath = data.hadoopHomePath
    formData.comment = data.comment
    formData.id = data.id
    modelConfig.title = '编辑节点'
  } else {
    formData.name = ''
    formData.host = ''
    formData.port = ''
    formData.username = ''
    formData.password = ''
    formData.agentHomePath = ''
    formData.agentPort = ''
    formData.hadoopHomePath = ''
    formData.comment = ''
    formData.id = ''
    modelConfig.title = '添加节点'
  }

  nextTick(() => {
    form.value?.resetFields()
  })
  modelConfig.visible = true
}

function okEvent() {
  form.value?.validate((valid) => {
    if (valid) {
      modelConfig.okConfig.loading = true
      callback
        .value({
          ...formData,
          id: formData.id ? formData.id : undefined,
          calculateEngineId: formData.id ? formData.id : undefined
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
