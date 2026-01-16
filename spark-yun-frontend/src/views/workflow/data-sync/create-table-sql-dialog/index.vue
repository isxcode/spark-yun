<template>
    <BlockModal :model-config="modelConfig" @close="closeEvent">
        <div class="create-table-sql-content">
            <div class="sql-body">
                <el-scrollbar v-if="!loading && sqlContent" height="400px">
                    <pre class="sql-pre">{{ sqlContent }}</pre>
                </el-scrollbar>
                <div v-else-if="loading" class="loading-container">
                    <el-icon class="is-loading" :size="40">
                        <Loading />
                    </el-icon>
                    <p>正在生成建表语句...</p>
                </div>
                <el-empty v-else description="暂无数据" />
            </div>
        </div>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, ref, defineExpose } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import BlockModal from '@/components/block-modal/index.vue'
import { GenerateCreateTableSql } from '@/services/data-sync.service'

const sqlContent = ref<string>('')
const loading = ref<boolean>(false)

const modelConfig = reactive({
    title: '建表语句',
    visible: false,
    width: '800px',
    okConfig: {
        title: '一键复制',
        ok: copySql,
        disabled: false,
        loading: false
    },
    cancelConfig: {
        title: '关闭',
        cancel: closeEvent,
        disabled: false,
        hide: true
    },
    needScale: false,
    zIndex: 1000,
    customClass: 'create-table-sql-dialog',
    closeOnClickModal: false
})

interface GenerateSqlParams {
    fromDbType: string
    fromTableName: string
    fromColumnList: any[]
    toDbType: string
}

function showModal(params: GenerateSqlParams): void {
    modelConfig.visible = true
    sqlContent.value = ''
    loading.value = true
    
    GenerateCreateTableSql(params).then((res: any) => {
        sqlContent.value = res.data.createTableSql || ''
        loading.value = false
    }).catch((err: any) => {
        console.error(err)
        loading.value = false
        ElMessage.error('生成建表语句失败')
    })
}

function copySql(): void {
    if (!sqlContent.value) {
        ElMessage.warning('暂无可复制的内容')
        return
    }
    
    navigator.clipboard.writeText(sqlContent.value).then(() => {
        ElMessage.success('复制成功')
    }).catch(() => {
        // 降级方案
        const textarea = document.createElement('textarea')
        textarea.value = sqlContent.value
        textarea.style.position = 'fixed'
        textarea.style.opacity = '0'
        document.body.appendChild(textarea)
        textarea.select()
        try {
            document.execCommand('copy')
            ElMessage.success('复制成功')
        } catch (err) {
            ElMessage.error('复制失败')
        }
        document.body.removeChild(textarea)
    })
}

function closeEvent(): void {
    modelConfig.visible = false
    sqlContent.value = ''
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.create-table-sql-dialog {
    .modal-content {
        .create-table-sql-content {
            padding: 0;

            .sql-body {
                padding: 20px;

                .loading-container {
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    height: 400px;
                    color: getCssVar('color', 'info');

                    p {
                        margin-top: 16px;
                        font-size: 14px;
                    }
                }

                .sql-pre {
                    margin: 0;
                    padding: 16px;
                    background-color: #f5f7fa;
                    border-radius: 4px;
                    font-family: 'Courier New', Courier, monospace;
                    font-size: 13px;
                    line-height: 1.6;
                    color: #303133;
                    white-space: pre-wrap;
                    word-wrap: break-word;
                }
            }
        }
    }
}
</style>

