<template>
    <div class="form-components-widget" :class="{ 'form-components-widget__out': !mouseenterIn }" @mouseleave="mouseoverEvent" @mouseenter="mouseenterEvent">
        <el-scrollbar>
            <div class="input-component">
                <p class="title">常用组件</p>
                <draggable
                    class="form-dragger-widget"
                    itemKey="uuid"
                    drag-class="chosen-item"
                    :group="{ name: 'ZFormEngineConfig', pull: 'clone', put: false }"
                    :move="onMove"
                    :animation="150"
                    :list="formComponentEditConfig"
                    :force-fallback="true"
                    :sort="false"
                    :clone="cloneItemData"
                    @start="startMoveEvent"
                    @end="endMoveEvent"
                >
                    <template #item="{ element }">
                        <div class="edit-item" @dblclick="clickToAdd(element)" v-if="element.type === 'simple'">
                            <el-icon class="draggable-icon"><component :is="element.icon" /></el-icon>
                            <span class="draggable-name">{{element.name}}</span>
                            <component
                                class="shadow-instance"
                                :formData="formDataWidget"
                                :formConfig="element"
                                :is="computedRenderSenceComponent(element.componentType)"
                            ></component>
                        </div>
                    </template>
                </draggable>
            </div>
            <div class="static-component">
                <p class="title">静态组件</p>
                <draggable
                    class="form-dragger-widget form-dragger-component__edit"
                    itemKey="uuid"
                    drag-class="chosen-item"
                    :group="{ name: 'ZFormEngineConfig', pull: 'clone', put: false }"
                    :move="onMove"
                    :animation="150"
                    :list="formComponentEditConfig"
                    :force-fallback="true"
                    :sort="false"
                    :clone="cloneItemData"
                    @start="startMoveEvent"
                    @end="endMoveEvent"
                >
                    <template #item="{ element }">
                        <div class="edit-item" @dblclick="clickToAdd(element)" v-if="element.type === 'static'">
                            <el-icon class="draggable-icon"><component :is="element.icon" /></el-icon>
                            <span class="draggable-name">{{element.name}}</span>
                            <component
                                class="shadow-instance"
                                :formData="formDataWidget"
                                :formConfig="element"
                                :isDragger="true"
                                :is="computedRenderSenceComponent(element.componentType)"
                            ></component>
                        </div>
                    </template>
                </draggable>
            </div>
        </el-scrollbar>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineProps, shallowRef, defineEmits, computed, markRaw } from 'vue'
import draggable from 'vuedraggable'
import FormComponents from './form-components'
import { cloneDeep, clone } from 'lodash-es'
import { ComponentInstance } from '../form-engine.interface'
import FormInstance from '../form-components/form-instance'

const guid = function() {
    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }
    return (S4() + S4() + '-' + S4() + '-' + S4() + '-' + S4() + '-' +S4() + S4() +S4());
}

const props = defineProps(['modelValue'])
const emit = defineEmits([ 'add-form-item', 'dbclick-add', 'removeInstance', 'update:modelValue' ])
const currentItem = ref<ComponentInstance>()
const formComponents = ref(FormComponents)
const formInstance = shallowRef<any>(FormInstance)
const formDataWidget = ref(null)
const mouseenterIn = ref(false)

const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})
const computedRenderSenceComponent = computed(() => {
    return (componentType: string) => {
        return markRaw(formInstance.value[componentType])
    }
})
const formComponentEditConfig = computed(() => {
    return formComponents.value.map(item => {
        return {
            ...item.componentConfig,
            icon: item.editConfig.icon,
            name: item.editConfig.name
        }
    })
})

const onMove = (e: any) => {
    return true
}

function startMoveEvent(e: any) {
    emit('update:modelValue', e.item.__draggable_context.element)
}
function endMoveEvent(e: any) {
    let path = e.originalEvent.path || (e.originalEvent.composedPath && e.originalEvent.composedPath())
    if (path && path.some(el => el.className === 'form-dragger-component')) {
        emit('add-form-item', currentItem.value)
    } else {
        emit('removeInstance', currentItem.value)
        emit('add-form-item', null)
    }
}

function cloneItemData(e: ComponentInstance) {
    const item = cloneDeep(e)
    item.uuid = guid()
    // item.formValueCode = guid()
    currentItem.value = item
    return item
}
function clickToAdd(data: ComponentInstance) {
    const item = cloneDeep(data)
    item.uuid = guid()
    // item.formValueCode = guid()
    currentItem.value = item
    emit('dbclick-add', currentItem.value)
}

function mouseoverEvent() {
    mouseenterIn.value = false
}
function mouseenterEvent() {
    mouseenterIn.value = true
}
</script>

<style lang="scss">
.form-components-widget {
    min-width: 230px;
    width: 230px;
    height: 100%;
    .input-component, .static-component {
        .title {
            padding: 0 8px;
            font-size: 12px;
            margin: 10px 0 0 0;
        }
    }
    &.form-components-widget__out {
        .form-dragger-widget {
            .chosen-item {
                // display: none !important;
                opacity: 0.4 !important;
            }
        }
    }
    .form-dragger-widget {
        box-sizing: border-box;
        padding: 0 8px;
        height: 100%;
        .chosen-item {
            // display: none !important;
        }
        .edit-item {
            margin-top: 8px;
            width: calc(50% - 14px);
            border: 1px solid #b2b2b2;
            border-radius: 2px;
            height: 32px;
            display: inline-flex;
            align-items: center;
            padding-left: 8px;
            font-size: 12px;
            line-height: 32px;
            cursor: grab;
            &:active {
                cursor: grabbing;
            }
            .draggable-name {
                margin-left: 8px;
            }
            &:nth-child(odd) {
                margin-right: 4px;
            }
            &:nth-child(even) {
                margin-left: 4px;
            }

            .shadow-instance {
                &.el-form-item {
                    &.form-render-item {
                        display: none !important;
                    }
                }
            }
        }
    }
}
</style>