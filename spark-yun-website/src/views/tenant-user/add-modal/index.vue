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
        label="成员"
        prop="userId"
      >
        <el-select
          v-model="formData.userId"
          placeholder="请选择"
        >
          <el-option
            v-for="item in userList"
            :key="item.id"
            :label="item.account"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="管理员">
        <el-switch v-model="formData.isTenantAdmin" />
      </el-form-item>
    </el-form>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetUserInfoList } from '@/services/tenant-user.service'

const form = ref<FormInstance>()
const callback = ref<any>()
const userList = ref([])
const modelConfig = reactive({
  title: '添加数据源',
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
  isTenantAdmin: false,
  userId: '',
  id: ''
})
const rules = reactive<FormRules>({
  userId: [
    {
      required: true,
      message: '请选择成员',
      trigger: [ 'change' ]
    }
  ]
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  modelConfig.visible = true
  getUserOfSystem()
  if (data) {
    formData.userId = data.userId
    formData.id = data.id
    modelConfig.title = '编辑成员'
  } else {
    formData.userId = ''
    formData.id = ''
    modelConfig.title = '添加成员'
  }
  nextTick(() => {
    form.value?.resetFields()
  })
}

function getUserOfSystem() {
  GetUserInfoList({
    page: 0,
    pageSize: 999,
    searchKeyWord: ''
  })
    .then((res: any) => {
      userList.value = res.data.content
    })
    .catch(() => {
      userList.value = []
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
