import React, { useEffect, useState } from 'react'
import { Button, Col, Input, Row, Space, Table, Tag } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import './TenantPage.less'
import { type BasePagination, defaultPagination } from '../../types/base/BasePagination'
import { delDatasourceApi } from '../../services/datasource/DatasourceService'
import { TenantRow } from '../../types/tenant/info/TenantRow'
import {
  checkTenantApi,
  delTenantApi,
  disableTenantApi,
  enableTenantApi,
  queryTenantsApi
} from '../../services/tenant/TenantService'
import { QueryTenantsReq } from '../../types/tenant/req/QueryTenantsReq'
import { TenantModal } from '../../modals/tenant/TenantModal'

function TenantPage() {
  const [tenants, setTenants] = useState<TenantRow[]>([])
  const [tenant, setTenant] = useState<TenantRow>()
  const [pagination, setPagination] = useState<BasePagination>(defaultPagination)
  const [isModalVisible, setIsModalVisible] = useState(false)

  useEffect(() => {
    fetchTenant()
  }, [pagination.currentPage])

  const queryTenantsReq: QueryTenantsReq = {
    page: pagination.currentPage,
    pageSize: pagination.pageSize,
    searchKeyWord: pagination.searchKeyWord
  }

  const fetchTenant = () => {
    queryTenantsApi(queryTenantsReq).then(function (response) {
      setTenants(response.content)
      setPagination((prevPagination) => ({
        ...prevPagination,
        totalItems: response.totalElements
      }))
    })
  }

  const handleOk = () => {
    fetchTenant()
    setIsModalVisible(false)
  }

  const delDatasource = (datasourceId: string | undefined) => {
    delDatasourceApi(datasourceId).then(function () {
      fetchTenant()
    })
  }

  const enableTenant = (tenantId: string | undefined) => {
    enableTenantApi(tenantId).then(function () {
      fetchTenant()
    })
  }

  const disableTenant = (tenantId: string | undefined) => {
    disableTenantApi(tenantId).then(function () {
      fetchTenant()
    })
  }

  const checkTenant = (tenantId: string | undefined) => {
    checkTenantApi(tenantId).then(function () {
      fetchTenant()
    })
  }

  const delTenant = (tenantId: string | undefined) => {
    delTenantApi(tenantId).then(function () {
      fetchTenant()
    })
  }

  const handleSearch = () => {
    setPagination((prevPagination) => ({
      ...prevPagination,
      currentPage: 1
    }))
    fetchTenant()
  }

  const columns: ColumnsType<TenantRow> = [
    {
      title: '租户名称',
      dataIndex: 'name',
      key: 'name',
      width: 140,
      render: (text, record) => (
        <a
          onClick={() => {
            setTenant(record)
            setIsModalVisible(true)
          }}
          className={'sy-table-a'}>
          {text}
        </a>
      )
    },
    {
      title: '已用/总成员',
      dataIndex: 'member',
      key: 'member',
      width: 120,
      render: (text, record) => (
        <>
          {record.usedMemberNum}/{record.maxMemberNum}
        </>
      )
    },
    {
      title: '已用/总工作流',
      dataIndex: 'workflow',
      key: 'workflow',
      width: 130,
      render: (text, record) => (
        <>
          {record.usedWorkflowNum}/{record.maxWorkflowNum}
        </>
      )
    },
    {
      title: '状态',
      dataIndex: 'jdbcUrl',
      key: 'jdbcUrl',
      width: 100,
      render: (_, record) => (
        <Space size="middle">
          {record.status === 'ENABLE' && <Tag color="green">启用</Tag>}
          {record.status === 'DISABLE' && <Tag color="red">禁用</Tag>}
        </Space>
      )
    },
    {
      title: '检测时间',
      dataIndex: 'checkDateTime',
      key: 'checkDateTime',
      width: 180
    },
    {
      title: '备注',
      key: 'remark',
      width: 250,
      dataIndex: 'remark'
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <a
            className={'sy-table-a'}
            onClick={() => {
              setTenant(record)
              setIsModalVisible(true)
            }}>
            编辑
          </a>
          <a
            className={'sy-table-a'}
            onClick={() => {
              checkTenant(record.id)
            }}>
            同步
          </a>
          {record.status === 'ENABLE'
? (
            <a
              className={'sy-table-a'}
              onClick={() => {
                disableTenant(record.id)
              }}>
              禁用
            </a>
          )
: (
            <a
              className={'sy-table-a'}
              onClick={() => {
                enableTenant(record.id)
              }}>
              启用
            </a>
          )}
          <a
            className={'sy-table-a'}
            onClick={() => {
              delTenant(record.id)
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
              setTenant({})
              setIsModalVisible(true)
            }}>
            创建租户
          </Button>
        </Col>
        <Col span={7} offset={9} style={{ textAlign: 'right', display: 'flex' }}>
          <Input
            style={{ marginRight: '10px' }}
            onPressEnter={handleSearch}
            defaultValue={queryTenantsReq.searchKeyWord}
            onChange={(e) => {
              setPagination({ ...pagination, searchKeyWord: e.target.value })
            }}
            placeholder={'租户名称/备注'}
          />
          <Button type={'primary'} onClick={handleSearch}>
            搜索
          </Button>
        </Col>
      </Row>

      <Table columns={columns} dataSource={tenants} />

      <TenantModal
        tenant={tenant}
        handleCancel={() => {
          setIsModalVisible(false)
        }}
        handleOk={handleOk}
        isModalVisible={isModalVisible}
      />
    </div>
  )
}

export default TenantPage
