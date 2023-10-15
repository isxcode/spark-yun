<template>
    <div class="data-sync-body" id="container">
        <div class="source-table-container">
            <el-table ref="sourceTableRef" :data="sourceTableData" row-key="code" border>
                <el-table-column prop="code" label="字段名" />
                <el-table-column prop="type" label="类型" />
                <el-table-column label="操作" :show-overflow-tooltip="true">
                    <template #default="scope">
                        {{ scope.row.sql }}
                        <el-dropdown trigger="click">
                            <el-icon class="option-more" @click.stop>
                                <MoreFilled />
                            </el-icon>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item>
                                        添加
                                    </el-dropdown-item>
                                    <el-dropdown-item>
                                        删除
                                    </el-dropdown-item>
                                    <el-dropdown-item>
                                        自定义
                                    </el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </template>
                </el-table-column>
            </el-table>
            <ul class="source-link-pointer">
                <li v-for="row in sourceTableData" :key="row.code">
                    <div class="lint-pointer" :class="`leftRow code-source-${row.code}`"></div>
                </li>
            </ul>
        </div>
        <div class="target-table-container">
            <el-table ref="targetTableRef" :data="targetTableData" row-key="code" border
                :row-class-name="({ row }) => `rightRow code-target-${row.code}`">
                <el-table-column prop="code" label="字段名" />
                <el-table-column prop="type" label="类型" />
            </el-table>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, defineProps, onMounted, nextTick } from 'vue'
import { jsPlumb } from 'jsplumb'
import { GetTableColumnsByTableId } from '@/services/data-sync.service'

interface connect {
    source: string
    target: string
}
interface TableDetailParam {
    dataSourceId: string
    tableName: string
}

interface FormData {
    workId: string     // 作业id
    sourceDBType: string    // 来源数据源类型
    sourceDBId: string      // 来源数据源
    sourceTable: string      // 来源数据库表名
    queryCondition: string   // 来源数据库查询条件

    targetDBType: string    // 目标数据库类型
    targetDBId: string     // 目标数据源
    targetTable: string     // 目标数据库表名
    overMode: string         // 写入模式
    columMapping?: any[]      // 字段映射关系
}

defineProps<{
    formData: FormData
}>()

let instance: any = null
const connectNodeList = ref<connect[]>([])
const sourceTableData = ref([
    // {
    //     code: 'name',
    //     type: 'int',
    //     sql: 'show databasesshow databasesshow databasesshow databasesshow databases'
    // }
])
const targetTableData = ref([
    // {
    //     code: 'age',
    //     type: 'int'
    // }
])

// 根据表名获取映射表字段
function getTableColumnData(params: TableDetailParam, type: string) {
    GetTableColumnsByTableId(params).then((res: any) => {
        console.log('res', res)
        // type === 'source' ? sourceList.value = options : targetList.value = options
    }).catch(err => {
        console.error(err)
        // type === 'source' ? sourceList.value = [] : targetList.value = []
    })
}

function tableLinkInit() {
    instance = jsPlumb.getInstance({
        Connector: 'Straight', //连接线形状 Bezier: 贝塞尔曲线 Flowchart: 具有90度转折点的流程线 StateMachine: 状态机 Straight: 直线
        PaintStyle: { strokeWidth: 2, stroke: '#ff7c06' }, //连接线样式
        Endpoint: ['Blank', { radius: 1 }], //端点
        Anchor: 'Right',
        // 绘制箭头
        ConnectionOverlays: [['Arrow', { width: 6, length: 6, location: 1 }],
        ['Label', {
            label: '<span class="delete-node-btn"><svg t="1695102875148" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5710" width="64" height="64"><path d="M512.42496 512.28672m-336.20992 0a336.20992 336.20992 0 1 0 672.41984 0 336.20992 336.20992 0 1 0-672.41984 0Z" fill="#FFFFFF" p-id="5711"></path><path d="M512.667 1012.954c-276.425 0-500.513-224.090-500.513-500.513s224.090-500.513 500.513-500.513 500.513 224.090 500.513 500.513-224.090 500.513-500.513 500.513zM751.919 326.369c6.81-6.8 11.022-16.197 11.022-26.58s-4.212-19.781-11.022-26.58c-6.8-6.81-16.198-11.022-26.58-11.022-10.383 0-19.781 4.212-26.58 11.022l-186.081 186.081-186.089-186.081c-6.8-6.81-16.197-11.022-26.58-11.022s-19.781 4.212-26.58 11.022c-6.81 6.8-11.022 16.198-11.022 26.58 0 10.383 4.212 19.781 11.022 26.58l186.081 186.081-186.081 186.081c-6.996 6.834-11.334 16.365-11.334 26.908 0 20.769 16.837 37.607 37.607 37.607 10.535 0 20.057-4.332 26.885-11.31l186.087-186.122 186.081 186.123c6.833 6.986 16.355 11.317 26.891 11.317 20.769 0 37.607-16.837 37.607-37.607 0-10.542-4.336-20.074-11.327-26.9l-186.089-186.098 186.089-186.081z" fill="#eb5463" p-id="5712"></path></svg></span>',
            location: 0.8,
            labelStyle: {
                color: 'red'
            },
            cssClass: 'endpointLabel'
        }]
        ],
        EndpointStyle: { fill: '#000000' }, //端点样式
        Container: 'container' //目标容器id
    })
}
// 设置可以连线的元素
function setContainer() {
    const leftElList = document.querySelectorAll('.leftRow') // 左侧行元素集合
    const rightElList = document.querySelectorAll('.rightRow') // 右侧行元素集合
    // 将dom元素设置为连线的起点或者终点 设置了起点的元素才能开始连线 设置为终点的元素才能为连线终点
    instance.batch(function () {
        [leftElList, rightElList].forEach((trList, index) => {
            trList.forEach((tr) => {
                if (index === 0) {
                    instance.makeSource(tr, {
                        allowLoopback: false,
                        anchor: ['Right'], // 设置端点位置
                        maxConnections: -1
                    })
                } else {
                    // 判断是否有子项,若没有则设置为终点
                    instance.makeTarget(tr, {
                        anchor: ['Left'],
                        maxConnections: 1
                    })
                }
            })
        })
    })
}
// 截取元素类名中的id
const interceptId = (className) => {
    return className.slice(className.indexOf('-') + 1)
}
const initJsPlumb = () => {
    jsPlumb.ready(function () {
        // 初始化jsPlumb 创建jsPlumb实例
        tableLinkInit()
        // 设置可以为连线起点和连线终点的元素
        setContainer()

        instance.bind('click', (conn: unknown, originalEvent: any) => {
            if (originalEvent.target.className === 'delete-node-btn') {
                instance.deleteConnection(conn)
                nextTick(() => {
                    try {
                        getLinkData()
                    } catch (error) {
                        console.log('整理关联节点失败，请排查原因')
                    }
                })
            }
        })
    })
}

function getLinkData() {
    const connectList: connect[] = []
    instance.getConnections().forEach((con: any) => {
        const conItem = {
            source: con.source.className.split(' ').filter((cls: string) => cls.match('code-source-'))[0].slice(12),
            target: con.target.className.split(' ').filter((cls: string) => cls.match('code-target-'))[0].slice(12)
        }
        connectList.push(conItem)
    })
    connectNodeList.value = connectList
}


// 设置默认连线
function clickSelectLinkConnect(type: string) {
    connectNodeList.value = []
    instance.deleteEveryConnection()
    if (['SameLine', 'SameName'].includes(type)) {
        sourceTableData.value.forEach((column: any, index: number) => {
            if (type === 'SameLine' && targetTableData.value[index]) {
                connectNodeList.value.push({
                    source: column.code,
                    target: targetTableData.value[index].code
                })
            }
            if (type === 'SameName' && targetTableData.value.find(c => c.code === column.code)) {
                connectNodeList.value.push({
                    source: column.code,
                    target: column.code
                })
            }
        })
        setTimeout(() => {
            connectNodeList.value.forEach((data: any) => {
                instance.connect({
                    source: document.querySelector(`.code-source-${data.source}`),
                    target: document.querySelector(`.code-target-${data.target}`)
                })
            })
        })
    } else if (type === 'quitLine') {
        instance.deleteEveryConnection()
    } else if (type === 'resetLine') {
        connectNodeList.value
    }
}

onMounted(() => {
    initJsPlumb()
    window.addEventListener('resize', function () {
        // 在窗口大小调整时执行的操作
        nextTick(() => {
            setTimeout(() => {
                getLinkData()
                instance.deleteEveryConnection()
                connectNodeList.value.forEach((data: any) => {
                    instance.connect({
                        source: document.querySelector(`.code-source-${data.source}`),
                        target: document.querySelector(`.code-target-${data.target}`)
                    })
                })
            })
        })
    });
})

defineExpose({
    getTableColumnData
})
</script>

<style lang="scss">
.data-sync-body {
    display: flex;
    position: relative;
    width: 99%;

    .source-table-container {
        width: 100%;
        margin-right: 100px;
        position: relative;

        .source-link-pointer {
            list-style: none;
            width: 6px;
            height: calc(100% - 40px);
            position: absolute;
            top: 40px;
            right: -3px;
            z-index: 100;
            padding: 0;
            margin: 0;
            box-sizing: content-box;

            li {
                height: 40px;
                display: flex;
                align-items: center;
                justify-content: flex-end;

                // pointer-events: all;
                .lint-pointer {
                    height: 6px;
                    width: 6px;
                    border-radius: 50%;
                    background-color: #ff7c06;
                    z-index: 1000;
                    // cursor: pointer;
                    transform: scale(1);
                    transition: transform 0.15s linear;

                    &:hover {
                        transform: scale(2);
                        transition: transform 0.15s linear;
                    }
                }
            }
        }
    }

    .target-table-container {
        width: 100%;
        overflow: auto;

        .el-table {
            // width: 100%;

            .el-table__row {
                &:hover {
                    td.el-table__cell {
                        background-color: unset;
                    }
                }
            }
        }

    }

    .el-dropdown {
        position: absolute;
        right: 4px;
        top: 15px;

        .option-more {
            font-size: 14px;
            transform: rotate(90deg);
            cursor: pointer;
            color: $--app-info-color;
        }
    }


    .jtk-overlay {
        &.jtk-hover {
            .delete-node-btn {
                // display: flex;
                transition: all 0.15s linear;
                visibility: visible;
                opacity: 1;
            }
        }

        .delete-node-btn {
            border-radius: 50%;
            padding: 0;
            width: 16px;
            height: 16px;
            font-size: 16px;
            display: flex;
            justify-content: center;
            align-items: center;
            color: #ffffff;
            background-color: #F56C6C !important;
            border: 1px solid #F56C6C;
            outline: none;
            cursor: pointer;
            transition: all 0.15s linear;
            background-color: #ffffff;
            transform: scale(.72);
            visibility: hidden;
            opacity: 0;

            .icon {
                pointer-events: none;
            }
        }
    }

    .jtk-endpoint-full {
        display: none;
    }

    .jtk-connector {
        // z-index: 10;
    }

    .jtk-endpoint-connected {
        display: none;
    }
}
</style>