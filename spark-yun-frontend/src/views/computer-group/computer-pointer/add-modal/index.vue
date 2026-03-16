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
          maxlength="100"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item
        prop="host"
      >
        <template #label>
          <div class="host-label">
            <span>Host</span>
            <el-popover
              :visible="showPortEdit"
              placement="top"
              :width="200"
              :teleported="false"
            >
              <div class="port-popover">
                <el-input
                  v-model="formData.port"
                  maxlength="5"
                  size="small"
                  placeholder="请输入端口号"
                />
                <div class="port-popover__footer">
                  <el-button size="small" @click="showPortEdit = false">取消</el-button>
                  <el-button size="small" type="primary" @click="showPortEdit = false">确定</el-button>
                </div>
              </div>
              <template #reference>
                <span class="port-btn" @click="showPortEdit = !showPortEdit">端口号</span>
              </template>
            </el-popover>
          </div>
        </template>
        <el-input
          v-model="formData.host"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="formData.username" maxlength="100" placeholder="请输入" />
      </el-form-item>
      <el-form-item>
        <template #label>
          <div class="host-label">
            <span>{{ pwdType === 'pwd' ? '密码' : '令牌' }}</span>
            <span class="port-btn" @click="togglePwdType">切换认证</span>
          </div>
        </template>
        <el-input v-if="pwdType === 'pwd'" v-model="formData.passwd" type="password" show-password placeholder="请输入" />
        <el-input
          v-else
          v-model="formData.passwd"
          show-word-limit
          type="textarea"
          :autosize="{ minRows: 2, maxRows: 2 }"
          placeholder="请输入"
        />
      </el-form-item>
      <el-form-item v-if="clusterType === 'standalone'" label="安装服务">
        <div class="install-service-row">
          <div class="install-service-item">
            <span>Spark</span>
            <el-switch v-model="formData.installSparkLocal" />
          </div>
          <div class="install-service-item">
            <span>Flink</span>
            <el-switch v-model="formData.installFlinkLocal" />
          </div>
        </div>
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
    </el-form>
    <template #customLeft>
      <div class="test-button">
        <el-button :loading="testLoading" type="primary" @click="testFun">链接测试</el-button>
        <el-popover
          placement="right"
          title="测试结果"
          :width="400"
          trigger="hover"
          popper-class="message-error-tooltip"
          :content="testResult?.log"
          :disabled="testResult?.status === 'SUCCESS'"
        >
          <template #reference>
            <el-icon class="hover-tooltip" v-if="testResult?.status === 'FAIL'"><WarningFilled /></el-icon>
            <el-icon class="hover-tooltip success" v-else-if="testResult?.status === 'SUCCESS'"><SuccessFilled /></el-icon>
          </template>
        </el-popover>
      </div>
    </template>
  </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { Validator } from '@/validator/index'
import { useRoute } from 'vue-router'
import { TestComputerPointHostData } from '@/services/computer-group.service'

const route = useRoute()

const form = ref<FormInstance>()
const callback = ref<any>()
const pwdType = ref('pwd')
const clusterType = ref('')
const showPortEdit = ref(false)
const testLoading = ref(false)
const testResult = ref()
const modelConfig = reactive({
  title: '添加节点',
  visible: false,
  width: '520px',
  customClass: 'compute-add-modal',
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
  host: '',
  port: '',
  username: '',
  passwd: '',
  agentHomePath: '',
  agentPort: '',
  hadoopHomePath: '',
  installSparkLocal: false,
  installFlinkLocal: false,
  remark: '',
  id: ''
})
const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      message: '请输入节点名称',
      trigger: [ 'blur', 'change' ]
    }
  ],
  host: [
    {
      required: true,
      message: '请输入Host',
      trigger: [ 'blur', 'change' ]
    },
    // {
    //   validator: Validator.HostValidator('请输入正确的host'),
    //   trigger: [ 'blur', 'change' ]
    // }
  ],
  port: [
    {
      required: true,
      message: '请输入port',
      trigger: [ 'blur', 'change' ]
    },
    {
      validator: Validator.PortValidator('请输入正确的端口号'),
      trigger: [ 'blur', 'change' ]
    }
  ]
})

function showModal(cb: () => void, data: any): void {
  callback.value = cb
  pwdType.value = 'pwd'
  showPortEdit.value = false
  clusterType.value = route.query.type

  testResult.value = {
    log: '',
    status: ''
  }
  if (data) {
    formData.name = data.name
    formData.host = data.host
    formData.port = data.port
    formData.username = data.username
    formData.passwd = data.passwd
    formData.agentHomePath = data.agentHomePath
    formData.agentPort = data.agentPort
    formData.hadoopHomePath = data.hadoopHomePath
    formData.installSparkLocal = data.installSparkLocal
    formData.installFlinkLocal = data.installFlinkLocal
    formData.remark = data.remark
    formData.id = data.id
    modelConfig.title = '编辑节点'
  } else {
    formData.name = ''
    formData.host = ''
    formData.port = '22'
    formData.username = ''
    formData.passwd = ''
    formData.agentHomePath = ''
    formData.agentPort = ''
    formData.hadoopHomePath = ''
    formData.installSparkLocal = true
    formData.installFlinkLocal = true
    formData.remark = ''
    formData.id = ''
    modelConfig.title = '添加节点'
  }

  nextTick(() => {
    form.value?.resetFields()
  })
  modelConfig.visible = true
}

function testFun() {
  if (formData.host && formData.port && formData.username && formData.passwd) {
    testLoading.value = true
    TestComputerPointHostData({
      host: formData.host,
      port: formData.port || '22',
      username: formData.username,
      passwd: formData.passwd
    }).then((res: any) => {
      testLoading.value = false
      testResult.value = res.data
      ElMessage.success(res.msg)
    }).catch(() => {
      testLoading.value = false
    })
  } else {
    ElMessage.warning('请将参数填写完整')
  }
}

function togglePwdType() {
  pwdType.value = pwdType.value === 'pwd' ? 'ssh' : 'pwd'
  formData.passwd = ''
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
            showPortEdit.value = false
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
  showPortEdit.value = false
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
  .el-form-item__label {
    width: 100%;
  }
  .host-label {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    .port-btn {
      color: getCssVar('color', 'primary');
      cursor: pointer;
      font-size: 12px;
      &:hover {
        opacity: 0.8;
      }
    }
  }
  .port-popover {
    .port-popover__footer {
      display: flex;
      justify-content: flex-end;
      margin-top: 8px;
    }
  }
  .install-service-row {
    display: flex;
    align-items: center;
    gap: 24px;
    width: 100%;
    .install-service-item {
      display: flex;
      align-items: center;
      gap: 8px;
      span {
        font-size: 13px;
        color: var(--el-text-color-regular);
      }
    }
  }
}
.compute-add-modal {
  .test-button {
    position: absolute;
    left: 20px;
    bottom: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .hover-tooltip {
    margin-left: 8px;
    font-size: 16px;
    color: getCssVar('color', 'danger');
    &.success {
      color: getCssVar('color', 'success');
    }
  }
}
.message-error-tooltip {
    .el-popover__title {
        font-size: 14px;
    }
    font-size: 12px;
}
</style>
