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
        label="集群名称"
        prop="name"
      >
        <el-input
          v-model="formData.name"
          maxlength="20"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="formData.clusterType" placeholder="请选择" :filterable="true">
          <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
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
import { reactive, defineExpose, ref, nextTick, computed } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { getVipLicenseEnabled } from '@/utils/vip-license'

const form = ref<FormInstance>()
const callback = ref<any>()
const modelConfig = reactive({
  title: '新建集群',
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
  clusterType: '',
  name: '',
  remark: '',
  id: ''
})
const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      message: '请输入集群名称',
      trigger: [ 'blur', 'change' ]
    }
  ]
})
const allTypeList = [
  {
    label: 'Kubernetes',
    value: 'kubernetes',
  },
  {
    label: 'Yarn',
    value: 'yarn',
  },
  {
    label: 'StandAlone',
    value: 'standalone',
  },
]
const licenseEnabled = ref(true)
const typeList = computed(() => {
  if (licenseEnabled.value) {
    return allTypeList
  }
  return allTypeList.filter(item => item.value === 'standalone')
})

async function showModal(cb: () => void, data: any): Promise<void> {
  callback.value = cb
  licenseEnabled.value = await getVipLicenseEnabled()

  if (data) {
    formData.name = data.name
    formData.clusterType = data.clusterType
    formData.remark = data.remark
    formData.id = data.id
    modelConfig.title = '编辑集群'
  } else {
    formData.name = ''
    formData.clusterType = licenseEnabled.value ? '' : 'standalone'
    formData.remark = ''
    formData.id = ''
    modelConfig.title = '新建集群'
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
          clusterId: formData.id ? formData.id : undefined
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
