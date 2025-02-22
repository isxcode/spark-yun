<template>
    <code-mirror
        ref="codeMirrorRef"
        v-model="formData"
        basic
        :lang="lang"
        v-bind="$attrs"
        :placeholder="placeholder || '请输入'"
        @change="changEvent"
    />
</template>

<script lang="ts" setup>
import CodeMirror from 'vue-codemirror6'
import { defineProps, defineEmits, computed, ref, onMounted, nextTick } from 'vue'

const props = defineProps(['modelValue', 'lang', 'placeholder'])
const emit = defineEmits(['update:modelValue', 'change'])

const formData = computed({
    get() {
        return props.modelValue || ''
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

function changEvent(e: any) {
    emit('change', e)
}
</script>

<style lang="scss">
.vue-codemirror {
    .cm-editor {
        .cm-panels {
            display: none;
        }
    }
}
</style>
