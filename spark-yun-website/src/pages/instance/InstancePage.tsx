import React, { useEffect, useState } from 'react'
import { Button, Col, Input, Modal, Row, Select, Space, Table, Tag } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import './InstancePage.less'
import { type BasePagination, defaultPagination } from '../../types/base/BasePagination'
import { type QueryDatasourceReq } from '../../types/datasource/req/QueryDatasourceReq'
import { WorkInstanceRow } from '../../types/woks/info/WorkInstanceRow'
import {
  deleteWorkInstanceApi,
  getSubmitLogApi,
  getWorkDataApi,
  getWorkLogApi,
  queryWorkInstanceApi,
  restartWorkInstanceApi,
  stopWorkInstanceApi
} from '../../services/works/WorksService'
import { RunWorkRes } from '../../types/woks/res/RunWorkRes'

const { Option } = Select

function InstancePage() {
  const [instances, setInstances] = useState<WorkInstanceRow[]>([])
  const [pagination, setPagination] = useState<BasePagination>(defaultPagination)
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [connectLog, setConnectLog] = useState('')
  const [isLogModalVisible, setIsLogModalVisible] = useState(false)
  const [isYarnLogModalVisible, setIsYarnLogModalVisible] = useState(false)
  const [isDataModalVisible, setDataModalVisible] = useState(false)
  const [result, setResult] = useState<RunWorkRes>()

  useEffect(() => {
    fetchInstances()
  }, [pagination.currentPage])

  const queryDatasourceReq: QueryDatasourceReq = {
    page: pagination.currentPage,
    pageSize: pagination.pageSize,
    searchKeyWord: pagination.searchKeyWord
  }

  const getData = (instanceId: string) => {
    getWorkDataApi(instanceId).then(function (response) {
      setResult({ ...result, data: response.data })
      setDataModalVisible(true)
    })
  }

  const deleteInstance = (instanceId: string) => {
    deleteWorkInstanceApi(instanceId).then(function () {
      fetchInstances()
    })
  }

  const restartInstance = (instanceId: string) => {
    restartWorkInstanceApi(instanceId).then(function () {
      fetchInstances()
    })
  }

  const stopInstance = (instanceId: string) => {
    stopWorkInstanceApi(instanceId).then(function () {
      fetchInstances()
    })
  }

  const fetchInstances = () => {
    queryWorkInstanceApi(queryDatasourceReq).then(function (response) {
      setInstances(response.content)
      setPagination((prevPagination) => ({
        ...prevPagination,
        totalItems: response.totalElements
      }))
    })
  }

  // const handleOk = () => {
  //   fetchDatasources()
  //   setIsModalVisible(false)
  // }

  const getConnectLog = (instanceId: string) => {
    getSubmitLogApi(instanceId).then(function (res) {
      setConnectLog(res.log as string)
    })
  }

  const getYarnLog = (instanceId: string) => {
    getWorkLogApi(instanceId).then(function (res) {
      setConnectLog(res.yarnLog as string)
      setIsYarnLogModalVisible(true)
    })
  }

  const handleSearch = () => {
    setPagination((prevPagination) => ({
      ...prevPagination,
      currentPage: 1
    }))
    fetchInstances()
  }

  const columns: ColumnsType<WorkInstanceRow> = [
    {
      title: '实例编码',
      key: 'id',
      dataIndex: 'id',
      ellipsis: true,
      width: 120
    },
    // {
    //   title: '作业流',
    //   key: 'workflowName',
    //   dataIndex: 'workflowName',
    //   ellipsis: true,
    //   width: 120
    // },
    {
      title: '作业',
      key: 'workName',
      dataIndex: 'workName',
      ellipsis: true,
      width: 120
    },
    {
      title: '类型',
      key: 'instanceType',
      dataIndex: 'instanceType',
      width: 80,
      render: (_, record) => (
        <Space size="middle">
          {record.instanceType === 'WORK' && <Tag>调度执行</Tag>}
          {record.instanceType === 'MANUAL' && <Tag>手动执行</Tag>}
        </Space>
      )
    },
    {
      title: '状态',
      key: 'status',
      dataIndex: 'status',
      width: 80,
      render: (_, record) => (
        <Space size="middle">
          {record.status === 'SUCCESS' && <Tag color="green">成功</Tag>}
          {record.status === 'FAIL' && <Tag color="red">失败</Tag>}
          {record.status === 'ABORT' && <Tag color="orange">已中止</Tag>}
          {record.status === 'RUNNING' && <Tag color="blue">运行中</Tag>}
          {record.status == null && <Tag color="red">未运行</Tag>}
        </Space>
      )
    },
    {
      title: '执行时间',
      key: 'execStartDateTime',
      dataIndex: 'execStartDateTime',
      ellipsis: true,
      width: 160
    },
    {
      title: '结束时间',
      key: 'execEndDateTime',
      dataIndex: 'execEndDateTime',
      ellipsis: true,
      width: 160
    },
    {
      title: '下次计划时间',
      key: 'nextPlanDateTime',
      dataIndex: 'nextPlanDateTime',
      ellipsis: true,
      width: 160
    },
    {
      title: '操作',
      key: 'action',
      width: 240,
      render: (_, record) => (
        <Space size="middle">
          {record.instanceType === 'WORK' &&
            (record.status === 'SUCCESS' || record.status === 'FAIL' || record.status === 'ABORT') && (
              <a
                className={'sy-table-a'}
                onClick={() => {
                  restartInstance(record.id as string)
                }}>
                重跑
              </a>
            )}
          {record.status === 'RUNNING' && record.workType === 'SPARK_SQL' && (
            <a
              className={'sy-table-a'}
              onClick={() => {
                stopInstance(record.id as string)
              }}>
              中止
            </a>
          )}
          <a
            className={'sy-table-a'}
            onClick={() => {
              getConnectLog(record.id as string)
              setIsLogModalVisible(true)
            }}>
            日志
          </a>
          {record.workType === 'SPARK_SQL' && (
            <a
              className={'sy-table-a'}
              onClick={() => {
                getYarnLog(record.id as string)
              }}>
              Yarn日志
            </a>
          )}

          {(record.status === 'SUCCESS' && (record.workType === 'SPARK_SQL' || record.workType === 'QUERY_JDBC')) && (
            <a
              className={'sy-table-a'}
              onClick={() => {
                getData(record.id as string)
              }}>
              数据
            </a>
          )}
          <a
            className={'sy-table-a'}
            onClick={() => {
              deleteInstance(record.id as string)
            }}>
            删除
          </a>
        </Space>
      )
    }
  ]

  const getColumns = () => {
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

  return (
    <div style={{ padding: 24 }}>
      <Row className={'datasource-bar'}>
        <Col span={7} offset={17} style={{ textAlign: 'right', display: 'flex' }}>
          <Input
            style={{ marginRight: '10px' }}
            onPressEnter={handleSearch}
            defaultValue={queryDatasourceReq.searchKeyWord}
            onChange={(e) => {
              setPagination({ ...pagination, searchKeyWord: e.target.value })
            }}
            placeholder={'实例编码/作业/作业流'}
          />
          <Button type={'primary'} onClick={handleSearch}>
            搜索
          </Button>
          <Button
            type={'primary'}
            onClick={() => {
              fetchInstances()
            }}
            style={{ marginLeft: '8px' }}>
            刷新
          </Button>
        </Col>
      </Row>

      <Table
        pagination={{
          current: pagination.currentPage,
          pageSize: pagination.pageSize,
          total: pagination.totalItems,
          onChange: (currentPage: number) => {
            setPagination({ ...pagination, currentPage })
          }
        }}
        columns={columns}
        dataSource={instances}
      />

      <Modal
        title="日志"
        width={800}
        open={isLogModalVisible}
        onCancel={() => {
          setIsLogModalVisible(false)
        }}
        footer={<></>}>
        <pre style={{ overflowY: 'scroll', maxHeight: '200px', whiteSpace: 'pre-wrap' }}>{connectLog}</pre>
      </Modal>

      <Modal
        title="结果"
        width={800}
        open={isDataModalVisible}
        onCancel={() => {
          setDataModalVisible(false)
        }}
        footer={<></>}>
        <Table columns={getColumns()} dataSource={data()} scroll={{ y: 200 }} />
      </Modal>

      <Modal
        title="Yarn日志"
        width={1000}
        open={isYarnLogModalVisible}
        onCancel={() => {
          setIsYarnLogModalVisible(false)
        }}
        footer={<></>}>
        <pre style={{ overflowY: 'scroll', maxHeight: '200px', whiteSpace: 'pre-wrap' }}>{connectLog}</pre>
      </Modal>
    </div>
  )
}

export default InstancePage
