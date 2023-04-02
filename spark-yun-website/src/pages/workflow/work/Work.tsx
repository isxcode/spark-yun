import React, { useEffect, useState } from 'react'
import './Work.scss'
import {
  Avatar,
  Button,
  Divider,
  Drawer,
  Form,
  Input,
  List,
  message,
  Select,
  Skeleton,
  Space,
  Table,
  Tabs,
  type TabsProps,
  Tag,
  theme
} from 'antd'
import { ColumnsType } from 'antd/es/table'
import { useNavigate, useParams } from 'react-router-dom'
import TextArea from 'antd/es/input/TextArea'
import axios from 'axios'

interface WorkType {
  script: string
  name: string
  workflowId: string
  datasourceId: string
  type: string
}

interface Datasource {
  id: string
  name: string
}

const { Option } = Select
function Work () {
  const navigate = useNavigate()

  const { workId } = useParams()

  const [work, setWork] = useState<WorkType>({ type: '', datasourceId: '', name: '', script: '', workflowId: '' })
  const [workScript, setWorkScript] = useState('')
  const [result, setResult] = useState({ message: '', log: '', data: [[]] })
  const handleInputChange = (value) => {
    setWorkScript(value)
  }

  useEffect(() => {
    getWork()
  }, [])

  const getWork = () => {
    axios({
      method: 'get',
      url: process.env.API_PREFIX_URL + '/workflow/getWork?workId=' + workId
    })
      .then(function (response) {
        setWork(response.data)
        setWorkScript(response.data.script)
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const configWorkDatasource = (datasourceId) => {
    axios({
      method: 'post',
      url: process.env.API_PREFIX_URL + '/workflow/configWork',
      data: {
        workId,
        script: workScript,
        datasourceId
      }
    })
      .then(function (response) {
        message.success('保存成功')
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const configWork = () => {
    axios({
      method: 'post',
      url: process.env.API_PREFIX_URL + '/workflow/configWork',
      data: {
        workId,
        script: workScript
      }
    })
      .then(function (response) {
        message.success('保存成功')
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const runWork = () => {
    axios({
      method: 'get',
      url: process.env.API_PREFIX_URL + '/workflow/runWork?workId=' + workId
    })
      .then(function (response) {
        message.success('保存成功')
        setResult(response.data)
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const onChange = (key: string) => {}

  const {
    token: { colorBgContainer, colorPrimary }
  } = theme.useToken()

  const columns =
    result.data != null && result.data.length > 0
      ? result.data[0].map((columnTitle) => ({
        title: columnTitle,
        dataIndex: columnTitle
      }))
      : []

  const data =
    result.data != null && result.data.length > 0
      ? result.data.slice(1).map((row) => {
        const rowData = {}
        result.data[0].forEach((columnTitle, columnIndex) => {
          rowData[columnTitle] = row[columnIndex]
        })
        return rowData
      })
      : []

  const items: TabsProps['items'] = [
    {
      key: '1',
      label: '提交日志',
      children: result.message
    },
    {
      key: '2',
      label: '运行日志',
      children: result.log
    },
    {
      key: '3',
      label: '返回数据',
      children: <Table columns={columns} dataSource={data} scroll={{ y: 160 }} />
    },
    {
      key: '4',
      label: '监控地址',
      children: 'http://localhost:8888'
    },
    {
      key: '5',
      label: '运行状态',
      children: '运行中'
    }
  ]

  const [open, setOpen] = useState(false)
  const [datasource, setDatasource] = useState<Datasource[]>([])

  const queryDatasources = () => {
    axios({
      method: 'get',
      url: process.env.API_PREFIX_URL + '/datasource/queryDatasource'
    })
      .then(function (response) {
        setDatasource(response.data)
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const showDrawer = () => {
    // 查询数据源
    queryDatasources()
    setOpen(true)
  }

  const onClose = () => {
    setOpen(false)
  }

  const onFinish = (values: any) => {
    console.log('configWorkDatasource' + values)
    configWorkDatasource(values.datasourceId)
  }

  const onFinishFailed = (errorInfo: any) => {
    console.log('Failed:', errorInfo)
  }

  const [form] = Form.useForm()

  const onGenderChange = (value: string) => {
    switch (value) {
      case 'male':
        form.setFieldsValue({ note: 'Hi, man!' })
        break
      case 'female':
        form.setFieldsValue({ note: 'Hi, lady!' })
        break
      case 'other':
        form.setFieldsValue({ note: 'Hi there!' })
        break
      default:
    }
  }

  return (
    <>
      <div>
        <div className={'work-bar'}>
          <Button type={'text'}>{work.name}</Button>
          <Button type={'text'}>{work.type}</Button>
          <Button
            onClick={() => {
              navigate('/worklist/' + work.workflowId)
            }}>
            返回
          </Button>
          <Button
            onClick={() => {
              runWork()
            }}>
            运行
          </Button>
          <Button>中止</Button>
          <Button>撤回</Button>
          <Button
            onClick={() => {
              configWork()
            }}>
            保存
          </Button>
          <Button onClick={showDrawer}>配置</Button>
        </div>
        <TextArea
          rows={12}
          value={workScript}
          onChange={(e) => {
            handleInputChange(e.target.value)
          }}
        />
        <Tabs defaultActiveKey="1" items={items} onChange={onChange} />
      </div>
      <Drawer title="作业配置" placement="right" width={550} onClose={onClose} open={open}>
        <Form
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 18 }}
          style={{ width: 400 }}
          initialValues={{ remember: true }}
          onFinish={onFinish}
          onFinishFailed={onFinishFailed}
          autoComplete="off"
          form={form}>
          <Form.Item name="datasourceId" label="数据源" rules={[{ required: true }]}>
            <Select defaultValue={work.datasourceId} placeholder="" onChange={onGenderChange} allowClear>
              {datasource.map((option) => (
                <Option key={option.id} value={option.id}>
                  {option.name}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item wrapperCol={{ offset: 8, span: 16 }}>
            <Button type="primary" htmlType="submit">
              保存
            </Button>
          </Form.Item>
        </Form>
      </Drawer>
    </>
  )
}

export default Work
