import React, { useEffect, useState } from 'react'
import { Button, Col, Form, Row, Table, Tabs, type TabsProps } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import TextArea from 'antd/es/input/TextArea'
import './WorkPage.less'
import { type WorkInfo } from '../../types/woks/info/WorkInfo'
import { type RunWorkRes } from '../../types/woks/res/RunWorkRes'
import {
  configWorkApi,
  getSubmitLogApi,
  getWorkApi,
  getWorkDataApi,
  getWorkLogApi,
  getWorkStatusApi,
  runWorkApi,
  stopWorkApi
} from '../../services/works/WorksService'
import { CloseOutlined, PlayCircleOutlined, RollbackOutlined, SaveOutlined, SettingOutlined } from '@ant-design/icons'
import { type ConfigWorkReq } from '../../types/woks/req/ConfigWorkReq'
import { WorkConfigDrawer } from '../../modals/work/config/WorkConfigDrawer'

function WorkPage() {
  const navigate = useNavigate()
  const { workId } = useParams()

  const [isModalVisible, setIsModalVisible] = useState(false)
  const [work, setWork] = useState<WorkInfo>()
  const [result, setResult] = useState<RunWorkRes>()
  const [instanceId, setInstanceId] = useState('')
  const [logs, setLogs] = useState('')
  const [pollingStarted, setPollingStarted] = useState(false)

  const startPolling = () => {
    setPollingStarted(true)
  }

  useEffect(() => {
    getWork()
  }, [])

  useEffect(() => {
    const fetchData = async () => {
      getSubmitLogApi(instanceId).then(function (response) {
        const successLog = response.status === 'SUCCESS' || response.status === 'FAIL'
        setLogs(response.log as string)
        if (successLog) {
          setPollingStarted(false)
        }
      })
    }

    if (pollingStarted) {
      const pollingInterval = setInterval(fetchData, 2000)
      return () => {
        clearInterval(pollingInterval)
      }
    }
  }, [pollingStarted])

  const getWork = () => {
    getWorkApi(workId as string).then(function (response) {
      setWork(response)
    })
  }

  const getWorkLog = () => {
    getWorkLogApi(instanceId).then(function (response) {
      setResult({ ...result, yarnLog: response.yarnLog, data: [[]] })
    })
  }

  const getData = () => {
    getWorkDataApi(instanceId).then(function (response) {
      setResult({ ...result, data: response.data })
    })
  }

  const getWorkStatus = () => {
    getWorkStatusApi(instanceId).then(function (response) {
      setResult({
        ...result,
        trackingUrl: response.trackingUrl,
        finalApplicationStatus: response.finalApplicationStatus,
        yarnApplicationState: response.yarnApplicationState,
        data: [[]]
      })
    })
  }

  const getStopWork = () => {
    stopWorkApi(workId as string, result?.applicationId).then(function (response) {})
  }

  const configWorkReq: ConfigWorkReq = {
    workId,
    sqlScript: work?.sqlScript as string,
    calculateEngineId: work?.calculateId as string,
    datasourceId: work?.datasourceId as string
  }

  const configWork = () => {
    configWorkApi(configWorkReq).then((r) => {})
  }

  const runWork = () => {
    runWorkApi(workId as string).then((r) => {
      setInstanceId(r.instanceId as string)
      startPolling()
    })
  }

  const onChange = (key: string) => {
    switch (key) {
      case 'EXECUTE_LOG':
        if (work?.workType === 'SPARK_SQL') {
          getWorkLog()
        }
        break
      case 'BACK_DATA':
        getData()
        break
      case 'MONITOR_INFO':
        if (work?.workType === 'SPARK_SQL') {
          getWorkStatus()
        }
        break
    }
  }

  const columns = () => {
    return result?.data != null && result?.data.length > 0
      ? result.data[0].map((columnTitle) => ({
          title: columnTitle,
          dataIndex: columnTitle
        }))
      : []
  }

  const data = () => {
    return result?.data != null && result?.data.length > 0
      ? result.data.slice(1).map((row) => {
          const rowData = {}
          result.data[0].forEach((columnTitle, columnIndex) => {
            rowData[columnTitle] = row[columnIndex]
          })
          return rowData
        })
      : []
  }

  const items: TabsProps['items'] = []
  const initItems = () => {
    items.push({
      key: 'SUBMIT_LOG',
      label: '提交日志',
      children: <pre style={{ overflowY: 'scroll', maxHeight: '200px', whiteSpace: 'pre-wrap' }}>{logs}</pre>
    })
    if (work?.workType === 'QUERY_JDBC' || work?.workType === 'SPARK_SQL') {
      items.push({
        key: 'BACK_DATA',
        label: '数据返回',
        children: <Table columns={columns()} dataSource={data()} scroll={{ y: 200 }} />
      })
    }
    if (work?.workType === 'SPARK_SQL') {
      items.push({
        key: 'EXECUTE_LOG',
        label: '运行日志',
        children: (
          <pre style={{ overflowY: 'scroll', maxHeight: '200px', whiteSpace: 'pre-wrap' }}>{result?.yarnLog}</pre>
        )
      })
      items.push({
        key: 'MONITOR_INFO',
        label: '监控信息',
        children: (
          <div>
            <pre>作业运行地址: {result?.trackingUrl}</pre>
            <pre>作业当前状态: {result?.finalApplicationStatus}</pre>
            <pre>Yarn容器状态: {result?.yarnApplicationState}</pre>
          </div>
        )
      })
    }

    return items
  }

  const containerStyle: React.CSSProperties = {
    padding: 24,
    position: 'relative',
    height: '100%',
    overflow: 'hidden'
  }

  return (
    <>
      <div style={containerStyle}>
        <Row>
          <Col span={24}>
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
              <Button
                className={'sy-btn'}
                type={'text'}
                icon={<PlayCircleOutlined />}
                onClick={() => {
                  runWork()
                }}>
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
              <Button
                className={'sy-btn'}
                type={'text'}
                icon={<SettingOutlined />}
                onClick={() => {
                  setIsModalVisible(true)
                }}>
                配置
              </Button>
              {/* <div className={'work-name'}>{work?.name}</div> */}
            </div>
          </Col>
        </Row>
        <Row style={{ height: '50%' }}>
          <Col span={24}>
            <TextArea
              style={{ height: '100%' }}
              className={'work-sql-textarea'}
              value={work?.sqlScript}
              onChange={(e) => {
                setWork({ ...work, sqlScript: e.target.value })
              }}
            />
          </Col>
        </Row>
        <Row style={{ height: '30%' }}>
          <Col span={24}>
            <Tabs
              style={{ height: '100%' }}
              className={'work-console-tab'}
              defaultActiveKey="1"
              items={initItems()}
              onChange={onChange}
            />
          </Col>
        </Row>
        <WorkConfigDrawer
          work={work}
          isModalVisible={isModalVisible}
          handleCancel={() => {
            setIsModalVisible(false)
            getWork()
          }}
        />
      </div>
    </>
  )
}

export default WorkPage
