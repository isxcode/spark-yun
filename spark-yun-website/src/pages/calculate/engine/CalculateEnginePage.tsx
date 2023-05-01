import React, { useEffect, useState } from 'react'
import { Button, Col, Input, Row, Space, Table, Tag } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { useNavigate } from 'react-router-dom'
import './CalculateEnginePage.less'
import { BasePagination, defaultPagination } from '../../../types/base/BasePagination'
import { CalculateEngineRow } from '../../../types/calculate/engine/info/CalculateEngineRow'
import { QueryEngineReq } from '../../../types/calculate/engine/req/QueryEngineReq'
import {
  checkEngineApi,
  delEngineApi,
  queryEnginesApi
} from '../../../services/calculate/engine/CalculateEngineService'
import { EngineModal } from '../../../modals/calculate/engine/EngineModal'

function CalculateEnginePage() {
  const navigate = useNavigate()
  const [calculates, setCalculates] = useState<CalculateEngineRow[]>([])
  const [calculate, setCalculate] = useState<CalculateEngineRow>()
  const [pagination, setPagination] = useState<BasePagination>(defaultPagination)
  const [isModalVisible, setIsModalVisible] = useState(false)

  useEffect(() => {
    fetchEngines()
  }, [pagination.currentPage])

  const queryEnginesReq: QueryEngineReq = {
    page: pagination.currentPage,
    pageSize: pagination.pageSize,
    searchKeyWord: pagination.searchKeyWord
  }

  const fetchEngines = () => {
    queryEnginesApi(queryEnginesReq).then(function (response) {
      setCalculates(response.content)
      setPagination((prevPagination) => ({
        ...prevPagination,
        totalItems: response.totalElements
      }))
    })
  }

  const handleOk = () => {
    fetchEngines()
    setIsModalVisible(false)
  }

  const delEngine = (engineId: string | undefined) => {
    delEngineApi(engineId).then(function () {
      fetchEngines()
    })
  }

  const checkEngine = (engineId: string | undefined) => {
    checkEngineApi(engineId).then(function () {
      fetchEngines()
    })
  }

  const handleSearch = () => {
    setPagination((prevPagination) => ({
      ...prevPagination,
      currentPage: 1
    }))
    fetchEngines()
  }

  const columns: ColumnsType<CalculateEngineRow> = [
    {
      title: '集群名称',
      dataIndex: 'name',
      key: 'name',
      width: 120,
      render: (text, record) => (
        <a
          className={'sy-table-a'}
          onClick={() => {
            navigate('/nodes/' + record.id)
          }}>
          {text}
        </a>
      )
    },
    {
      title: '可用/总节点数',
      dataIndex: 'node',
      key: 'node',
      width: 130
    },
    {
      title: '已用/总内存',
      dataIndex: 'memory',
      key: 'memory',
      width: 120
    },
    {
      title: '已用/总存储',
      dataIndex: 'storage',
      key: 'storage',
      width: 120
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (_, record) => (
        <Space size="middle">
          {record.status === 'NEW' && <Tag color="blue">待配置</Tag>}
          {record.status === 'ACTIVE' && <Tag color="green">可用</Tag>}
          {record.status === 'FAIL' && <Tag color="red">连接失败</Tag>}
          {record.status === 'UN_CHECK' && <Tag color="cyan">待检测</Tag>}
          {record.status === 'NO_ACTIVE' && <Tag color="red">不可用</Tag>}
        </Space>
      )
    },
    {
      title: '检测时间',
      dataIndex: 'checkTime',
      key: 'checkTime',
      width: 200
    },
    {
      title: '备注',
      key: 'comment',
      dataIndex: 'comment'
    },
    {
      title: '操作',
      key: 'action',
      width: 250,
      render: (_, record) => (
        <Space size="middle">
          <a
            className={'sy-table-a'}
            onClick={() => {
              setCalculate(record)
              setIsModalVisible(true)
            }}>
            编辑
          </a>
          <a
            className={'sy-table-a'}
            onClick={() => {
              checkEngine(record.id)
            }}>
            {' '}
            检测
          </a>
          <a
            className={'sy-table-a'}
            onClick={() => {
              navigate('/nodes/' + record.id)
            }}>
            节点
          </a>
          <a
            className={'sy-table-a'}
            onClick={() => {
              setCalculate({})
              delEngine(record.id)
            }}>
            删除
          </a>
        </Space>
      )
    }
  ]

  return (
    <div style={{ padding: 24 }}>
      <Row className={'engine-bar'}>
        <Col span={8}>
          <Button
            type={'primary'}
            onClick={() => {
              setCalculate({})
              setIsModalVisible(true)
            }}>
            添加集群
          </Button>
        </Col>
        <Col span={7} offset={9} style={{ textAlign: 'right', display: 'flex' }}>
          <Input
            style={{ marginRight: '10px' }}
            onPressEnter={handleSearch}
            defaultValue={queryEnginesReq.searchKeyWord}
            onChange={(e) => {
              setPagination({ ...pagination, searchKeyWord: e.target.value })
            }}
            placeholder={'名称/备注'}
          />
          <Button type={'primary'} onClick={handleSearch}>
            搜索
          </Button>
        </Col>
      </Row>
      <Table
        columns={columns}
        dataSource={calculates}
        pagination={{
          current: pagination.currentPage,
          pageSize: pagination.pageSize,
          total: pagination.totalItems,
          onChange: (currentPage: number) => {
            setPagination({ ...pagination, currentPage })
          }
        }}
      />
      <EngineModal
        engine={calculate}
        handleCancel={() => {
          setIsModalVisible(false)
        }}
        handleOk={handleOk}
        isModalVisible={isModalVisible}
      />
    </div>
  )
}

export default CalculateEnginePage
