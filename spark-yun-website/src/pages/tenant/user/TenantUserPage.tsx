import React, { useEffect, useState } from 'react'
import { Button, Col, Input, Row, Space, Table, Tag } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import './TenantUserPage.less'
import { type BasePagination, defaultPagination } from '../../../types/base/BasePagination'
import { TenantUserRow } from '../../../types/tenant/user/info/TenantUserRow'
import { QueryTenantUserReq } from '../../../types/tenant/user/req/QueryTenantUserReq'
import {
  queryTenantUserApi,
  removeTenantAdminApi,
  removeTenantUserApi,
  setTenantAdminApi
} from '../../../services/tenant/user/TenantUserService'
import { TenantUserModal } from '../../../modals/tenant/user/TenantUserModal'

function TenantUserPage() {
  const [tenantUsers, setTenantUsers] = useState<TenantUserRow[]>([])
  const [tenantUser, setTenantUser] = useState<TenantUserRow>()
  const [pagination, setPagination] = useState<BasePagination>(defaultPagination)
  const [isModalVisible, setIsModalVisible] = useState(false)

  useEffect(() => {
    fetchTenantUser()
  }, [pagination.currentPage])

  const queryTenantUserReq: QueryTenantUserReq = {
    page: pagination.currentPage,
    pageSize: pagination.pageSize,
    searchKeyWord: pagination.searchKeyWord
  }

  const fetchTenantUser = () => {
    queryTenantUserApi(queryTenantUserReq).then(function (response) {
      setTenantUsers(response.content)
      setPagination((prevPagination) => ({
        ...prevPagination,
        totalItems: response.totalElements
      }))
    })
  }

  const handleOk = () => {
    fetchTenantUser()
    setIsModalVisible(false)
  }

  const setTenantAdmin = (userId: string | undefined) => {
    setTenantAdminApi(userId).then(function () {
      fetchTenantUser()
    })
  }

  const removeTenantAdmin = (userId: string | undefined) => {
    removeTenantAdminApi(userId).then(function () {
      fetchTenantUser()
    })
  }

  const deleteMember = (userId: string | undefined) => {
    removeTenantUserApi(userId).then(function () {
      fetchTenantUser()
    })
  }

  const handleSearch = () => {
    setPagination((prevPagination) => ({
      ...prevPagination,
      currentPage: 1
    }))
    fetchTenantUser()
  }

  const columns: ColumnsType<TenantUserRow> = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 120
    },
    {
      title: '账号',
      dataIndex: 'account',
      key: 'account',
      width: 150
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone',
      width: 180
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
      width: 250
    },
    {
      title: '角色',
      dataIndex: 'roleCode',
      key: 'roleCode',
      width: 80,
      render: (_, record) => (
        <Space size="middle">
          {record.roleCode === 'ROLE_TENANT_ADMIN' && <Tag color="green">管理员</Tag>}
          {record.roleCode === 'ROLE_TENANT_MEMBER' && <Tag color="orange">成员</Tag>}
        </Space>
      )
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          {record.roleCode === 'ROLE_TENANT_ADMIN'
? (
            <a
              className={'sy-table-a'}
              onClick={() => {
                removeTenantAdmin(record.id)
              }}>
              取消授权
            </a>
          )
: (
            <a
              className={'sy-table-a'}
              onClick={() => {
                setTenantAdmin(record.id)
              }}>
              授权管理
            </a>
          )}
          <a
            className={'sy-table-a'}
            onClick={() => {
              deleteMember(record.id)
            }}>
            移除
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
              setIsModalVisible(true)
            }}>
            添加成员
          </Button>
        </Col>
        <Col span={7} offset={9} style={{ textAlign: 'right', display: 'flex' }}>
          <Input
            style={{ marginRight: '10px' }}
            onPressEnter={handleSearch}
            defaultValue={queryTenantUserReq.searchKeyWord}
            onChange={(e) => {
              setPagination({ ...pagination, searchKeyWord: e.target.value })
            }}
            placeholder={'用户名/账号/手机号/邮箱'}
          />
          <Button type={'primary'} onClick={handleSearch}>
            搜索
          </Button>
        </Col>
      </Row>

      <Table columns={columns} dataSource={tenantUsers} />

      <TenantUserModal
        isModalVisible={isModalVisible}
        handleCancel={() => {
          setIsModalVisible(false)
        }}
        handleOk={handleOk}
      />
    </div>
  )
}

export default TenantUserPage
