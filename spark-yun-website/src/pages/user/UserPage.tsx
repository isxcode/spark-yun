import React, {useEffect, useState} from 'react'
import {Button, Col, Input, Row, Space, Table, Tag} from 'antd'
import {type ColumnsType} from 'antd/es/table'
import './UserPage.less'
import {type BasePagination, defaultPagination} from '../../types/base/BasePagination'
import {delDatasourceApi} from '../../services/datasource/DatasourceService'
import {UserRow} from "../../types/user/info/UserRow";
import {delUserApi, disableUserApi, enableUserApi, queryAllUsersApi} from "../../services/user/UserService";
import {QueryAllUserReq} from "../../types/user/req/QueryAllUserReq";
import {UserModal} from "../../modals/user/UserModal";

function UserPage() {
  const [users, setUsers] = useState<UserRow[]>([])
  const [user, setUser] = useState<UserRow>()
  const [pagination, setPagination] = useState<BasePagination>(defaultPagination)
  const [isModalVisible, setIsModalVisible] = useState(false);

  useEffect(() => {
    fetchUsers()
  }, [pagination.currentPage])

  const queryAllUserReq: QueryAllUserReq = {
    page: pagination.currentPage,
    pageSize: pagination.pageSize,
    searchKeyWord: pagination.searchKeyWord
  }

  const fetchUsers = () => {
    queryAllUsersApi(queryAllUserReq).then(function (response) {
      setUsers(response.content)
      setPagination((prevPagination) => ({
        ...prevPagination,
        totalItems: response.totalElements
      }))
    })
  };

  const handleOk = () => {
    fetchUsers()
    setIsModalVisible(false)
  }

  const delUser = (datasourceId: string | undefined) => {
    delUserApi(datasourceId).then(function () {
      fetchUsers()
    });
  }

  const enableUser = (userId: string | undefined) => {
    enableUserApi(userId).then(function () {
      fetchUsers()
    })
  }

  const disableUser = (userId: string | undefined) => {
    disableUserApi(userId).then(function () {
      fetchUsers()
    })
  }

  const handleSearch = () => {
    setPagination((prevPagination) => ({
      ...prevPagination,
      currentPage: 1
    }))
    fetchUsers()
  }

  const columns: ColumnsType<UserRow> = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 100
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
      width: 150
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
      width: 160
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (_, record) => (
        <Space size="middle">
          {record.status === 'ENABLE' && <Tag color="green">启用</Tag>}
          {record.status === 'DISABLE' && <Tag color="red">禁用</Tag>}
        </Space>),
    },
    {
      title: '备注',
      key: 'remark',
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
              setUser(record)
              setIsModalVisible(true)
            }}>
            编辑
          </a>
          {
            record.status === "ENABLE" ? <a
              className={'sy-table-a'}
              onClick={() => {
                disableUser(record.id)
              }}>
              禁用
            </a> : <a
              className={'sy-table-a'}
              onClick={() => {
                enableUser(record.id)
              }}>
              启用
            </a>
          }
          <a
            className={'sy-table-a'}
            onClick={() => {
              delUser(record.id)
            }}>
            删除
          </a>
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: 24 }}>
      <Row className={'datasource-bar'}>
        <Col span={8}>
          <Button
            type={'primary'}
            onClick={() => {
              setUser({})
              setIsModalVisible(true)
            }}>
            创建用户
          </Button>
        </Col>
        <Col span={7} offset={9} style={{ textAlign: 'right', display: 'flex' }}>
          <Input
            style={{ marginRight: '10px' }}
            onPressEnter={handleSearch}
            defaultValue={queryAllUserReq.searchKeyWord}
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

      <Table columns={columns} dataSource={users} />

      <UserModal
        user={user}
        handleCancel={() => {
          setIsModalVisible(false)
        }}
        handleOk={handleOk}
        isModalVisible={isModalVisible}
      />
    </div>
  )
}

export default UserPage
