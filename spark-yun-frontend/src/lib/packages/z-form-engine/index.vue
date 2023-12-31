<template>
    <div class="zqy-form-engine">
        <!-- 左侧组件选择 -->
        <form-widget
            v-if="isDragger"
            v-model="movingInstance"
            @add-form-item="addFormItem"
            @dbclick-add="dbclickAddItem"
            @removeInstance="removeInstance"
        ></form-widget>
        <!-- 中间表单组件拖拽 -->
        <form-components
            ref="formComponentRef"
            v-model="formData"
            :componentList="componentList"
            :currentInstance="instanceConfig.chooseItemData"
            :isDragger="isDragger"
            :renderSence="renderSence"
            :movingInstance="movingInstance"
            @componentListChange="componentListChange"
            @chooseItem="chooseItem"
            @removeInstance="removeInstance"
        ></form-components>
        <!-- 右侧组件配置 -->
        <form-components-config
            v-if="isDragger"
            v-model="instanceConfig.chooseItemData"
            :formConfig="instanceConfig.chooseItemData"
            :configList="instanceConfig.chooseItemConfigList"
            :getTableCodesMethod="getTableCodesMethod"
        ></form-components-config>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, defineProps, withDefaults, defineEmits, computed, watch, defineExpose } from 'vue'
import FormWidget from './form-widget/index.vue'
import FormComponents from './form-components/index.vue'
import FormComps from './form-widget/form-components'
import FormComponentsConfig from './form-components-config/index.vue'
import { cloneDeep } from 'lodash-es'

import { ComponentInstance } from './form-engine.interface'

interface InstanceConfig {
    chooseItemData: ComponentInstance | null
    chooseItemConfigList: string[]
}
const props = withDefaults(defineProps<{
    renderSence?: string
    modelValue: any
    isDragger?: boolean
    formConfigList: ComponentInstance[]
    getTableCodesMethod?: Function
}>(), {
    renderSence: 'new',
    isDragger: false,
    formConfigList: []
})
const emit = defineEmits(['update:modelValue'])
const componentList = ref<ComponentInstance[]>([])
const formComponentRef = ref()
const formComps = reactive(FormComps)
// 右侧配置相关参数
const instanceConfig = reactive<InstanceConfig>({
    chooseItemData: null,
    chooseItemConfigList: []
})
const movingInstance = ref<ComponentInstance>()

const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

watch(() => props.formConfigList, (e: ComponentInstance[]) => {
    componentList.value = cloneDeep(e)
}, { immediate: true, deep: true })

// 左侧组件选择事件
function addFormItem(instance: ComponentInstance | null) {
    instanceConfig.chooseItemData = instance
    if (instance) {
        if (!componentList.value.some(ins => ins.uuid === instanceConfig.chooseItemData?.uuid)) {
            componentList.value.push(instance)
        }
        try {
            instanceConfig.chooseItemConfigList = formComps.find(item => item.editConfig.code === instanceConfig.chooseItemData.componentType).conponentSetConfig
        } catch (error) {
            console.error('请确认组件类型是否注册 001')   
        }
    }
}

function dbclickAddItem(instance: ComponentInstance) {
    componentList.value.push(instance)
    instanceConfig.chooseItemData = instance
    try {
        instanceConfig.chooseItemConfigList = formComps.find(item => item.editConfig.code === instanceConfig.chooseItemData.componentType).conponentSetConfig
    } catch (error) {
        console.error('请确认组件类型是否注册 002')   
    }
}

// 中间表单事件
function chooseItem(data: ComponentInstance) {
    instanceConfig.chooseItemData = data
    try {
        instanceConfig.chooseItemConfigList = formComps.find(item => item.editConfig.code === instanceConfig.chooseItemData.componentType).conponentSetConfig
    } catch (error) {
        console.error('请确认组件类型是否注册 003')   
    }
}
function componentListChange(e: any[]) {
    componentList.value = e
}
function removeInstance(e: ComponentInstance) {
    if (instanceConfig.chooseItemData?.uuid === e.uuid) {
        instanceConfig.chooseItemData = null
    }
    componentList.value = componentList.value.filter((config: ComponentInstance) => config.uuid !== e.uuid)
}

// expose method
function getFormItemConfigList() {
    return componentList.value || []
}

function validateForm(callback: any) {
    formComponentRef.value?.validateForm(callback)
}

defineExpose({
    getFormItemConfigList,
    validateForm
})
</script>

<style lang="scss">
.zqy-form-engine {
    width: 100%;
    display: flex;
    height: 100%;
    // height: calc(100vh - 4px);
}
</style>
