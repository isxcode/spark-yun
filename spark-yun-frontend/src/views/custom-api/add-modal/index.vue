<template>
    <BlockModal :model-config="modelConfig">
        <el-steps class="custom-api-form__step" :active="stepIndex" finish-status="success">
            <el-step title="基础配置" />
            <el-step title="接口配置" />
            <el-step title="接口检测" />
        </el-steps>
        <el-form ref="form" class="custom-api-form" label-position="top" :model="formData" :rules="rules">
            <div class="api-item" v-if="stepIndex === 0">
                <!-- 基础配置 -->
                <!-- <div class="item-title">基础配置</div> -->
                <el-form-item label="名称" prop="name">
                    <el-input v-model="formData.name" maxlength="200" placeholder="请输入" />
                </el-form-item>
                <el-form-item label="请求方式" prop="apiType">
                    <el-select v-model="formData.apiType" placeholder="请选择">
                        <el-option label="GET" value="GET" />
                        <el-option label="POST" value="POST" />
                    </el-select>
                </el-form-item>
                <el-form-item label="自定义访问路径" prop="path">
                    <el-tooltip
                        content="路径规则：/path1、/path1/path2、/path1/path2/path3，仅支持三级"
                        placement="top"
                    >
                        <el-icon style="left: 92px" class="tooltip-msg"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    <el-input v-model="formData.path" maxlength="1000" placeholder="请输入" />
                </el-form-item>
                <el-form-item label="数据源" prop="datasourceId" v-if="!isEdit">
                    <el-select v-model="formData.datasourceId" placeholder="请选择" @visible-change="getDataSourceList">
                        <el-option
                            v-for="item in dataSourceList"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input
                        v-model="formData.remark"
                        type="textarea"
                        maxlength="200"
                        :autosize="{ minRows: 4, maxRows: 4 }"
                        placeholder="请输入"
                    />
                </el-form-item>
            </div>
            <div class="api-item" v-if="stepIndex === 1">
                <!-- 接口配置 -->
                <!-- <div class="item-title">请求配置</div> -->
                <el-form-item label="请求头模式">
                    <el-tooltip
                        content="任何人访问：无权限拦截。系统认证：通过至轻云用户权限拦截。自定义：用户自定义请求头拦截"
                        placement="top"
                    >
                        <el-icon style="left: 68px" class="tooltip-msg"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    <el-radio-group v-model="formData.tokenType" size="small" @change="tokenTypeChangeEvent">
                        <el-radio-button label="ANONYMOUS">任何人访问</el-radio-button>
                        <el-radio-button label="SYSTEM">系统认证</el-radio-button>
                        <el-radio-button label="CUSTOM">自定义</el-radio-button>
                    </el-radio-group>
                </el-form-item>
                <el-form-item
                    v-if="formData.tokenType === 'CUSTOM'"
                    label="请求头设置"
                    prop="reqHeader"
                    :class="{ 'show-screen__full': reqHeaderFullStatus }"
                >
                    <!-- <el-icon class="modal-full-screen" @click="fullScreenEvent('reqHeaderFullStatus')"><FullScreen v-if="!reqHeaderFullStatus" /><Close v-else /></el-icon>
          <code-mirror v-model="formData.headerToken" basic :lang="jsonLang"/> -->
                    <span class="add-btn">
                        <el-icon @click="addNewOption"><CirclePlus /></el-icon>
                    </span>
                    <div class="form-options__list">
                        <div class="form-options__item" v-for="(element, index) in formData.reqHeader" :key="index">
                            <div class="input-item">
                                <span class="item-label">键</span>
                                <el-input v-model="element.label" placeholder="请输入"></el-input>
                            </div>
                            <div class="input-item">
                                <span class="item-label">值</span>
                                <el-input v-model="element.value" placeholder="请输入"></el-input>
                            </div>
                            <div class="option-btn">
                                <el-icon
                                    v-if="formData.reqHeader && formData.reqHeader.length > 1"
                                    class="remove"
                                    @click="removeItem(index)"
                                >
                                    <CircleClose />
                                </el-icon>
                            </div>
                        </div>
                    </div>
                </el-form-item>
                <el-form-item label="开启分页">
                    <el-tooltip
                        content="分页系统参数如下：$system.page（分页的页数，从0开始），$system.pageSize（分页的每页条数），$system.count（总条数）"
                        placement="top"
                    >
                        <el-icon style="left: 54px" class="tooltip-msg"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    <el-switch v-model="formData.pageType" />
                </el-form-item>
                <el-form-item label="请求体设置" :class="{ 'show-screen__full': reqBodyFullStatus }">
                    <el-tooltip placement="top">
                        <template #content>
                            <pre style="max-height: 300px; overflow: auto">
post模版:
{
  "req":{
    "custom_a":"${a.date}",
    "custom_b":"${b.datetime}",
    "custom_c":"${c.timestamp}",
    "custom_d":"${d.string}",
    "custom_e":"${e.boolean}",
    "custom_f":"${f.double}",
    "custom_g":"${g.int}"
  },
  "custom_page":"${page.int}",
  "custom_pageSize":"${pageSize.int}"
}

get模版:
custom_a=${a.date}&custom_b=${b.datetime}&custom_c=${c.timestamp}&custom_d=${d.string}&custom_e=
${e.boolean}&custom_f=${f.double}&custom_g=${g.int}&custom_page=${page.int}&custom_pageSize=${pageSize.int}
</pre
                            >
                        </template>
                        <el-icon style="left: 68px" class="tooltip-msg"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    <span class="format-json" @click="formatterJsonEvent(formData, 'reqBody')">格式化JSON</span>
                    <el-icon class="modal-full-screen" @click="fullScreenEvent('reqBodyFullStatus')">
                        <FullScreen v-if="!reqBodyFullStatus" />
                        <Close v-else />
                    </el-icon>
                    <code-mirror v-model="formData.reqBody" basic :lang="jsonLang" />
                </el-form-item>
                <el-form-item label="SQL设置" :class="{ 'show-screen__full': sqlFullStatus }">
                    <el-tooltip placement="top">
                        <template #content>
                            <pre>
不支持*表达语法，'${a}'对应请求中的"$a.date"
插入模版：
insert into demo (col1,col2,col3,col4,col5,col6,col7) values ('${a}','${b}','${c}','${d}','${e}','${f}','${g}')

查询模版：
select col1,col2,col3,col4,col5,col6,col7 from demo where col1 = '${a}' and col2= '${b}'
and col3= '${c}' and col4= '${d}' and col5= '${e}' and col6= '${f}' and col7= '${g}'</pre
                            >
                        </template>
                        <el-icon style="left: 48px" class="tooltip-msg"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    <el-icon class="modal-full-screen" @click="fullScreenEvent('sqlFullStatus')">
                        <FullScreen v-if="!sqlFullStatus" />
                        <Close v-else />
                    </el-icon>
                    <code-mirror v-model="formData.apiSql" basic :lang="sqlLang" />
                </el-form-item>
                <el-form-item label="返回体设置" prop="resBody" :class="{ 'show-screen__full': respBodyFullStatus }">
                    <el-tooltip placement="top">
                        <template #content>
                            <pre style="max-height: 300px; overflow: auto">
系统参数$count.long，需要开启分页激活

单对象模版：
{
    "a": "${col1.date}",
    "b": "${col2.datetime}",
    "c": "${col3.timestamp}",
    "d": "${col4.string}",
    "e": "${col5.boolean}",
    "f": "${col6.double}",
    "g": "${col7.int}"
}

数组模版：
[
    {
        "a": "${col1.date}",
        "b": "${col2.datetime}",
        "c": "${col3.timestamp}",
        "d": "${col4.string}",
        "e": "${col5.boolean}",
        "f": "${col6.double}",
        "g": "${col7.int}"
  }
]

分页模版：
{
  "$data": [
    {
      "a": "${col1.date}",
      "b": "${col2.datetime}",
      "c": "${col3.timestamp}",
      "d": "${col4.string}",
      "e": "${col5.boolean}",
      "f": "${col6.double}",
      "g": "${col7.int}"
    }
  ],
  "count": "${count.long}"
}</pre
                            >
                        </template>
                        <el-icon style="left: 68px" class="tooltip-msg"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    <span class="format-json" @click="formatterJsonEvent(formData, 'resBody')">格式化JSON</span>
                    <el-icon class="modal-full-screen" @click="fullScreenEvent('respBodyFullStatus')">
                        <FullScreen v-if="!respBodyFullStatus" />
                        <Close v-else />
                    </el-icon>
                    <code-mirror v-model="formData.resBody" basic :lang="jsonLang" />
                </el-form-item>
            </div>
        </el-form>
        <!-- 第三步-接口检测---------------- ++ -->
        <el-form
            v-if="stepIndex === 2"
            ref="formTest"
            class="custom-api-form"
            label-position="top"
            :model="formDataTest"
        >
            <el-form-item label="请求路径">
                <el-input v-model="formDataTest.path" maxlength="1000" placeholder="请输入" :disabled="true" />
                <span
                    class="copy-url"
                    id="api-path"
                    :data-clipboard-text="formDataTest.path"
                    @click="copyUrlEvent('api-path-test')"
                >
                    复制
                </span>
            </el-form-item>
            <el-form-item label="请求头" prop="headerConfig" :class="{ 'show-screen__full': reqHeaderFullStatus }">
                <span class="add-btn">
                    <el-icon @click="addNewOption(formDataTest.headerConfig)"><CirclePlus /></el-icon>
                </span>
                <div class="form-options__list">
                    <div class="form-options__item" v-for="(element, index) in formDataTest.headerConfig" :key="index">
                        <div class="input-item">
                            <span class="item-label">键</span>
                            <el-input v-model="element.label" placeholder="请输入"></el-input>
                        </div>
                        <div class="input-item">
                            <span class="item-label">值</span>
                            <el-input v-model="element.value" placeholder="请输入"></el-input>
                        </div>
                        <div class="option-btn">
                            <el-icon
                                v-if="formDataTest.headerConfig && formDataTest.headerConfig.length > 1"
                                class="remove"
                                @click="removeItem(index, formDataTest.headerConfig)"
                            >
                                <CircleClose />
                            </el-icon>
                        </div>
                    </div>
                </div>
            </el-form-item>
            <el-form-item
                v-if="formDataTest.method === 'POST'"
                label="请求体"
                prop="bodyParams"
                :class="{ 'show-screen__full': reqBodyFullStatus }"
            >
                <span class="format-json" @click="formatterJsonEvent(formDataTest, 'bodyParams')">格式化JSON</span>
                <el-icon class="modal-full-screen" @click="fullScreenEvent('reqBodyFullStatus')">
                    <FullScreen v-if="!reqBodyFullStatus" />
                    <Close v-else />
                </el-icon>
                <code-mirror v-model="formDataTest.bodyParams" basic :lang="jsonLang" />
            </el-form-item>
            <el-form-item v-if="formDataTest.method === 'GET'" label="请求体" prop="bodyConfig">
                <span class="add-btn">
                    <el-icon @click="addNewOption(formDataTest.bodyConfig)"><CirclePlus /></el-icon>
                </span>
                <div class="form-options__list">
                    <div class="form-options__item" v-for="(element, index) in formDataTest.bodyConfig" :key="index">
                        <div class="input-item">
                            <span class="item-label">键</span>
                            <el-input v-model="element.label" placeholder="请输入"></el-input>
                        </div>
                        <div class="input-item">
                            <span class="item-label">值</span>
                            <el-input v-model="element.value" placeholder="请输入"></el-input>
                        </div>
                        <div class="option-btn">
                            <el-icon
                                v-if="formDataTest.bodyConfig && formDataTest.bodyConfig.length > 1"
                                class="remove"
                                @click="removeItem(index, formDataTest.bodyConfig)"
                            >
                                <CircleClose />
                            </el-icon>
                        </div>
                    </div>
                </div>
            </el-form-item>
            <!-- 响应体 状态码 -->
            <el-form-item
                class="resp-http-body"
                label="响应体"
                prop="returnConfig"
                :class="{ 'show-screen__full': respBodyFullStatus }"
            >
                <span class="format-json" @click="formatterJsonEvent(formDataTest, 'bodyParams')">格式化JSON</span>
                <span class="resp-http-status" :style="{ color: httpStatus == 500 ? 'red' : '' }">
                    {{ httpStatus }}
                </span>
                <el-icon class="modal-full-screen" @click="fullScreenEvent('respBodyFullStatus')">
                    <FullScreen v-if="!respBodyFullStatus" />
                    <Close v-else />
                </el-icon>
                <code-mirror
                    ref="responseBodyRef"
                    v-model="formDataTest.returnConfig"
                    :disabled="true"
                    basic
                    :lang="jsonLang"
                />
            </el-form-item>
        </el-form>
        <!-- 第三步-接口检测---------------- ++ -->
        <template #customLeft>
            <template v-if="stepIndex === 0">
                <el-button @click="closeEvent">取消</el-button>
                <el-button type="primary" @click="nextStepEvent">下一步</el-button>
            </template>
            <template v-if="stepIndex === 1">
                <el-button @click="closeEvent">取消</el-button>
                <el-button type="primary" @click="stepIndex = 0">上一步</el-button>
                <el-button :loading="okLoading" type="primary" @click="nextStepEvent">下一步</el-button>
            </template>
            <template v-if="stepIndex === 2">
                <el-button @click="closeEvent">取消</el-button>
                <el-button type="primary" @click="stepIndex = 1">上一步</el-button>
                <el-button :loading="testLoading" @click="testApiEvent" type="primary">检测</el-button>
                <el-button :loading="okLoading" type="primary" @click="okToCloseEvent">确定</el-button>
            </template>
        </template>
    </BlockModal>
</template>

<script lang="ts" setup>
import { reactive, defineExpose, ref, nextTick } from 'vue'
import BlockModal from '@/components/block-modal/index.vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { GetDatasourceList } from '@/services/datasource.service'
import { GetComputerGroupList } from '@/services/computer-group.service'
// import CodeMirror from 'vue-codemirror6'
import { json } from '@codemirror/lang-json'
import { jsonFormatter } from '@/utils/formatter'
import { sql } from '@codemirror/lang-sql'
import { GetCustomApiDetailData, TestCustomApiData } from '@/services/custom-api.service'
import Clipboard from 'clipboard'

interface Option {
    label: string
    value: string
}

import { useAuthStore } from '@/store/useAuth'

const authStore = useAuthStore()

const checkPath = (rule: any, value: any, callback: any) => {
    const phoneReg = /^\/([a-zA-Z0-9_]+(\/[a-zA-Z0-9_]+)*)$/
    if (value && !phoneReg.test(value)) {
        callback(new Error('路径输入有误'))
    } else {
        callback()
    }
}

const optionsRule = (rule: any, value: any, callback: any) => {
    const valueList = (value || []).map((v) => v.value).filter((v) => !!v)
    if (value && value.some((item: Option) => !item.label || !item.value)) {
        callback(new Error('请将键/值输入完整'))
    } else if (
        value &&
        valueList &&
        valueList.length.length &&
        valueList.length !== Array.from(new Set(valueList)).length
    ) {
        callback(new Error('值重复，请重新输入'))
    } else {
        callback()
    }
}

const form = ref<FormInstance>()
const callback = ref<any>()
const initCb = ref<any>()
const clusterList = ref<any[]>([]) // 计算集群
const dataSourceList = ref<any[]>([]) // 数据源

const jsonLang = ref<any>(json())
const sqlLang = ref<any>(sql())

const reqHeaderFullStatus = ref<boolean>(false)
const reqBodyFullStatus = ref<boolean>(false)
const sqlFullStatus = ref<boolean>(false)
const respBodyFullStatus = ref<boolean>(false)
const isEdit = ref<boolean>(false)
const stepIndex = ref<number>(0)

const okLoading = ref<boolean>(false)
const testLoading = ref<boolean>(false)
const httpStatus = ref<number | null>(null)

const modelConfig = reactive({
    title: '添加接口',
    visible: false,
    width: '564px',
    cancelConfig: {
        title: '取消',
        cancel: closeEvent,
        disabled: false,
        hide: true
    },
    needScale: false,
    zIndex: 1100,
    closeOnClickModal: false
})
const formData = reactive<
    | {
          id: string
          name: string
          apiType: string
          path: string
          datasourceId: string
          remark: string
          tokenType: string
          reqHeader: Option[]
          reqBody: string | null
          apiSql: string | null
          pageType: boolean
          resBody: string | null
      }
    | any
>({
    id: '',
    // 基础配置
    name: '', // 名称
    apiType: '', // 请求方式
    path: '', // 自定义访问路径
    // clusterId: '',        // 计算集群
    datasourceId: '', // 数据源
    remark: '', // 备注
    // 请求配置
    tokenType: 'ANONYMOUS', // 请求头模式
    reqHeader: [], // 请求头设置
    reqBody: null, // 请求体设置
    apiSql: null, // SQL设置
    pageType: false, // 是否分页
    resBody: null // 返回体设置（成功/失败）
})
const rules = reactive<FormRules>({
    name: [{ required: true, message: '请输入名称', trigger: ['blur', 'change'] }],
    apiType: [{ required: true, message: '请选择请求方式', trigger: ['blur', 'change'] }],
    path: [
        { required: true, message: '请输入自定义访问路径', trigger: ['blur', 'change'] },
        { validator: checkPath, trigger: ['blur', 'change'] }
    ],
    // clusterId: [{ required: true, message: '请选择计算集群', trigger: [ 'blur', 'change' ]}],
    datasourceId: [{ required: true, message: '请选择数据源', trigger: ['blur', 'change'] }],
    reqHeader: [{ validator: optionsRule, trigger: ['blur', 'change'] }],
    reqBody: [{ required: true, message: '请输入请求体设置', trigger: ['blur', 'change'] }],
    apiSql: [{ required: true, message: '请输入SQL设置', trigger: ['blur', 'change'] }],
    resBody: [{ required: true, message: '请输入返回体设置（成功/失败）', trigger: ['blur', 'change'] }]
})

// 接口检测参数
const formDataTest = reactive<{
    id: string
    path: string
    method: string
    headerConfig: Option[]
    bodyConfig: Option[]
    bodyParams: any
    returnConfig: any
}>({
    id: '',
    path: '', // 自定义访问路径
    method: '',
    headerConfig: [], // 请求头
    bodyConfig: [], // 请求体
    bodyParams: null,
    returnConfig: null // 返回体
})

function showModal(cb: () => void, data: any, cb2?: () => void): void {
    callback.value = cb
    initCb.value = cb2
    stepIndex.value = 0
    modelConfig.visible = true
    if (data) {
        getApiDetailData(data.id)
        isEdit.value = true
        modelConfig.title = '编辑接口'
    } else {
        Object.keys(formData).forEach((key) => {
            formData[key] = null
        })
        formData.tokenType = 'ANONYMOUS'
        formData.pageType = false
        isEdit.value = false
        modelConfig.title = '添加接口'
    }
    nextTick(() => {
        form.value?.resetFields()
    })
}

// 确定按钮去刷新列表数据
function okToCloseEvent() {
    ElMessage.success('保存成功')
    initCb.value()
    closeEvent()
}

function formatterJsonEvent(formData: any, key: string) {
    try {
        formData[key] = jsonFormatter(formData[key])
    } catch (error) {
        console.error('请检查输入的JSON格式是否正确', error)
        ElMessage.error('请检查输入的JSON格式是否正确')
    }
}

function getApiDetailData(id: string, type?: string) {
    GetCustomApiDetailData({
        id: id
    }).then((res: any) => {
        if (type === 'test') {
            if (res.data.reqHeader) {
                formDataTest.headerConfig = res.data.reqHeader.map((item: any) => {
                    item.value = ''
                    return {
                        ...item
                    }
                })
            }
            if (res.data.apiType === 'POST') {
                formDataTest.bodyParams = jsonFormatter(JSON.parse(res.data.reqJsonTemp))
            } else {
                formDataTest.bodyConfig = res.data.reqGetTemp
            }
        } else {
            Object.keys(formData).forEach((key) => {
                if (key === 'reqHeader') {
                    formData[key] = res.data['reqHeader']
                } else {
                    formData[key] = res.data[key]
                }
            })
        }
    }).catch(() => {})
}

// 查询计算集群
function getClusterList(e: boolean) {
    if (e) {
        GetComputerGroupList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: ''
        }).then((res: any) => {
            clusterList.value = res.data.content.map((item: any) => {
                return {
                    label: item.name,
                    value: item.id
                }
            })
        }).catch(() => {
            clusterList.value = []
        })
    }
}

// 查询数据源
function getDataSourceList(e: boolean, searchType?: string) {
    if (e) {
        GetDatasourceList({
            page: 0,
            pageSize: 10000,
            searchKeyWord: searchType || ''
        }).then((res: any) => {
            dataSourceList.value = res.data.content.map((item: any) => {
                return {
                    label: item.name,
                    value: item.id
                }
            })
        }).catch(() => {
            dataSourceList.value = []
        })
    }
}

function nextStepEvent() {
    form.value?.validate((valid) => {
        if (valid) {
            if (stepIndex.value === 1) {
                // 第二步中点击下一步时，暂存入库一下数据
                okLoading.value = true
                callback.value({
                    ...formData,
                    id: formData.id ? formData.id : undefined
                }).then((res: any) => {
                    okLoading.value = false
                    httpStatus.value = null
                    // 保存成功，进入下一步
                    stepIndex.value += 1

                    formData.id = res?.data.id

                    formDataTest.id = formData.id
                    getApiDetailData(formDataTest.id, 'test')
                    if (formDataTest.path.slice(0, 1) !== '/') {
                        formDataTest.path = '/' + formData.path
                    }
                    formDataTest.path = `${location.origin}/${authStore.tenantId}/api${formData.path}`
                    formDataTest.method = formData.apiType
                    formDataTest.headerConfig = [{ label: '', value: '' }]
                    formDataTest.bodyConfig = [{ label: '', value: '' }]
                    formDataTest.bodyParams = null
                    formDataTest.returnConfig = null
                }).catch(() => {
                    okLoading.value = false
                })
            } else {
                stepIndex.value += 1
            }
        } else {
            ElMessage.warning('请将表单输入完整')
        }
    })
}

function testApiEvent() {
    const headerParams: any = {}
    formDataTest.headerConfig.forEach((item) => {
        headerParams[item.label] = item.value
    })
    try {
        let bodyParams: any = {}
        if (formDataTest.method === 'GET') {
            formDataTest.bodyConfig.forEach((item) => {
                bodyParams[item.label] = item.value
            })
        } else {
            bodyParams = JSON.parse(formDataTest.bodyParams)
        }
        testLoading.value = true
        TestCustomApiData({
            id: formDataTest.id,
            headerParams: headerParams,
            requestBody: bodyParams
        }).then((res: any) => {
            testLoading.value = false
            ElMessage.success(res.msg)
            httpStatus.value = res.data.httpStatus
            if (res.data.httpStatus === 200) {
                formDataTest.returnConfig = jsonFormatter(JSON.stringify(res.data.body))
            } else {
                formDataTest.returnConfig = res.data.msg
            }
        }).catch(() => {
            testLoading.value = false
        })
    } catch (error) {
        console.warn('请求体输入有误', error)
        ElMessage.warning('请求体格式输入有误')
    }
}

function tokenTypeChangeEvent(e: string) {
    if (e === 'CUSTOM') {
        formData.reqHeader = [
            {
                label: '',
                value: ''
            }
        ]
    }
}
function addNewOption(data?: any) {
    if (!!data) {
        // 接口检测部分
        data.push({
            label: '',
            value: ''
        })
    } else {
        formData.reqHeader.push({
            label: '',
            value: ''
        })
    }
}
function removeItem(index: number, data?: any) {
    if (!!data) {
        // 接口检测部分
        data.splice(index, 1)
    } else {
        formData.reqHeader.splice(index, 1)
    }
}

function okEvent() {
    form.value?.validate((valid) => {
        if (valid) {
            okLoading.value = true
            callback
                .value({
                    ...formData,
                    id: formData.id ? formData.id : undefined
                })
                .then((res: any) => {
                    okLoading.value = false
                    if (res === undefined) {
                        modelConfig.visible = false
                    } else {
                        modelConfig.visible = true
                    }
                })
                .catch(() => {
                    okLoading.value = false
                })
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
    } else if (type === 'respBodyFullStatus') {
        respBodyFullStatus.value = !respBodyFullStatus.value
    } else if (type === 'sqlFullStatus') {
        sqlFullStatus.value = !sqlFullStatus.value
    }
}

// 接口检测部分
function copyUrlEvent(id: string) {
    let clipboard = new Clipboard('#' + id)
    clipboard.on('success', () => {
        ElMessage.success('复制成功')
        clipboard.destroy()
    })
}

defineExpose({
    showModal
})
</script>

<style lang="scss">
.custom-api-form__step {
    margin: auto;
    padding-top: 20px;
    position: sticky;
    top: 0;
    background: #ffffff;
    z-index: 10;
    padding-left: 25%;
    padding-right: 25%;
    box-sizing: border-box;
    border-bottom: 1px solid getCssVar('border-color');
    .el-step__head {
        &.is-process {
            color: getCssVar('color', 'primary');
            .el-step__line {
                // background-color: getCssVar('color', 'primary');
            }
            .el-step__icon {
                border-color: getCssVar('color', 'primary');
            }
        }
    }
    .el-step__main {
        margin-left: -12px;
        .el-step__title {
            font-size: 12px;
            &.is-process {
                color: getCssVar('color', 'primary');
            }
        }
    }
}
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
        .format-json {
            position: absolute;
            top: -34px;
            right: 20px;
            font-size: 12px;
            color: getCssVar('color', 'primary');
            cursor: pointer;
            &:hover {
                text-decoration: underline;
            }
        }
        .tooltip-msg {
            position: absolute;
            top: -28px;
            // left: 20px;
            color: getCssVar('color', 'info');
            font-size: 16px;
        }
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
                .modal-full-screen {
                    position: absolute;
                    top: -26px;
                    right: 0;
                    cursor: pointer;
                    &:hover {
                        color: getCssVar('color', 'primary');
                    }
                }
                .vue-codemirror {
                    height: calc(100% - 36px);
                }
            }
        }
        .el-form-item__content {
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
                    font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif,
                        'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol';
                }

                .cm-content {
                    font-size: 12px;
                    font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif,
                        'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol';
                }

                .cm-tooltip-autocomplete {
                    ul {
                        li {
                            height: 40px;
                            display: flex;
                            align-items: center;
                            font-size: 12px;
                            background-color: #ffffff;
                            font-family: v-sans, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif,
                                'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol';
                        }

                        li[aria-selected] {
                            background: #409eff;
                        }

                        .cm-completionIcon {
                            margin-right: -4px;
                            opacity: 0;
                        }
                    }
                }
            }
            .add-btn {
                position: absolute;
                right: 0;
                top: -32px;
                .el-icon {
                    color: getCssVar('color', 'primary');
                    cursor: pointer;
                    font-size: 16px;
                }
            }
            .form-options__list {
                width: 100%;
                max-height: 210px;
                overflow: auto;
                .form-options__item {
                    display: flex;
                    margin-bottom: 8px;
                    .input-item {
                        display: flex;
                        font-size: 12px;
                        margin-right: 8px;
                        color: #303133;
                        width: 100%;
                        .item-label {
                            margin-right: 8px;
                        }
                        .el-input {
                            .el-input__wrapper {
                                // padding: 0;
                            }
                        }
                    }
                    .option-btn {
                        display: flex;
                        height: 32px;
                        align-items: center;
                        .remove {
                            color: red;
                            cursor: pointer;
                            margin-right: 8px;
                            &:hover {
                                color: getCssVar('color', 'primary');
                            }
                        }
                        .move {
                            color: getCssVar('color', 'primary');
                            &:active {
                                cursor: move;
                            }
                        }
                    }
                }
            }
        }
    }
}
</style>
