import React, { useEffect, useState } from 'react'
import { Button, Drawer, Form, message, Select, Table, Tabs, type TabsProps, theme } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import TextArea from 'antd/es/input/TextArea'
import axios from 'axios'
import './WorkPage.less'
import { type WorkInfo } from '../../types/woks/info/WorkInfo'
import { type RunWorkRes } from '../../types/woks/res/RunWorkRes'
import {
  configWorkApi,
  getWorkApi,
  getWorkDataApi,
  getWorkLogApi,
  getWorkStatusApi,
  runWorkApi,
  stopWorkApi
} from '../../services/works/WorksService'
import {
  CloseOutlined,
  LeftOutlined,
  PlayCircleOutlined,
  RollbackOutlined,
  SaveOutlined,
  SettingOutlined
} from '@ant-design/icons'
import { ConfigWorkReq } from '../../types/woks/req/ConfigWorkReq'

const { Option } = Select

function WorkPage() {
  const navigate = useNavigate()
  const { workId } = useParams()
  const [form] = Form.useForm()

  const [work, setWork] = useState<WorkInfo>()
  const [result, setResult] = useState<RunWorkRes>()

  useEffect(() => {
    getWork()
  }, [])

  const getWork = () => {
    getWorkApi(workId as string).then(function (response) {
      setWork(response)
    })
  }

  const getWorkLog = () => {
    getWorkLogApi(workId as string, result?.applicationId).then(function (response) {
      setResult(response)
    })
  }

  const getData = () => {
    getWorkDataApi(workId as string, result?.applicationId).then(function (response) {
      setResult(response)
    })
  }

  const getWorkStatus = () => {
    getWorkStatusApi(workId as string, result?.applicationId).then(function (response) {
      setResult(response)
    })
  }

  const getStopWork = () => {
    stopWorkApi(workId as string, result?.applicationId).then(function (response) {})
  }

  const configWorkReq: ConfigWorkReq = {
    workId: workId,
    engineId: '',
    datasourceId: ''
  }

  const configWork = () => {
    configWorkApi(configWorkReq).then((r) => {})
  }

  const runWork = () => {
    runWorkApi(workId as string).then((r) => {})
  }

  const onChange = (key: string) => {
    switch (key) {
      case '2':
        if (work?.workType === 'sparkSql') {
          // 查询日志
          getWorkLog()
        }
        break
      case '3':
        if (work?.workType === 'sparkSql') {
          getData()
        }
        break
      case '4':
      case '5':
        if (work?.workType === 'sparkSql') {
          // 查询数据
          getWorkStatus()
        }
        break
      default:
    }
  }

  const columns =
    result?.data != null && result?.data.length > 0
      ? result.data[0].map((columnTitle) => ({
          title: columnTitle,
          dataIndex: columnTitle
        }))
      : []

  const data =
    result?.data != null && result?.data.length > 0
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
      children: result?.message
    },
    {
      key: '2',
      label: '运行日志',
      children: result?.log
    },
    {
      key: '3',
      label: '数据返回',
      children: <Table columns={columns} dataSource={data} scroll={{ y: 160 }} />
    },
    {
      key: '4',
      label: '监控信息',
      children: result?.trackingUrl
    }
  ]

  return (
    <>
      <div>
        <div className={'work-bar'}>
          <Button
            onClick={() => {
              navigate('/works/' + work?.workflowId)
            }}
            className={'sy-btn'}
            type={'text'}
            icon={<RollbackOutlined />}>
            返回
          </Button>
          <Button className={'sy-btn'} type={'text'} icon={<PlayCircleOutlined />}>
            运行
          </Button>
          <Button className={'sy-btn'} type={'text'} icon={<CloseOutlined />}>
            中止
          </Button>
          <Button
            onClick={() => {
              configWork()
            }}
            className={'sy-btn'}
            type={'text'}
            icon={<SaveOutlined />}>
            保存
          </Button>
          <Button className={'sy-btn'} type={'text'} icon={<SettingOutlined />}>
            配置
          </Button>
        </div>
        <TextArea
          rows={12}
          value={work?.sql}
          onChange={(e) => {
            configWorkReq.sql = e.target.value
          }}
        />
        <Tabs defaultActiveKey="1" items={items} onChange={onChange} />
      </div>
    </>
  )
}

export default WorkPage
