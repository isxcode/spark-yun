import React, { useEffect, useState } from 'react'
import { Button, Col, Input, Row, Space, Table, Tag } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { DatasourceModal } from '../../modals/datasource/DatasourceModal'
import './LicensePage.less'
import { type DatasourceRow } from '../../types/datasource/info/DatasourceRow'
import { type BasePagination, defaultPagination } from '../../types/base/BasePagination'
import { type QueryDatasourceReq } from '../../types/datasource/req/QueryDatasourceReq'
import { delDatasourceApi, queryDatasourceApi, testDatasourceApi } from '../../services/datasource/DatasourceService'

function LicensePage() {
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
    searchKeyWord: pagination.searchKeyWord
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
      title: '证书编号',
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
      title: '过期时间',
      dataIndex: 'type',
      key: 'type',
      width: 100,
      render: (_, record) => (
        <Space size="middle">
          <Tag color="default">{record.dbType}</Tag>
        </Space>
      )
    },
    {
      title: '生成时间',
      dataIndex: 'jdbcUrl',
      key: 'jdbcUrl',
      width: 150
    },
    {
      title: '最大成员数',
      dataIndex: 'jdbcUrl',
      key: 'jdbcUrl',
      width: 150
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
            删除
          </a>
          <a
            className={'sy-table-a'}
            onClick={() => {
              delDatasource(record.id)
            }}>
            启用
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
            上传证书
          </Button>
        </Col>
        <Col span={7} offset={9} style={{ textAlign: 'right', display: 'flex' }}>
          <Input
            style={{ marginRight: '10px' }}
            onPressEnter={handleSearch}
            defaultValue={queryDatasourceReq.searchKeyWord}
            onChange={(e) => {
              setPagination({ ...pagination, searchKeyWord: e.target.value })
            }}
            placeholder={'用户名/账号/手机号/邮箱/备注'}
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

export default LicensePage
