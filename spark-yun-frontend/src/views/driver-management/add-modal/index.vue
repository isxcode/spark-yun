<template>
  <BlockModal :model-config="modelConfig">
    <el-form
      ref="form"
      class="add-driver-modal"
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
          maxlength="200"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item
        label="类型"
        prop="dbType"
      >
        <el-select
          v-model="formData.dbType"
          placeholder="请选择"
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
          type="textarea"
          maxlength="200"
          :autosize="{ minRows: 4, maxRows: 4 }"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item label="驱动" prop="driver">
        <el-upload
          ref="uploadRef"
          class="license-upload"
          action=""
          :limit="1"
          :multiple="false"
          :drag="true"
          :auto-upload="false"
          :on-change="handleChange"
          :on-remove="removeChange"
        >
          <el-icon class="el-icon--upload">
            <upload-filled />
          </el-icon>
          <div class="el-upload__text">
            上传驱动 <em>点击上传</em>
          </div>
        </el-upload>
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
const uploadRef = ref()
const modelConfig = reactive({
  title: '添加驱动',
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
  dbType: '',
  remark: '',
  driver: null,
  id: ''
})
const typeList = reactive([
  {
    label: 'Mysql',
    value: 'MYSQL',
  },
  {
    label: 'Oracle',
    value: 'ORACLE',
  },
  {
    label: 'SqlServer',
    value: 'SQL_SERVER',
  },
  {
    label: 'PostgreSql',
    value: 'POSTGRE_SQL',
  },
  {
    label: 'Clickhouse',
    value: 'CLICKHOUSE',
  },
  {
    label: 'Hive',
    value: 'HIVE',
  },
  {
    label: 'HanaSap',
    value: 'HANA_SAP',
  },
  {
    label: '达梦',
    value: 'DM',
  },
  {
    label: 'Doris',
    value: 'DORIS',
  },
  {
    label: 'OceanBase',
    value: 'OCEANBASE',
  },
  {
    label: 'TiDB',
    value: 'TIDB',
  },
  {
    label: 'StarRocks',
    value: 'STAR_ROCKS',
  },
  {
    label: 'H2',
    value: 'H2',
  },
]);
const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      message: '请输入驱动名称',
      trigger: [ 'blur', 'change' ]
    }
  ],
  dbType: [
    {
      required: true,
      message: '请选择类型',
      trigger: [ 'blur', 'change' ]
    }
  ],
  driver: [
    {
      required: true,
      message: '请上传驱动',
      trigger: 'change'
    }
  ]
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  modelConfig.visible = true
  if (data) {
    formData.name = data.name
    formData.dbType = data.dbType
    formData.remark = data.remark
    formData.id = data.id
    modelConfig.title = '编辑驱动'
  } else {
    formData.name = ''
    formData.dbType = ''
    formData.remark = ''
    formData.id = ''
    modelConfig.title = '添加驱动'
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

function handleChange(e: any) {
  // fileData.value = e.raw
  formData.driver = e.raw
}

function removeChange(e: any) {
  formData.driver = null
  uploadRef.value.clearFiles()
}

function closeEvent() {
  modelConfig.visible = false
}

defineExpose({
  showModal
})
</script>

<style lang="scss">
.add-driver-modal {
  padding: 12px 20px 0 20px;
  box-sizing: border-box;
  .license-upload {
    margin: 0px;
    width: 100%;
    .el-upload {
      .el-upload-dragger {
        padding: 12px 0;
        border-radius: getCssVar('border-radius', 'small');
        .el-upload__text {
          font-size: getCssVar('font-size', 'extra-small');
        }
      }
    }
  }
}
</style>
