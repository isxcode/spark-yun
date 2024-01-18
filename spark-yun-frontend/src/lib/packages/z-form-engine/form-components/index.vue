<template>
    <div class="form-components" :class="{ 'form-components__dragger': isDragger }">
        <el-scrollbar>
            <el-form
                class="form-component-container"
                ref="ZFormEngineRef"
                :model="formData"
                :label-position="'top'"
                :disabled="renderSence === 'readonly'"
            >
                <draggable
                    class="form-dragger-component"
                    itemKey="uuid"
                    group="ZFormEngineConfig"
                    :ghost-class="ghostClass"
                    :animation="150"
                    :list="componentList"
                    :force-fallback="true"
                    :fallback-class="true"
                    :disabled="!isDragger"
                    :touchStartThreshold="2"
                    :scroll="false"
                    @start="startMoveEvent"
                    @end="endMoveEvent"
                >
                    <template #item="{ element }">
                        <span :class="{'form-item-container__error': !element.valid}" class="form-item-container" :style="{ 'width': componentWidth(element.width) }">
                            <component
                                :class="{ 'choose-item__active': chooseItemData.uuid === element.uuid && isDragger, 'choose-item': isDragger }"
                                v-model="formData[element.uuid]"
                                :is="computedRenderSenceComponent(element.componentType)"
                                :formConfig="element"
                                :formData="formData"
                                :isDragger="isDragger"
                                :renderSence="renderSence"
                                @removeInstance="removeInstance"
                                @mousedown="mousedownEvent($event, element)"
                            ></component>
                        </span>
                    </template>
                </draggable>
                <EmptyPage class="form-component-container__empty" v-if="!componentList.length"></EmptyPage>
            </el-form>
        </el-scrollbar>
    </div>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed, markRaw, ref, shallowRef, watch, nextTick } from 'vue'
import draggable from 'vuedraggable'
import FormInstance from './form-instance'
import { ComponentInstance } from '../form-engine.interface'

const props = defineProps(['modelValue', 'componentList', 'currentInstance', 'isDragger', 'renderSence', 'movingInstance'])
const emit = defineEmits(['update:modelValue', 'componentListChange', 'chooseItem', 'removeInstance'])
const formInstance = shallowRef<any>(FormInstance)
const chooseItemData = ref<ComponentInstance>({})

const ZFormEngineRef = ref()
const componentWidth = computed(() => {
    return (d: number) => {
        return `${d * 25}%`
    }
})
const formData = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})
const ghostClass = computed(() => {
    return props?.movingInstance?.width === 4 ? 'ghost__all' : 'ghost'
})
watch(() => props.componentList, (e) => {
    emit('componentListChange', e)
}, {
    deep: true
})
watch(() => props.currentInstance?.uuid, (e: ComponentInstance) => {
    if (e) {
        chooseItemData.value = props.currentInstance
    }
})
const computedRenderSenceComponent = computed(() => {
    return (componentType: string) => {
        return markRaw(formInstance.value[componentType])
    }
})

const mousedownEvent = (e: EventListener, data: ComponentInstance) => {
    emit('chooseItem', data)
}

function endMoveEvent(e: any) {
    let path = e.originalEvent.path || (e.originalEvent.composedPath && e.originalEvent.composedPath())
    if (path && !path.some(el => el.className === 'form-dragger-component')) {
        emit('removeInstance', chooseItemData.value)
    }
}
function startMoveEvent(e: any) {
    chooseItemData.value = e.item.__draggable_context.element
}
function removeInstance(e: ComponentInstance) {
    emit('removeInstance', e)
}

function validateForm(callback: any) {
    ZFormEngineRef.value?.validate((valid: boolean) => {
        callback(valid)
    })
}

defineExpose({
    validateForm
})
</script>

<style lang="scss">
.form-components {
    width: 100%;
    height: 100%;
    &.form-components__dragger {
        border-left: 1px solid getCssVar('border-color');
        border-right: 1px solid getCssVar('border-color');
    }
    .form-component-container {
        padding: 12px;
        height: 100%;
        box-sizing: border-box;
        position: relative;
        .form-component-container__empty {
            top: 0;
            z-index: 1;
        }
        .form-dragger-component {
            z-index: 10;
            height: 100%;
            position: relative;
            .form-item-container {
                display: inline-block;
                &.form-item-container__error {
                    .el-form-item {
                        &.form-input__dragger {
                            background-color: getCssVar('color', 'error', 'light-9');
                            user-select: none !important;
                        }
                    }
                }
            }
            .ghost {
                width: 50%;
                display: inline-block;
                opacity: .7;
                background-color: getCssVar('color', 'white');
                .draggable-icon {
                    display: none;
                }
                .draggable-name {
                    display: none;
                }
                .shadow-instance {
                    &.el-form-item {
                        &.form-render-item {
                            display: inline-block !important;
                            margin-bottom: 12px;
                            padding: 12px 18px;
                            box-sizing: border-box;
                            margin-bottom: 0 !important;
                            position: relative;
                            .el-form-item__label {
                                margin-bottom: 8px !important;
                            }
                        }
                    }
                }
            }
            .ghost__all {
                width: 100%;
                display: inline-block;
                opacity: .7;
                background-color: getCssVar('color', 'white');
                .draggable-icon {
                    display: none;
                }
                .draggable-name {
                    display: none;
                }
                .shadow-instance {
                    &.el-form-item {
                        &.form-render-item {
                            display: inline-block !important;
                            margin-bottom: 12px;
                            padding: 12px 18px;
                            box-sizing: border-box;
                            margin-bottom: 0 !important;
                            position: relative;
                            .el-form-item__label {
                                margin-bottom: 8px !important;
                            }
                        }
                    }
                }
            }
            .choose-item {
                background-color: getCssVar('color', 'white');
                margin-bottom: 0px;
                .el-input__inner {
                    pointer-events: none !important;
                }
                &:active {
                    background-color: #f1f1f1;
                }
                &.choose-item__active {
                    background-color: getCssVar('color', 'primary', 'light-8') !important;
                    &.form-render-item {
                        .form-render-item__btn {
                            display: flex;
                        }
                    }
                }
            }
        }
    }
    .el-scrollbar {
        .el-scrollbar__wrap {
            .el-scrollbar__view {
                height: 100%;
                padding-bottom: 20px;
                box-sizing: border-box;
            }
        }
    }
}
</style>
