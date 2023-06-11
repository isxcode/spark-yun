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
      <el-form-item label="类型" prop="dbType">
        <el-select v-model="formData.dbType" placeholder="请选择">
          <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item
        label="连接信息"
        prop="jdbcUrl"
      >
        <el-input
          v-model="formData.jdbcUrl"
          maxlength="100"
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
          maxlength="100"
          placeholder="请输入"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="密码" prop="passwd">
        <el-input v-model="formData.passwd" maxlength="100" type="password" show-password placeholder="请输入" />
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

const form = ref<FormInstance>()
const callback = ref<any>()
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
  name: "",
  dbType: "",
  jdbcUrl: "",
  username: "",
  passwd: "",
  comment: "",
  id: "",
});
const typeList = reactive([
  {
    label: "Mysql",
    value: "MYSQL",
  },
  {
    label: "Oracle",
    value: "ORACLE",
  },
  {
    label: "SqlServer",
    value: "SQL_SERVER",
  },
  {
    label: "PostgreSql",
    value: "POSTGRE_SQL",
  },
  {
    label: "Clickhouse",
    value: "CLICKHOUSE",
  },
  {
    label: "Hive",
    value: "HIVE",
  },
  {
    label: "HanaSap",
    value: "HANA_SAP",
  },
  {
    label: "达梦",
    value: "DM",
  },
  {
    label: "Doris",
    value: "DORIS",
  },
  {
    label: "OceanBase",
    value: "OCEANBASE",
  },
  {
    label: "TiDB",
    value: "TIDB",
  },
  {
    label: "StarRocks",
    value: "STAR_ROCKS",
  },
]);
const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      message: '请输入数据源名称',
      trigger: [ 'blur', 'change' ]
    }
  ],
  dbType: [
    {
      required: true,
      message: '请选择数据库类型',
      trigger: [ 'blur', 'change' ]
    }
  ],
  jdbcUrl: [
    {
      required: true,
      message: '请输入jdbc连接信息',
      trigger: [ 'blur', 'change' ]
    }
  ],
  username: [
    {
      required: true,
      message: '请输入用户名',
      trigger: [ 'blur', 'change' ]
    }
  ],
  passwd: [
    {
      required: true,
      message: '请输入密码',
      trigger: [ 'blur', 'change' ]
    }
  ]
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  modelConfig.visible = true
  if (data) {
    formData.name = data.name;
    formData.dbType = data.dbType;
    formData.jdbcUrl = data.jdbcUrl;
    formData.username = data.username;
    formData.passwd = data.passwd;
    formData.comment = data.comment;
    formData.id = data.id;
    modelConfig.title = "编辑数据源";
  } else {
    formData.name = "";
    formData.dbType = "";
    formData.jdbcUrl = "";
    formData.username = "";
    formData.passwd = "";
    formData.comment = "";
    formData.id = "";
    modelConfig.title = "添加数据源";
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
