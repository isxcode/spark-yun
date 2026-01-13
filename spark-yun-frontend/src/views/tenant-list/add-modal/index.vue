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
        label="租户名称"
        prop="name"
      >
        <el-input
          v-model="formData.name"
          maxlength="100"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="成员数">
        <el-input-number
          v-model="formData.maxMemberNum"
          placeholder="请输入"
          :min="0"
          :controls="false"
        />
      </el-form-item>
      <el-form-item label="作业流数">
        <el-input-number
          v-model="formData.maxWorkflowNum"
          placeholder="请输入"
          :min="0"
          :controls="false"
        />
      </el-form-item>
      <el-form-item
        v-if="renderSence === 'new'"
        label="管理员"
        prop="adminUserId"
      >
        <el-select
          v-model="formData.adminUserId"
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
    <template #customLeft>
      <div class="valid-time">
        <el-date-picker
          v-model="formData.validEndDateTime"
          type="datetime"
          placeholder="有效期"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          :editable="false"
        />
      </div>
    </template>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetUserInfoList } from '@/services/tenant-user.service'
import dayjs from 'dayjs'

const form = ref<FormInstance>()
const callback = ref<any>()
const userList = ref([])
const renderSence = ref('new')
const modelConfig = reactive({
  title: '添加租户',
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
  maxMemberNum: 5,
  maxWorkflowNum: 20,
  adminUserId: '',
  validStartDateTime: '',
  validEndDateTime: '',
  remark: '',
  id: ''
})
const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      message: '请输入租户名称',
      trigger: [ 'change', 'blur' ]
    }
  ],
  adminUserId: [
    {
      required: true,
      message: '请选择管理员',
      trigger: [ 'change', 'blur' ]
    }
  ]
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  modelConfig.visible = true
  getUserOfSystem()
  if (data) {
    formData.name = data.name
    formData.maxMemberNum = data.maxMemberNum
    formData.maxWorkflowNum = data.maxWorkflowNum
    formData.remark = data.remark
    formData.validStartDateTime = data.validStartDateTime
    formData.validEndDateTime = data.validEndDateTime
    formData.id = data.id
    modelConfig.title = '编辑租户'
    renderSence.value = 'edit'
  } else {
    formData.name = ''
    formData.maxMemberNum = 2
    formData.maxWorkflowNum = 5
    formData.adminUserId = ''
    formData.remark = ''
    formData.validStartDateTime = ''
    formData.validEndDateTime = ''
    formData.id = ''
    modelConfig.title = '添加租户'
    renderSence.value = 'new'
  }
  if (!formData.validStartDateTime) {
    formData.validStartDateTime = dayjs(new Date()).format('YYYY-MM-DD HH:mm:ss')
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
        .catch((err: any) => {
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
.el-date-picker {
  .el-picker-panel__footer {
    display: flex;
    justify-content: space-between;
  }
}
.valid-time {
  position: absolute;
  left: 20px;
  .el-date-editor {
    .el-input__wrapper {
      width: 156px;
      height: 26px;
    }
  }
}
</style>
