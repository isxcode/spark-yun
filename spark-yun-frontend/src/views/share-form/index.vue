<template>
    分享表单
</template>

<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { http } from '@/utils/http'

const route = useRoute()

watch(() => route.params, (e) => {
    console.log('路由', e)
}, {
    immediate: true,
    deep: true
})

function initData() {
    http.request({
        method: 'post',
        url: '/123',
        headers: {
            tenant: 123
        }
    }).then(res => {
        console.log('res')
    })
}

onMounted(() => {
    initData()
    console.log('route', route.params.tenant)
})
</script>