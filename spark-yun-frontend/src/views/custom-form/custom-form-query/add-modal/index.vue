<template>
    <BlockModal :model-config="modelConfig">
        <z-form-engine
            ref="formEngineRef"
            v-model="formData"
            renderSence="new"
            :formConfigList="formConfigList"
        ></z-form-engine>
    </BlockModal>
</template>
  
<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import ZFormEngine from '@/lib/packages/z-form-engine/index.vue'
import { ElMessage } from 'element-plus'

const callback = ref<any>()
const formEngineRef = ref()
const modelConfig = reactive({
    title: '添加',
    visible: false,
    width: '80%',
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
const formData = ref({})
const formConfigList = reactive([
  {
    "uuid": "cdd90901-1d54-ae12-0595-fc8a415b4c64",
    "type": "simple",
    "formValueCode": "1",
    "codeType": "custom",
    "label": "文本输入",
    "placeholder": "请输入",
    "disabled": false,
    "required": false,
    "isColumn": true,
    "width": 2,
    "componentType": "FormInputText",
    "valid": true,
    "icon": "Document",
    "name": "文本输入"
  },
  {
    "uuid": "9fed36f1-b031-1ffa-2ad9-2446c375c8fe",
    "type": "simple",
    "formValueCode": "2",
    "codeType": "custom",
    "label": "下拉选择",
    "placeholder": "请选择",
    "disabled": false,
    "multiple": false,
    "isColumn": true,
    "width": 2,
    "componentType": "FormInputSelect",
    "options": [
      {
        "label": "选项1",
        "value": "1"
      }
    ],
    "valid": true,
    "icon": "Document",
    "name": "下拉选择"
  },
  {
        uuid: '2c82156f-2cc2-abe1-f7fd-10bc3671774e',
        type: 'simple',
        formValueCode: '333',
        codeType: 'custom',
        label: '金额输入',
        placeholder: '请输入',
        disabled: false,
        required: false,
        isColumn: true,
        precision: 2,
        width: 2,
        componentType: 'FormInputMoney',
        valid: false,
        icon: 'Document',
        name: '金额输入'
    },
    {
        uuid: '323515c0-ea7c-e5e1-ae28-9f37dd1d281f',
        type: 'simple',
        formValueCode: '1111',
        codeType: 'custom',
        label: '手机号输入',
        placeholder: '请输入',
        disabled: false,
        required: true,
        defaultValue: '13308749289',
        isColumn: true,
        width: 2,
        componentType: 'FormInputPhone',
        valid: false,
        icon: 'Document',
        name: '手机号输入'
    },
    {
        uuid: '942365e6-938c-5694-ea81-8679d4ced15b',
        type: 'simple',
        formValueCode: '333dd',
        codeType: 'custom',
        label: '邮箱输入',
        placeholder: '请输入',
        disabled: false,
        required: true,
        isColumn: true,
        defaultValue: '123@qq.com',
        width: 2,
        componentType: 'FormInputEmail',
        valid: false,
        icon: 'Document',
        name: '邮箱输入'
    },
  {
    "uuid": "3bc1a3d8-4844-5524-9730-b5674cee01c4",
    "type": "simple",
    "formValueCode": "3",
    "codeType": "custom",
    "label": "数字输入",
    "placeholder": "请输入",
    "disabled": false,
    "required": false,
    "isColumn": true,
    "precision": null,
    "width": 2,
    "componentType": "FormInputNumber",
    "valid": true,
    "icon": "Document",
    "name": "数字输入"
  },
  {
    "uuid": "0b71d994-56cd-a063-70d2-7ff7aa505fcd",
    "type": "simple",
    "formValueCode": "4",
    "codeType": "custom",
    "label": "日期选择",
    "placeholder": "请选择",
    "disabled": false,
    "required": false,
    "isColumn": true,
    "width": 1,
    "dateType": "date",
    "componentType": "FormInputDate",
    "valid": true,
    "icon": "Document",
    "name": "日期选择"
  },
  {
    "uuid": "b52d09d6-43e9-86d6-db3f-315038f95e8e",
    "type": "simple",
    "formValueCode": "5",
    "codeType": "custom",
    "label": "开关组件",
    "disabled": false,
    "required": false,
    "width": 2,
    "isColumn": true,
    "componentType": "FormInputSwitch",
    "switchInfo": {
      "open": "是",
      "close": "否"
    },
    "valid": true,
    "icon": "Open",
    "name": "开关组件"
  },
  {
    "uuid": "635ccf08-8001-2585-b166-8fdad5a75d76",
    "type": "simple",
    "formValueCode": "6",
    "codeType": "custom",
    "label": "单选框",
    "placeholder": "请选择",
    "disabled": false,
    "width": 4,
    "isColumn": true,
    "componentType": "FormInputRadio",
    "options": [
      {
        "label": "选项1",
        "value": "1"
      }
    ],
    "valid": true,
    "icon": "Document",
    "name": "单选框"
  },
  {
    "uuid": "5dac13bb-5ae7-2980-0a70-74fcf250c267",
    "type": "simple",
    "formValueCode": "7",
    "codeType": "custom",
    "label": "多选框",
    "placeholder": "请选择",
    "disabled": false,
    "width": 4,
    "multiple": true,
    "componentType": "FormInputCheckbox",
    "isColumn": true,
    "options": [
      {
        "label": "选项1",
        "value": "1"
      }
    ],
    "valid": true,
    "icon": "Document",
    "name": "多选框",
    "defaultValue": []
  }
] )
function showModal(cb: () => void, data?: any): void {
    callback.value = cb
    formData.value = {}
    if (data) {
      modelConfig.title = '编辑'
    } else {
      modelConfig.title = '添加'
    }
    modelConfig.visible = true
}

function okEvent() {
  formEngineRef.value.validateForm((valid: boolean) => {
    if (valid) {
      console.log('表单保存', JSON.parse(JSON.stringify(formData.value)))
      modelConfig.visible = false
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
  