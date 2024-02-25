<template>
  <BlockModal :model-config="modelConfig">
    <el-form ref="form" class="add-computer-group" label-position="top" :model="formData" :rules="rules">
      <el-form-item label="名称" prop="funcName">
        <el-input v-model="formData.funcName" maxlength="200" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择">
          <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="资源文件" prop="fileId">
        <el-select v-model="formData.fileId" placeholder="请选择">
          <el-option v-for="item in fileIdList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="类名" prop="className">
        <el-input v-model="formData.className" maxlength="200" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="结果类型" prop="resultType">
        <el-select v-model="formData.resultType" placeholder="请选择">
          <el-option v-for="item in resultTypeList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
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
import { GetFileCenterList } from '@/services/file-center.service'

const form = ref<FormInstance>()
const callback = ref<any>()
const typeList = ref([
  {
    label: 'UDF',
    value: 'UDF'
  },
  {
    label: 'UDAF',
    value: 'UDAF'
  }
])
const resultTypeList = ref([
  {
    label: 'string',
    value: 'string',
  },
  {
    label: 'int',
    value: 'int',
  },
  {
    label: 'long',
    value: 'long',
  },
  {
    label: 'double',
    value: 'double'
  },
  {
    label: 'boolean',
    value: 'boolean',
  },
  {
    label: 'date',
    value: 'date',
  },
  {
    label: 'timestamp',
    value: 'timestamp',
  }
])
const fileIdList = ref([])
const modelConfig = reactive({
  title: '添加函数',
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
  type: '',
  fileId: '',
  funcName: '',
  className: '',
  resultType: '',
  remark: '',
  id: ''
})
const rules = reactive<FormRules>({
  funcName: [
    {
      required: true,
      message: '请输入函数名',
      trigger: ['blur', 'change']
    }
  ],
  type: [
    {
      required: true,
      message: '请选择类型',
      trigger: ['blur', 'change']
    }
  ],
  fileId: [
    {
      required: true,
      message: '请选择资源文件',
      trigger: ['blur', 'change']
    }
  ],
  className: [
    {
      required: true,
      message: '请输入类名',
      trigger: ['blur', 'change']
    }
  ],
  resultType: [
    {
      required: true,
      message: '请选择结果返回类型',
      trigger: ['blur', 'change']
    }
  ]
})

function showModal(cb: () => void, data: any): void {
  getFileCenterList()

  if (data) {
    formData.type = data.type
    formData.fileId = data.fileId
    formData.funcName = data.funcName
    formData.className = data.className
    formData.resultType = data.resultType
    formData.remark = data.remark
    formData.id = data.id
    modelConfig.title = '编辑'
  } else {
    formData.type = ''
    formData.fileId = ''
    formData.funcName = ''
    formData.className = ''
    formData.resultType = ''
    formData.remark = ''
    modelConfig.title = '添加'
  }

  callback.value = cb
  modelConfig.visible = true
}

function getFileCenterList() {
  GetFileCenterList({
    page: 0,
    pageSize: 10000,
    searchKeyWord: '',
    type: 'FUNC'
  }).then((res: any) => {
    fileIdList.value = res.data.content.map(item => {
      return {
        label: item.fileName,
        value: item.id
      }
    })
  }).catch(() => {
    fileIdList.value = []
  })
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

<style lang="scss">
.license-upload {
  margin: 20px;

  .el-upload {
    .el-upload-dragger {
      border-radius: getCssVar('border-radius', 'small');

      .el-upload__text {
        font-size: getCssVar('font-size', 'extra-small');
      }
    }
  }
}
</style>
