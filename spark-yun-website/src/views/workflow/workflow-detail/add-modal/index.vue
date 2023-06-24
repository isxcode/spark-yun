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
        label="类型"
        prop="workType"
      >
        <el-select
          v-model="formData.workType"
          placeholder="请选择"
          :disabled="formData.id ? true : false"
        >
          <el-option
            v-for="item in typeList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
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

const form = ref<FormInstance>()
const callback = ref<any>()
const modelConfig = reactive({
  title: '添加作业',
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
  workType: '',
  remark: '',
  id: ''
})
const typeList = reactive([
  {
    label: 'Jdbc执行作业',
    value: 'EXE_JDBC'
  },
  {
    label: 'Jdbc查询作业',
    value: 'QUERY_JDBC'
  },
  {
    label: 'SparkSql查询作业',
    value: 'SPARK_SQL'
  }
])
const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      message: '请输入作业名称',
      trigger: [ 'blur', 'change' ]
    }
  ],
  workType: [
    {
      required: true,
      message: '请选择类型',
      trigger: [ 'blur', 'change' ]
    }
  ]
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  modelConfig.visible = true
  if (data && data.id) {
    formData.name = data.name
    formData.workType = data.workType
    formData.remark = data.remark
    formData.id = data.id
    modelConfig.title = '编辑作业'
  } else {
    formData.name = ''
    formData.workType = ''
    formData.remark = ''
    formData.id = ''
    modelConfig.title = '添加作业'
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
        .catch((err: any) => {
          modelConfig.okConfig.loading = false
          ElMessage.error(err)
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
