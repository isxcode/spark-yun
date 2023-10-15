<template>
    <div class="data-sync-page">
        <div class="data-sync__option-container">
            <div class="btn-box" @click="goBack">
                <el-icon>
                    <RefreshLeft />
                </el-icon>
                <span class="btn-text">返回</span>
            </div>
            <div class="btn-box">
                <el-icon>
                    <Finished />
                </el-icon>
                <span class="btn-text">保存</span>
            </div>
            <div class="btn-box">
                <el-icon>
                    <Setting />
                </el-icon>
                <span class="btn-text">配置</span>
            </div>
            <div class="btn-box">
                <el-icon>
                    <RefreshLeft />
                </el-icon>
                <span class="btn-text">定位</span>
            </div>
        </div>
        <div class="data-sync" id="data-sync">
            <div class="data-sync-top">
                <el-card class="box-card">
                    <template #header>
                        <div class="card-header">
                            <span>数据来源</span>
                        </div>
                    </template>
                    <el-form ref="form" label-position="left" label-width="70px" :model="formData" :rules="rules">
                        <el-form-item prop="sourceDBType" label="类型">
                            <el-select v-model="formData.sourceDBType" placeholder="请选择">
                                <el-option v-for="item in typeList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <el-form-item prop="sourceDBId" label="数据源">
                            <el-select v-model="formData.sourceDBId" placeholder="请选择"
                                @visible-change="getDataSource($event, formData.sourceDBType, 'source')">
                                <el-option v-for="item in sourceList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <el-form-item prop="sourceTable" label="表">
                            <el-select v-model="formData.sourceTable" placeholder="请选择"
                                @visible-change="getDataSourceTable($event, formData.sourceDBId, 'source')"
                                @change="tableChangeEvent($event, formData.sourceDBId, 'source')"
                            >
                                <el-option v-for="item in sourceTablesList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                            <el-button type="primary" link @click="showTableDetail">数据预览</el-button>
                        </el-form-item>
                        <el-form-item prop="queryCondition" label="过滤条件">
                            <code-mirror v-model="formData.queryCondition" basic :lang="lang" />
                        </el-form-item>
                    </el-form>
                </el-card>
                <el-card class="box-card">
                    <template #header>
                        <div class="card-header">
                            <span>数据去向</span>
                        </div>
                    </template>
                    <el-form ref="form" label-position="left" label-width="70px" :model="formData" :rules="rules">
                        <el-form-item prop="targetDBType" label="类型">
                            <el-select v-model="formData.targetDBType" placeholder="请选择">
                                <el-option v-for="item in typeList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <el-form-item prop="targetDBId" label="数据源">
                            <el-select v-model="formData.targetDBId" placeholder="请选择"
                                @visible-change="getDataSource($event, formData.targetDBType, 'target')">
                                <el-option v-for="item in targetList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                        <el-form-item prop="targetTable" label="表">
                            <el-select v-model="formData.targetTable" placeholder="请选择"
                                @visible-change="getDataSourceTable($event, formData.targetDBId, 'target')"
                                @change="tableChangeEvent($event, formData.targetDBId, 'target')"
                            >
                                <el-option v-for="item in targetTablesList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                            <el-button type="primary" link @click="createTableWork">生成建表作业</el-button>
                        </el-form-item>
                        <el-form-item prop="overMode" label="写入模式">
                            <el-select v-model="formData.overMode" placeholder="请选择">
                                <el-option v-for="item in overModeList" :key="item.value" :label="item.label"
                                    :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-form>
                </el-card>
            </div>
            <div class="select-link-type">
                <el-button v-for="button in buttons" :key="button.text" :type="button.type" link
                    @click="clickSelectLinkConnect(button.code)">{{ button.text }}</el-button>
            </div>
            <data-sync-table ref="dataSyncTableRef" :formData="formData"></data-sync-table>
        </div>
        <!-- 数据预览 -->
        <table-detail ref="tableDetailRef"></table-detail>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import CodeMirror from 'vue-codemirror6'
import { sql } from '@codemirror/lang-sql'
import { DataSourceType, OverModeList } from './data.config.ts'
import { GetDatasourceList } from '@/services/datasource.service'
import { CreateTableWork, GetDataSourceTables } from '@/services/data-sync.service'
import TableDetail from './table-detail/index.vue'
import DataSyncTable from './data-sync-table/index.vue'

interface Option {
    label: string
    value: string
}

const buttons = ref([
    { type: 'primary', text: '同行映射', code: 'SameLine' },
    { type: 'primary', text: '同名映射', code: 'SameName' },
    //   { type: 'primary', text: '智能映射', code: 'SameLine' },
    { type: 'primary', text: '取消映射', code: 'quitLine' },
    { type: 'primary', text: '重置映射', code: 'resetLine' },
])
const form = ref<FormInstance>()
const tableDetailRef = ref()
const dataSyncTableRef = ref()
const lang = ref<any>(sql())
const sourceList = ref<Option[]>([])
const targetList = ref<Option[]>([])
const sourceTablesList = ref<Option[]>([])
const targetTablesList = ref<Option[]>([])
const overModeList = ref<Option[]>(OverModeList)

const typeList = ref(DataSourceType);
const formData = reactive({
    workId: '',     // 作业id
    sourceDBType: '',     // 来源数据源类型
    sourceDBId: '',       // 来源数据源
    sourceTable: '',      // 来源数据库表名
    queryCondition: '',   // 来源数据库查询条件

    targetDBType: '',     // 目标数据库类型
    targetDBId: '',       // 目标数据源
    targetTable: '',      // 目标数据库表名
    overMode: '',         // 写入模式
    columMapping: []      // 字段映射关系
})
const rules = reactive<FormRules>({
    // sourceDBType: [
    //     {
    //         required: true,
    //         message: '请选择数据源类型',
    //         trigger: ['blur', 'change']
    //     }
    // ]
})

// 获取数据源
function getDataSource(e: boolean, sourceType: string, type: string) {
    if (e && sourceType) {
        let options = []
        GetDatasourceList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: sourceType || ''
        }).then((res: any) => {
            options = res.data.content.map((item: any) => {
                return {
                    label: item.name,
                    value: item.id
                }
            })
            type === 'source' ? sourceList.value = options : targetList.value = options
        }).catch(err => {
            console.error(err)
            type === 'source' ? sourceList.value = [] : targetList.value = []
        })
    } else {
        type === 'source' ? sourceList.value = [] : targetList.value = []
    }
}

// 获取数据源表
function getDataSourceTable(e: boolean, dataSourceId: string, type: string) {
    if (e && dataSourceId) {
        let options = []
        GetDataSourceTables({
            dataSourceId: dataSourceId
        }).then((res: any) => {
            options = res.data.tables.map((item: any) => {
                return {
                    label: item,
                    value: item
                }
            })
            type === 'source' ? sourceTablesList.value = options : targetTablesList.value = options
        }).catch(err => {
            console.error(err)
            type === 'source' ? sourceTablesList.value = [] : targetTablesList.value = []
        })
    } else {
        type === 'source' ? sourceTablesList.value = [] : targetTablesList.value = []
    }
}
// 数据预览
function showTableDetail(): void {
    if (formData.sourceDBId && formData.sourceTable) {
        tableDetailRef.value.showModal({
            dataSourceId: formData.sourceDBId,
            tableName: formData.sourceTable
        })
    } else {
        ElMessage.warning('请选择数据源和表')
    }
}
// 生成建表作业
function createTableWork() {
    CreateTableWork({
        dataSourceId: formData.sourceDBId,
        tableName: formData.sourceTable
    }).then((res: any) => {
        ElMessage.success('操作成功')
    }).catch(err => {
        console.error(err)
    })
}

function tableChangeEvent(e: string, dataSourceId: string, type: string) {
    dataSyncTableRef.value.getTableColumnData({
        dataSourceId: dataSourceId,
        tableName: e
    }, type)
}

onMounted(() => {

})
</script>

<style lang="scss">
.data-sync-page {
    position: relative;
    padding-top: 44px;
    background-color: #ffffff;
    width: 100%;

    .data-sync__option-container {
        height: $--app-item-height;
        display: flex;
        align-items: center;
        color: $--app-unclick-color;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        padding-left: 20px;
        z-index: 10;
        border-bottom: 1px solid $--app-border-color;

        .btn-box {
            font-size: $--app-small-font-size;
            display: flex;
            cursor: pointer;
            width: 48px;
            margin-right: 8px;

            &.btn-box__4 {
                width: 70px;
            }

            .btn-text {
                margin-left: 4px;
            }

            &:hover {
                color: $--app-primary-color;
            }
        }
    }

    .data-sync {
        width: 100%;
        padding: 20px;
        box-sizing: border-box;
        overflow: auto;
        height: calc(100vh - 156px);
        position: relative;

        .data-sync-top {
            display: flex;
            width: 100%;

            .vue-codemirror {
                height: 100px;
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

                    // display: none !important;
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

            .el-card {
                width: 100%;

                &+.el-card {
                    margin-left: 12px;
                }

                .el-card__header {
                    padding: 0;
                    border: 0;

                    .card-header {
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        height: 32px;
                        font-size: 14px;
                    }
                }

                .el-card__body {
                    .el-form {
                        .el-form-item {
                            .el-form-item__label {
                                position: relative;

                                &::before {
                                    position: absolute;
                                    left: -8px;
                                }
                            }

                            .el-form-item__content {
                                flex-wrap: nowrap;
                                justify-content: flex-end;
                            }
                        }
                    }
                }
            }
        }

        .select-link-type {
            height: 44px;
            display: flex;
            align-items: center;
            font-size: 12px;
            width: 100%;

            .el-select {
                margin-left: 20px;
            }
        }
    }
}</style>
