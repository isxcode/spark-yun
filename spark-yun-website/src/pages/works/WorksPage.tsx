import React, { useEffect, useState } from 'react'
import { Button, Col, Input, message, Row, Space, Table, Tag } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { useNavigate, useParams } from 'react-router-dom'
import { WorkModal } from '../../modals/work/WorkModal'
import './WorksPage.less'
import { type WorkRow } from '../../types/woks/info/WorkRow'
import { type BasePagination, defaultPagination } from '../../types/base/BasePagination'
import { type QueryWorkReq } from '../../types/woks/req/QueryWorkReq'
import {
  deleteWorkApi,
  delWorkApi,
  depolyWorkApi,
  pauseWorkApi,
  queryWorkApi,
  resumeWorkApi
} from '../../services/works/WorksService'

function WorksPage() {
  const navigate = useNavigate()
  const { workflowId } = useParams()

  const [works, setWorks] = useState<WorkRow[]>([])
  const [work, setWork] = useState<WorkRow>()
  const [pagination, setPagination] = useState<BasePagination>(defaultPagination)
  const [isModalVisible, setIsModalVisible] = useState(false)

  useEffect(() => {
    fetchWorks()
  }, [])

  const queryWorkReq: QueryWorkReq = {
    workflowId,
    page: pagination.currentPage,
    pageSize: pagination.pageSize,
    searchKeyWord: pagination.searchKeyWord
  }

  const fetchWorks = () => {
    queryWorkApi(queryWorkReq).then(function (response) {
      setWorks(response.content)
      setPagination((prevPagination) => ({
        ...prevPagination,
        totalItems: response.totalElements
      }))
    })
  }

  const handleOk = () => {
    fetchWorks()
    setIsModalVisible(false)
  }

  const depolyWork = (workId: string) => {
    depolyWorkApi(workId).then(function () {
      fetchWorks()
    })
  }

  const resumeWork = (workId: string) => {
    resumeWorkApi(workId).then(function () {
      fetchWorks()
    })
  }

  const stopWork = (workId: string) => {
    deleteWorkApi(workId).then(function () {
      fetchWorks()
    })
  }

  const pauseWork = (workId: string) => {
    pauseWorkApi(workId).then(function () {
      fetchWorks()
    })
  }

  const delWork = (workId: string | undefined) => {
    delWorkApi(workId).then(function () {
      fetchWorks()
    })
  }

  const handleSearch = () => {
    setPagination((prevPagination) => ({
      ...prevPagination,
      currentPage: 1
    }))
    fetchWorks()
  }

  const columns: ColumnsType<WorkRow> = [
    {
      title: '作业名称',
      dataIndex: 'name',
      key: 'name',
      width: 200,
      render: (text, record) => (
        <a
          className={'sy-table-a'}
          onClick={() => {
            navigate('/work/' + record.id)
          }}>
          {text}
        </a>
      )
    },
    {
      title: '类型',
      dataIndex: 'workType',
      key: 'workType',
      width: 120,
      render: (_, record) => (
        <Space size="middle">
          {record.workType === 'EXE_JDBC' && <Tag color="default">EXE_JDBC</Tag>}
          {record.workType === 'QUERY_JDBC' && <Tag color="default">QUERY_JDBC</Tag>}
          {record.workType === 'SPARK_SQL' && <Tag color="default">SPARK_SQL</Tag>}
        </Space>
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (_, record) => (
        <Space size="middle">
          {record.status === 'PUBLISHED' && <Tag color="green">已发布</Tag>}
          {record.status === 'STOP' && <Tag color="red">已下线</Tag>}
          {record.status === 'PAUSED' && <Tag color="orange">已暂停</Tag>}
          {record.status === 'UN_PUBLISHED' && <Tag color="cyan">未发布</Tag>}
        </Space>
      )
    },
    {
      title: '创建时间',
      dataIndex: 'createDateTime',
      key: 'createDateTime',
      width: 220
    },
    {
      title: '备注',
      key: 'remark',
      dataIndex: 'remark'
    },
    {
      title: '操作',
      key: 'action',
      width: 320,
      render: (_, record) => (
        <Space size="middle">
          <a
            className={'sy-table-a'}
            onClick={() => {
              setWork(record)
              setIsModalVisible(true)
            }}>
            编辑
          </a>
          {record.status === 'STOP' && (
            <a
              className={'sy-table-a'}
              onClick={() => {
                depolyWork(record.id as string)
              }}>
              发布
            </a>
          )}
          {record.status === ('UN_PUBLISHED' || 'STOP') && (
            <a
              className={'sy-table-a'}
              onClick={() => {
                depolyWork(record.id as string)
              }}>
              发布
            </a>
          )}
          {record.status === 'PAUSED' && (
            <a
              className={'sy-table-a'}
              onClick={() => {
                resumeWork(record.id as string)
              }}>
              重启
            </a>
          )}
          {record.status === 'PUBLISHED' && (
            <a
              className={'sy-table-a'}
              onClick={() => {
                pauseWork(record.id as string)
              }}>
              暂停
            </a>
          )}
          {record.status === 'PAUSED' && (
            <a
              className={'sy-table-a'}
              onClick={() => {
                stopWork(record.id as string)
              }}>
              下线
            </a>
          )}
          {record.status === 'PUBLISHED' && (
            <a
              className={'sy-table-a'}
              onClick={() => {
                stopWork(record.id as string)
              }}>
              下线
            </a>
          )}
          <a
            className={'sy-table-a'}
            onClick={() => {
              setWork({})
              delWork(record.id)
            }}>
            删除
          </a>
        </Space>
      )
    }
  ]

  return (
    <div style={{ padding: 24 }}>
      <Row className={'works-bar'}>
        <Col span={8}>
          <Button
            type={'primary'}
            onClick={() => {
              setWork({})
              setIsModalVisible(true)
            }}>
            添加作业
          </Button>
        </Col>
        <Col span={7} offset={9} style={{ textAlign: 'right', display: 'flex' }}>
          <Input
            style={{ marginRight: '10px' }}
            onPressEnter={handleSearch}
            defaultValue={queryWorkReq.searchKeyWord}
            onChange={(e) => {
              setPagination({ ...pagination, searchKeyWord: e.target.value })
            }}
            placeholder={'名称/类型/备注'}
          />
          <Button type={'primary'} onClick={handleSearch}>
            搜索
          </Button>
        </Col>
      </Row>
      <Table
        columns={columns}
        dataSource={works}
        pagination={{
          current: pagination.currentPage,
          pageSize: pagination.pageSize,
          total: pagination.totalItems,
          onChange: (currentPage: number) => {
            setPagination({ ...pagination, currentPage })
          }
        }}
      />

      <WorkModal
        workflowId={workflowId as string}
        handleCancel={() => {
          setIsModalVisible(false)
        }}
        handleOk={handleOk}
        isModalVisible={isModalVisible}
        work={work}
      />
    </div>
  )
}

export default WorksPage
