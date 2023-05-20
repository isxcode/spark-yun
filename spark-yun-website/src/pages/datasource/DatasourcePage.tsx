import React, { useEffect, useState } from 'react'
import { Button, Col, Input, Modal, Row, Space, Table, Tag, Tooltip } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { DatasourceModal } from '../../modals/datasource/DatasourceModal'
import './DatasourcePage.less'
import { type DatasourceRow } from '../../types/datasource/info/DatasourceRow'
import { type BasePagination, defaultPagination } from '../../types/base/BasePagination'
import { type QueryDatasourceReq } from '../../types/datasource/req/QueryDatasourceReq'
import {
  delDatasourceApi,
  getConnectLogApi,
  queryDatasourceApi,
  testDatasourceApi
} from '../../services/datasource/DatasourceService'

function DatasourcePage() {
  const [datasources, setDatasources] = useState<DatasourceRow[]>([])
  const [datasource, setDatasource] = useState<DatasourceRow>()
  const [pagination, setPagination] = useState<BasePagination>(defaultPagination)
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [connectLog, setConnectLog] = useState('')
  const [isLogModalVisible, setIsLogModalVisible] = useState(false)

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

  const getConnectLog = (datasourceId: string) => {
    getConnectLogApi(datasourceId).then(function (res) {
      setConnectLog(res.connectLog)
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
      ellipsis: true,
      width: 130
      // render: (text, record) => (
      //   <a
      //     onClick={() => {
      //       setDatasource(record)
      //       setIsModalVisible(true)
      //     }}
      //     className={'sy-table-a'}>
      //     {text}
      //   </a>
      // )
    },
    {
      title: '类型',
      dataIndex: 'dbType',
      key: 'dbType',
      width: 120,
      render: (_, record) => (
        <Space size="middle">
          <Tag color="default">{record.dbType}</Tag>
        </Space>
      )
    },
    {
      title: '连接信息',
      dataIndex: 'jdbcUrl',
      key: 'jdbcUrl',
      width: 250,
      ellipsis: true
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
      width: 100,
      render: (_, record) => (
        <Space size="middle">
          {record.status === 'UN_CHECK' && <Tag color="blue">未检测</Tag>}
          {record.status === 'ACTIVE' && <Tag color="green">可用</Tag>}
          {record.status === 'FAIL' && <Tag color="red">不可用</Tag>}
        </Space>
      )
    },
    {
      title: '检测时间',
      key: 'checkDateTime',
      dataIndex: 'checkDateTime',
      width: 180
    },
    {
      title: '备注',
      key: 'remark',
      dataIndex: 'remark',
      width: 170
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
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
              getConnectLog(record.id as string)
              setIsLogModalVisible(true)
            }}>
            日志
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
  ];

  return (
    <div style={{padding: 24}}>
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
        <Col span={7} offset={9} style={{textAlign: 'right', display: 'flex'}}>
          <Input
            style={{marginRight: '10px'}}
            onPressEnter={handleSearch}
            defaultValue={queryDatasourceReq.searchKeyWord}
            onChange={(e) => {
              setPagination({...pagination, searchKeyWord: e.target.value})
            }}
            placeholder={'名称/类型/连接信息/用户名/备注'}
          />
          <Button type={'primary'} onClick={handleSearch}>
            搜索
          </Button>
        </Col>
      </Row>

      <Table columns={columns} dataSource={datasources}/>

      <DatasourceModal
        datasource={datasource}
        handleCancel={() => {
          setIsModalVisible(false)
        }}
        handleOk={handleOk}
        isModalVisible={isModalVisible}
      />

      <Modal
        title="连接日志"
        open={isLogModalVisible}
        onCancel={() => {
          setIsLogModalVisible(false)
        }}
        footer={<></>}>
        <p>{connectLog}</p>
      </Modal>
    </div>
  );
}

export default DatasourcePage
