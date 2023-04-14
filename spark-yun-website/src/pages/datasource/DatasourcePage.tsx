import React, { useEffect, useState } from 'react'
import { Button, Col, Input, Row, Space, Table, Tag } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { DatasourceModal } from '../../modals/datasource/DatasourceModal'
import './DatasourcePage.less'
import { type DatasourceRow } from '../../types/datasource/info/DatasourceRow'
import { type BasePagination, defaultPagination } from '../../types/base/BasePagination'
import { type QueryDatasourceReq } from '../../types/datasource/req/QueryDatasourceReq'
import { delDatasourceApi, queryDatasourceApi, testDatasourceApi } from '../../services/datasource/DatasourceService'

function DatasourcePage() {
  const [datasources, setDatasources] = useState<DatasourceRow[]>([])
  const [datasource, setDatasource] = useState<DatasourceRow>()
  const [pagination, setPagination] = useState<BasePagination>(defaultPagination)
  const [isModalVisible, setIsModalVisible] = useState(false)

  useEffect(() => {
    fetchDatasources()
  }, [pagination.currentPage])

  const queryDatasourceReq: QueryDatasourceReq = {
    page: pagination.currentPage,
    pageSize: pagination.pageSize,
    searchContent: pagination.searchContent
  }

  const fetchDatasources = () => {
    queryDatasourceApi(queryDatasourceReq).then(function (response) {
      setDatasources(response.content)
      setPagination((prevPagination) => ({
        ...prevPagination,
        totalItems: response.totalElements
      }))
    })
  }

  const handleOk = () => {
    fetchDatasources()
    setIsModalVisible(false)
  }

  const delDatasource = (datasourceId: string | undefined) => {
    delDatasourceApi(datasourceId).then(function () {
      fetchDatasources()
    })
  }

  const testDatasource = (datasourceId: string) => {
    testDatasourceApi(datasourceId).then(function () {
      fetchDatasources()
    })
  }

  const handleSearch = () => {
    setPagination((prevPagination) => ({
      ...prevPagination,
      currentPage: 1
    }))
    fetchDatasources()
  }

  const columns: ColumnsType<DatasourceRow> = [
    {
      title: '数据源名称',
      dataIndex: 'name',
      key: 'name',
      width: 140,
      render: (text, record) => (
        <a
          onClick={() => {
            setDatasource(record)
            setIsModalVisible(true)
          }}
          className={'sy-table-a'}>
          {text}
        </a>
      )
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 80,
      render: (_, record) => (
        <Space size="middle">
          <Tag color="default">{record.type}</Tag>
        </Space>
      )
    },
    {
      title: '连接信息',
      dataIndex: 'jdbcUrl',
      key: 'jdbcUrl',
      width: 250
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 100
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (_, record) => (
        <Space size="middle">
          {record.status === 'UN_CHECK' && <Tag color="blue">未检测</Tag>}
          {record.status === 'ACTIVE' && <Tag color="green">可用</Tag>}
          {record.status === 'FAIL' && <Tag color="red">连接失败</Tag>}
        </Space>
      )
    },
    {
      title: '检测时间',
      key: 'checkTime',
      dataIndex: 'checkTime',
      width: 180
    },
    {
      title: '备注',
      key: 'comment',
      dataIndex: 'comment'
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <a
            className={'sy-table-a'}
            onClick={() => {
              setDatasource(record)
              setIsModalVisible(true)
            }}>
            编辑
          </a>
          <a
            className={'sy-table-a'}
            onClick={() => {
              testDatasource(record.id as string)
            }}>
            检测
          </a>
          <a
            className={'sy-table-a'}
            onClick={() => {
              delDatasource(record.id)
            }}>
            删除
          </a>
        </Space>
      )
    }
  ]

  return (
    <div style={{ padding: 24 }}>
      <Row className={'datasource-bar'}>
        <Col span={8}>
          <Button
            type={'primary'}
            onClick={() => {
              setDatasource({})
              setIsModalVisible(true)
            }}>
            添加数据源
          </Button>
        </Col>
        <Col span={7} offset={9} style={{ textAlign: 'right', display: 'flex' }}>
          <Input
            style={{ marginRight: '10px' }}
            onPressEnter={handleSearch}
            defaultValue={queryDatasourceReq.searchContent}
            onChange={(e) => {
              setPagination({ ...pagination, searchContent: e.target.value })
            }}
            placeholder={'名称/类型/连接信息/用户名/备注'}
          />
          <Button type={'primary'} onClick={handleSearch}>
            搜索
          </Button>
        </Col>
      </Row>

      <Table columns={columns} dataSource={datasources} />

      <DatasourceModal
        datasource={datasource}
        handleCancel={() => {
          setIsModalVisible(false)
        }}
        handleOk={handleOk}
        isModalVisible={isModalVisible}
      />
    </div>
  )
}

export default DatasourcePage
