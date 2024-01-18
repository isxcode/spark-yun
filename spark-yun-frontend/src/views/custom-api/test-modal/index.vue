<template>
  <BlockModal :model-config="modelConfig">
    <el-form
      ref="form"
      class="custom-api-form"
      label-position="top"
      :model="formData"
      :rules="rules"
    >
      <el-form-item label="请求路径">
        <el-input
          v-model="formData.url"
          maxlength="1000"
          placeholder="请输入"
          :disabled="true"
        />
      </el-form-item>
      <el-form-item label="请求头" prop="headerConfig" :class="{ 'show-screen__full': reqHeaderFullStatus }">
        <el-icon class="modal-full-screen" @click="fullScreenEvent('reqHeaderFullStatus')"><FullScreen v-if="!reqHeaderFullStatus" /><Close v-else /></el-icon>
        <code-mirror v-model="formData.headerConfig" basic :lang="jsonLang" @blur="blurEvent"/>
      </el-form-item>
      <el-form-item label="请求体" prop="bodyConfig" :class="{ 'show-screen__full': reqBodyFullStatus }">
        <el-icon class="modal-full-screen" @click="fullScreenEvent('reqBodyFullStatus')"><FullScreen v-if="!reqBodyFullStatus" /><Close v-else /></el-icon>
        <code-mirror v-model="formData.bodyConfig" basic :lang="jsonLang"/>
      </el-form-item>
      <el-form-item label="响应体" prop="returnConfig" :class="{ 'show-screen__full': respBodyFullStatus }">
        <el-icon class="modal-full-screen" @click="fullScreenEvent('respBodyFullStatus')">
          <FullScreen v-if="!respBodyFullStatus" />
          <Close v-else />
        </el-icon>
        <code-mirror ref="responseBodyRef" v-model="formData.returnConfig" :disabled="true" basic :lang="jsonLang"/>
      </el-form-item>
    </el-form>
  </BlockModal>
</template>
  
<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetDatasourceList } from '@/services/datasource.service'
import { GetComputerGroupList } from '@/services/computer-group.service'
import CodeMirror from 'vue-codemirror6'
import {json} from '@codemirror/lang-json'
import { jsonFormatter } from '@/utils/formatter'

const form = ref<FormInstance>()
const jsonLang = ref<any>(json())
const responseBodyRef = ref()

const reqHeaderFullStatus = ref(false)
const reqBodyFullStatus = ref(false)
const respBodyFullStatus = ref(false)

const modelConfig = reactive({
  title: '测试接口',
  visible: false,
  width: '60%',
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
  url: '',              // 自定义访问路径
  headerConfig: null,   // 请求头
  bodyConfig: null,     // 请求体
  returnConfig: null    // 返回体
})
const rules = reactive<FormRules>({
  // url: [{ required: true, message: '请输入自定义访问路径', trigger: [ 'blur', 'change' ]}],
  headerConfig: [{ required: true, message: '请输入请求头设置', trigger: [ 'blur', 'change' ]}],
  bodyConfig: [{ required: true, message: '请输入请求体设置', trigger: [ 'blur', 'change' ]}]
})

function showModal(data: any): void {
  modelConfig.visible = true
  formData.url = data.url
  formData.headerConfig = null
  formData.bodyConfig = null
  formData.returnConfig = null
  nextTick(() => {
    form.value?.resetFields()
  })
}


function okEvent() {
  form.value?.validate((valid) => {
    if (valid) {
      // modelConfig.okConfig.loading = true
      // callback
      //   .value({
      //     ...formData
      //   })
      //   .then((res: any) => {
      //     modelConfig.okConfig.loading = false
      //     if (res === undefined) {
      //       modelConfig.visible = false
      //     } else {
      //       modelConfig.visible = true
      //     }
      //   })
      //   .catch(() => {
      //     modelConfig.okConfig.loading = false
      //   })
      console.log('responseBodyRef', responseBodyRef.value)
      formData.returnConfig = '{"data":{"id":5000534,"pipelineDefId":3006415,"teamCode":null,"templateId":null,"stages":[{"id":4018457,"taskDefId":1,"name":"执行源","taskList":[[{"id":4018452,"taskDefId":2,"name":"全量测试流水线20230830_copy","taskType":"PerformSource","source":{"sourceType":"pipelineSource","sourcePipelineId":"3006339","defaultVersion":"1"},"buildParams":{},"threshold":[],"result":{},"manualParams":{},"extra":{},"projectId":null,"podName":null}],[{"id":4018453,"taskDefId":9,"name":"TSF容器部署","taskType":"PerformSource","source":{"sourceType":"pipelineSource","sourcePipelineId":"3003230","defaultVersion":"-1"},"buildParams":{},"threshold":[],"result":{},"manualParams":{},"extra":{},"projectId":null,"podName":null}],[{"id":4018454,"taskDefId":20,"name":"TCS容器部署-TEST","taskType":"PerformSource","source":{"sourceType":"pipelineSource","sourcePipelineId":"3004052","defaultVersion":"-1"},"buildParams":{},"threshold":[],"result":{},"manualParams":{},"extra":{},"projectId":null,"podName":null}],[{"id":4018455,"taskDefId":21,"name":"批处理导入文件-test","taskType":"PerformSource","source":{"sourceType":"pipelineSource","sourcePipelineId":"3007719","defaultVersion":"-1"},"buildParams":{},"threshold":[],"result":{},"manualParams":{},"extra":{},"projectId":null,"podName":null}],[{"id":4018456,"taskDefId":22,"name":"批处理导入文件-test2","taskType":"PerformSource","source":{"sourceType":"pipelineSource","sourcePipelineId":"3007722","defaultVersion":"-1"},"buildParams":{},"threshold":[],"result":{},"manualParams":{},"extra":{},"projectId":null,"podName":null}]]},{"id":4018459,"taskDefId":3,"name":"工具","taskList":[[{"id":4018458,"taskDefId":4,"name":"人工卡点","taskType":"ManualCheckpoint","source":{},"buildParams":{},"threshold":[],"result":{},"manualParams":{"examinations":[{"userInfoList":[{"usercode":"chenlong-026","username":"chenlong-026（chenlong-026）"},{"usercode":"xuxiaomei-008","username":"徐晓梅（xuxiaomei-008）"},{"usercode":"zhangyijie-002","username":"张逸杰（zhangyijie-002）"}],"examineMode":"orSign","usercode":["chenlong-026","xuxiaomei-008","zhangyijie-002"]}],"notice":{"email":{"enabled":false}}},"extra":{},"projectId":null,"podName":null}]]},{"id":4018461,"taskDefId":18,"name":"交付","taskList":[[{"id":4018460,"taskDefId":19,"name":"交付","taskType":"Delivery","source":{},"buildParams":{"tsfEnv":["PRO"],"ftpHost":"29.23.101.204","ftpUsername":"JTKAIFA","acrEnv":["PRO"],"tcsEnv":["PRO"],"projectCode":"P36965967409851936","ftpPassword":"ENC-aM8DgO2rxQ2IB4bPCQ+ChA==","acrCloudEnv":[],"ftpPort":"9999","ftpStatus":"true","tenant":"TECH"},"threshold":[],"result":{},"manualParams":{},"extra":{},"projectId":null,"podName":null}]]},{"id":4018465,"taskDefId":33,"name":"发布","taskList":[[{"id":4018462,"taskDefId":34,"name":"TCS发布单（应用）","taskType":"TcsReleaseApp","source":{},"buildParams":{"step2":{"configurationVersion":1704176082346,"releaseDescription":"1","appName":"cicdtest","deployGroupName":"cicdtest-sit","appId":"application-6yojpdvl","groupId":"group-dapbq2ly","configurationItemName":"test"},"grayReleaseFlg":{"step3_grayReleaseFlg":false,"step1_grayReleaseFlg":false},"step3":{},"releaseType":["configFile"],"step1":{"versionDescription":"12","configurationVersion":1704176082346,"appName":"cicdtest","appId":"application-6yojpdvl","configurationFile":"http://tech-generic.coding-gp22dpm-dev.group.cpic.com/P36965967409851936/generic-dev/configManage/3004553/2033647/20211105/配置文件/ymal1.ymal?version=latest","configurationItemName":"test"}},"threshold":[],"result":{},"manualParams":{},"extra":{},"projectId":null,"podName":null}],[{"id":4018463,"taskDefId":35,"name":"TCS发布单（公共）","taskType":"TcsReleasePublic","source":{},"buildParams":{"step4":{"enableImportedTask":"false"},"step2":{"configurationVersion":1704176105122,"releaseDescription":"1","nameSpace":"cicdtest1","configurationItemName":"test2"},"grayReleaseFlg":{"step3_grayReleaseFlg":false,"step1_grayReleaseFlg":false},"step3":{"appName":"cicdtest","deployGroupName":"cicdtest-sit","appId":"application-6yojpdvl","appImage":"registry-cd.gp21cmcp-sit.group.cpic.com/tsf-909619400/cicdtest:20231025_20231213141951"},"releaseType":["configFile","appImage"],"step1":{"versionDescription":"12","configurationVersion":1704176105122,"configurationFile":"http://tech-generic.coding-gp22dpm-dev.group.cpic.com/P36965967409851936/generic-dev/configManage/3004553/2033647/20211105/配置文件/ymal.ymal?version=latest","configurationItemName":"test2"}},"threshold":[],"result":{},"manualParams":{},"extra":{},"projectId":null,"podName":null}],[{"id":4018464,"taskDefId":36,"name":"TCS发布单（文件）","taskType":"TcsReleaseFile","source":{},"buildParams":{"step4":{"enableImportedTask":"false"},"step2":{"configurationVersion":1704176138203,"releaseDescription":"12","appName":"cicdtest","deployGroupName":"cicdtest-dev","groupId":"group-6ymq873v","configurationItemName":"test"},"grayReleaseFlg":{"step3_grayReleaseFlg":false,"step1_grayReleaseFlg":false},"step3":{"appId":""},"releaseType":["configFile"],"step1":{"suffixScript":"ls","versionDescription":"12","configurationVersion":1704176138203,"configurationAddr":"/etc/nginx/conf.d/","appName":"cicdtest","appId":"application-6yojpdvl","configurationFile":"http://tech-generic.coding-gp22dpm-dev.group.cpic.com/P36965967409851936/generic-dev/configManage/3004553/2033647/20211105/配置文件/ymal.ymal?version=latest","configFileName":"file12","fileCode":"gbk","configurationItemName":"test"}},"threshold":[],"result":{},"manualParams":{},"extra":{},"projectId":null,"podName":null}]]}],"version":38,"name":"TSF/TCS发布单","params":null,"insertTime":"2023-10-19T07:06:12.000+00:00","insertOperator":"chenlong-026","insertOperatorId":null,"insertOperatorCode":"chenlong-026","modifyTime":"2024-01-02T06:25:47.000+00:00","modifyOperator":"chenlong-026","modifyOperatorCode":"chenlong-026","modifyOperatorId":null,"authorityCode":null,"category":"DELIVER","subCategory":null,"pubDate":"2023-10-19","sysCode":"TX00340","passed":null,"reason":null,"releaseOrders":null,"testingTime":null,"pipelineId":null,"paramsMd5":"5dccf130a2b06c2e7a8808717d14e03a","autoHookStatus":false,"taskDeleteUpdate":true},"success":true,"code":null,"message":null}'
      try {
        formData.returnConfig = jsonFormatter(formData.returnConfig)
      } catch (error) {
        console.error('JSON格式有误，请检查输入格式是否正确！', error)
      }
    } else {
      ElMessage.warning('请将表单输入完整')
    }
  })
}

function closeEvent() {
  modelConfig.visible = false
}

function fullScreenEvent(type: string) {
  if (type === 'reqHeaderFullStatus') {
    reqHeaderFullStatus.value = !reqHeaderFullStatus.value
  } else if (type === 'reqBodyFullStatus') {
    reqBodyFullStatus.value = !reqBodyFullStatus.value
  } else {
    respBodyFullStatus.value = !respBodyFullStatus.value
  }
}

defineExpose({
  showModal
})
</script>

<style lang="scss">
.custom-api-form {
  box-sizing: border-box;
  padding: 12px 20px 0 20px;
  width: 100%;

  .api-item {
    .item-title {
      font-size: 14px;
      padding-bottom: 12px;
      margin-bottom: 12px;
      box-sizing: border-box;
      border-bottom: 1px solid #ebeef5;
      font-weight: bolder;
      color: getCssVar('color', 'primary');
    }
  }
  .el-form-item {
    &.show-screen__full {
      position: fixed;
      width: 100%;
      height: 100%;
      top: 0;
      left: 0;
      background-color: #ffffff;
      padding: 12px 20px;
      box-sizing: border-box;
      transition: all 0.15s linear;
      z-index: 10;
      .el-form-item__content {
        align-items: flex-start;
        height: 100%;
        .vue-codemirror {
          height: calc(100% - 36px);
        }
      }
    }
    .el-form-item__content {
      position: relative;
      .modal-full-screen {
        position: absolute;
        top: -26px;
        right: 0;
        cursor: pointer;
        &:hover {
          color: getCssVar('color', 'primary');;
        }
      }
      .vue-codemirror {
        height: 130px;
        width: 100%;

        .cm-editor {
          height: 100%;
          outline: none;
          border: 1px solid #dcdfe6;
        }

        .cm-gutters {
          font-size: 12px;
          font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
        }

        .cm-content {
          font-size: 12px;
          font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
        }

        .cm-tooltip-autocomplete {
          ul {
            li {
              height: 40px;
              display: flex;
              align-items: center;
              font-size: 12px;
              background-color: #ffffff;
              font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
            }

            li[aria-selected] {
              background: #409EFF;
            }

            .cm-completionIcon {
              margin-right: -4px;
              opacity: 0;
            }
          }
        }
      }
    }
  }
}
</style>
