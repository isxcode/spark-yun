<template>
  <BlockModal :model-config="modelConfig">
    <el-form ref="form" class="add-computer-group" label-position="top" :model="formData" :rules="rules">
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" maxlength="200" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="类型" prop="msgType">
        <el-select v-model="formData.msgType" placeholder="请选择">
          <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <!-- 如果选择了阿里短信 -->
      <template v-if="formData.msgType === 'ALI_SMS'">
        <el-form-item label="服务地址(region)" prop="region">
          <el-select v-model="formData.region" placeholder="请选择">
            <el-option v-for="item in regionList" :key="item.regionId" :label="item.regionName" :value="item.regionId" />
          </el-select>
        </el-form-item>
        <el-form-item label="AccessKeyId" prop="accessKeyId">
          <el-input v-model="formData.accessKeyId" maxlength="1000" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="AccessKeySecret" prop="accessKeySecret">
          <el-input v-model="formData.accessKeySecret" maxlength="1000" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="短信签名名称(SignName)" prop="signName">
          <el-input v-model="formData.signName" maxlength="200" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="短信模板Code(TemplateCode)" prop="templateCode">
          <el-input v-model="formData.templateCode" maxlength="200" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="短信模版样例(TemplateParam)">
          <el-input v-model="formData.contentTemplate" type="textarea" maxlength="1000" :autosize="{ minRows: 4, maxRows: 4 }" placeholder="请输入" />
        </el-form-item>
      </template>
      <template v-else-if="formData.msgType === 'EMAIL'">
        <el-form-item label="服务地址(Host)" prop="host">
          <el-input v-model="formData.host" maxlength="200" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="端口号(Port)" prop="port">
          <el-input v-model="formData.port" maxlength="200" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="账号(Username)" prop="username">
          <el-input v-model="formData.username" maxlength="200" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input :show-password="true" v-model="formData.password" maxlength="200" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="主题(Subject)" prop="subject">
          <el-input v-model="formData.subject" maxlength="200" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="邮箱模版样例">
          <el-input v-model="formData.contentTemplate" type="textarea" maxlength="1000" :autosize="{ minRows: 4, maxRows: 4 }" placeholder="请输入" />
        </el-form-item>
      </template>
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
import { RegionList } from '../message-notification.config'

const form = ref<FormInstance>()
const callback = ref<any>()
const typeList = ref([
  {
    label: '阿里短信',
    value: 'ALI_SMS'
  },
  {
    label: '邮箱',
    value: 'EMAIL'
  }
])
const regionList = ref(RegionList)
const modelConfig = reactive({
  title: '添加',
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
  msgType: '',
  remark: '',
  // -----  选择阿里短信的配置 messageConfig
  region: '',
  accessKeyId: '',
  accessKeySecret: '',
  signName: '',
  templateCode: '',
  contentTemplate: '',
  // -----  选择邮箱的配置 messageConfig
  host: '',
  port: '',
  username: '',
  password: '',
  subject: '',

  id: ''
})
const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入函数名', trigger: ['blur', 'change']}],
  msgType: [{ required: true, message: '请选择类型', trigger: ['blur', 'change']}],
  // 阿里短信
  region: [{ required: true, message: '请选择region', trigger: ['blur', 'change']}],
  accessKeyId: [{ required: true, message: '请输入AccessKeyId', trigger: ['blur', 'change']}],
  accessKeySecret: [{ required: true, message: '请输入AccessKeySecret', trigger: ['blur', 'change']}],
  templateCode: [{ required: true, message: '请输入TemplateCode(短信模板Code)', trigger: ['blur', 'change']}],
  // 邮箱
  host: [{ required: true, message: '请输入地址', trigger: ['blur', 'change']}],
  port: [{ required: true, message: '请输入端口', trigger: ['blur', 'change']}],
  username: [{ required: true, message: '请输入账号', trigger: ['blur', 'change']}],
  password: [{ required: true, message: '请输入账号', trigger: ['blur', 'change']}],
})

function showModal(cb: () => void, data: any): void {
  if (data) {
    formData.name = data.name
    formData.msgType = data.msgType
    formData.remark = data.remark
    if (data.messageConfig) {
      Object.keys(data.messageConfig).forEach((key: string) => {
        formData[key] = data.messageConfig[key]
      })
    }
    formData.id = data.id
    modelConfig.title = '编辑'
  } else {
    Object.keys(formData).forEach((key: string) => {
      formData[key] = ''
    })
    modelConfig.title = '添加'
  }

  callback.value = cb
  modelConfig.visible = true
}

function okEvent() {
  form.value?.validate((valid) => {
    if (valid) {
      modelConfig.okConfig.loading = true
      let formDataParams = {
        name: formData.name,
        msgType: formData.msgType,
        remark: formData.remark,
        id: formData.id,
        messageConfig: {}
      }
      if (formData.msgType === 'ALI_SMS') {
        formDataParams.messageConfig = {
          region: formData.region,
          accessKeyId: formData.accessKeyId,
          accessKeySecret: formData.accessKeySecret,
          signName: formData.signName,
          templateCode: formData.templateCode,
          contentTemplate: formData.contentTemplate
        }
      } else if (formData.msgType === 'EMAIL') {
        formDataParams.messageConfig = {
          host: formData.host,
          port: formData.port,
          username: formData.username,
          password: formData.password,
          subject: formData.subject,
          contentTemplate: formData.contentTemplate
        }
      }
      callback.value(formDataParams).then((res: any) => {
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
