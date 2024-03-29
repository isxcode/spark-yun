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
      <!-- <el-form-item label="默认计算集群">
        <el-select
          v-model="formData.defaultClusterId"
          placeholder="请选择"
        >
          <el-option
            v-for="item in clusterList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item> -->
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
import { GetComputerGroupList } from '@/services/computer-group.service';

const form = ref<FormInstance>()
const callback = ref<any>()
const clusterList = ref([])  // 计算集群
const modelConfig = reactive({
  title: '添加作业流',
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
  // defaultClusterId: '',
  remark: '',
  id: ''
})
const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      message: '请输入作业流名称',
      trigger: [ 'blur', 'change' ]
    }
  ]
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  modelConfig.visible = true
  if (data) {
    formData.name = data.name
    formData.remark = data.remark
    formData.id = data.id
    modelConfig.title = '编辑作业流'
  } else {
    formData.name = ''
    formData.remark = ''
    formData.id = ''
    modelConfig.title = '添加作业流'
  }

  getClusterList()
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
        })
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}

function getClusterList() {
  GetComputerGroupList({
    page: 0,
    pageSize: 10000,
    searchKeyWord: ''
  }).then((res: any) => {
    clusterList.value = res.data.content.map((item: any) => {
      return {
        label: item.name,
        value: item.id
      }
    })
  }).catch(() => {
    clusterList.value = []
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
