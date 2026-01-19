<template>
  <BlockModal :model-config="modelConfig">
    <el-form ref="form" class="add-lib-package" label-position="top" :model="formData" :rules="rules">
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" maxlength="200" placeholder="请输入" />
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

const form = ref<FormInstance>()
const callback = ref<any>()

const modelConfig = reactive({
  title: '添加依赖包',
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
  remark: '',
  id: ''
})

const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      message: '请输入名称',
      trigger: ['blur', 'change']
    }
  ]
})

function showModal(cb: () => void, data: any): void {
  if (data) {
    formData.name = data.name
    formData.remark = data.remark
    formData.id = data.id
    modelConfig.title = '编辑依赖包'
  } else {
    formData.name = ''
    formData.remark = ''
    formData.id = ''
    modelConfig.title = '添加依赖包'
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

function closeEvent() {
  modelConfig.visible = false
}

defineExpose({
  showModal
})
</script>

<style lang="scss" scoped>
.add-lib-package {
  padding: 20px;
}
</style>

