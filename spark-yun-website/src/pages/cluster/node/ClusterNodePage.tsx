import React, { useEffect, useState } from 'react'
import { Button, Col, Input, Row, Space, Table, Tag } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { useParams } from 'react-router-dom'
import './ClusterNodePage.less'
import { BasePagination, defaultPagination } from '../../../types/base/BasePagination'
import { QueryEngineNodeReq } from '../../../types/calculate/node/req/QueryEngineNodeReq'
import {
  checkAgentApi,
  delEngineNodeApi,
  installAgentApi,
  queryEngineNodesApi,
  removeAgentApi
} from '../../../services/cluster/node/ClusterNodeService'
import { type EngineNodeRow } from '../../../types/calculate/node/info/EngineNodeRow'
import { ClusterNodeModal } from '../../../modals/cluster/node/ClusterNodeModal'

function ClusterNodePage() {
  const { clusterId } = useParams()
  const [engineNodes, setEngineNodes] = useState<EngineNodeRow[]>([])
  const [engineNode, setEngineNode] = useState<EngineNodeRow>()
  const [pagination, setPagination] = useState<BasePagination>(defaultPagination)
  const [isModalVisible, setIsModalVisible] = useState(false)

  useEffect(() => {
    fetchEngineNodes()
  }, [pagination.currentPage])

  const queryEngineNodeReq: QueryEngineNodeReq = {
    clusterId: clusterId as string,
    page: pagination.currentPage,
    pageSize: pagination.pageSize,
    searchKeyWord: pagination.searchKeyWord
  }

  const fetchEngineNodes = () => {
    queryEngineNodesApi(queryEngineNodeReq).then(function (response) {
      setEngineNodes(response.content)
      setPagination((prevPagination) => ({
        ...prevPagination,
        totalItems: response.totalElements
      }))
    })
  }

  const handleOk = () => {
    fetchEngineNodes()
    setIsModalVisible(false)
  }

  const handleSearch = () => {
    setPagination((prevPagination) => ({
      ...prevPagination,
      currentPage: 1
    }))
    fetchEngineNodes()
  }

  const delEngineNode = (engineNodeId: string | undefined) => {
    delEngineNodeApi(engineNodeId).then(function () {
      fetchEngineNodes()
    })
  }

  const checkAgent = (engineNodeId: string | undefined) => {
    checkAgentApi(engineNodeId).then(function () {
      fetchEngineNodes()
    })
  }

  const installAgent = (engineNodeId: string | undefined) => {
    installAgentApi(engineNodeId).then(function () {
      fetchEngineNodes()
    })
  }

  const removeAgent = (engineNodeId: string | undefined) => {
    removeAgentApi(engineNodeId).then(function () {
      fetchEngineNodes()
    })
  }

  const columns: ColumnsType<EngineNodeRow> = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      width: 100
      // render: (text, record) => (
      //   <a
      //     onClick={() => {
      //       setEngineNode(record)
      //       setIsModalVisible(true)
      //     }}
      //     className={'sy-table-a'}>
      //     {text}
      //   </a>
      // )
    },
    {
      title: '地址',
      dataIndex: 'host',
      key: 'host',
      width: 100
    },
    {
      title: 'CPU',
      dataIndex: 'cpu',
      key: 'cpu',
      width: 80,
      render: (text, record) => <>{text}%</>
    },
    {
      title: '内存',
      dataIndex: 'memory',
      key: 'memory',
      width: 110
    },
    {
      title: '存储',
      dataIndex: 'storage',
      key: 'storage',
      width: 110
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (_, record) => (
        <Space size="middle">
          {record.status === 'NEW' && <Tag color="blue">未检测</Tag>}
          {record.status === 'CAN_INSTALL' && <Tag color="green">可安装</Tag>}
          {record.status === 'CAN_NOT_INSTALL' && <Tag color="orange">不可安装</Tag>}
          {record.status === 'ACTIVE' && <Tag color="green">可用</Tag>}
          {record.status === 'UNINSTALLED' && <Tag color="default">已卸载</Tag>}
          {record.status === 'UN_CHECK' && <Tag color="cyan">待检测</Tag>}
          {record.status === 'INSTALL_ERROR' && <Tag color="red">安装失败</Tag>}
          {record.status === 'CHECK_ERROR' && <Tag color="red">检测失败</Tag>}
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
      dataIndex: 'remark',
      width: 100
    },
    {
      title: '操作',
      key: 'action',
      width: 300,
      render: (_, record) => (
        <Space size="middle">
          <a
            className={'sy-table-a'}
            onClick={() => {
              delEngineNode(record.id)
            }}>
            编辑
          </a>
          <a
            className={'sy-table-a'}
            onClick={() => {
              checkAgent(record.id)
            }}>
            检测
          </a>
          {/* <a */}
          {/*  className={'sy-table-a'} */}
          {/*  onClick={() => { */}
          {/*    installAgent(record.id) */}
          {/*  }}> */}
          {/*  安装 */}
          {/* </a> */}
          {/* <a */}
          {/*  className={'sy-table-a'} */}
          {/*  onClick={() => { */}
          {/*    removeAgent(record.id) */}
          {/*  }}> */}
          {/*  卸载 */}
          {/* </a> */}
          <a
            className={'sy-table-a'}
            onClick={() => {
              delEngineNode(record.id)
            }}>
            日志
          </a>
          <a
            className={'sy-table-a'}
            onClick={() => {
              installAgent(record.id)
            }}>
            安装
          </a>
          <a
            className={'sy-table-a'}
            onClick={() => {
              delEngineNode(record.id)
            }}>
            启动
          </a>
          <a
            className={'sy-table-a'}
            onClick={() => {
              delEngineNode(record.id)
            }}>
            删除
          </a>
        </Space>
      )
    }
  ]

  return (
    <div style={{ padding: 24 }}>
      <Row className={'node-bar'}>
        <Col span={8}>
          <Button
            type={'primary'}
            onClick={() => {
              setEngineNode({})
              setIsModalVisible(true)
            }}>
            添加节点
          </Button>
        </Col>
        <Col span={7} offset={9} style={{ textAlign: 'right', display: 'flex' }}>
          <Input
            style={{ marginRight: '10px' }}
            onPressEnter={handleSearch}
            defaultValue={queryEngineNodeReq.searchKeyWord}
            onChange={(e) => {
              setPagination({ ...pagination, searchKeyWord: e.target.value })
            }}
            placeholder={'名称/地址/备注'}
          />
          <Button type={'primary'} onClick={handleSearch}>
            搜索
          </Button>
        </Col>
      </Row>
      <Table columns={columns} dataSource={engineNodes} />
      <ClusterNodeModal
        calculateEngineId={clusterId as string}
        engineNode={engineNode}
        isModalVisible={isModalVisible}
        handleCancel={() => {
          setIsModalVisible(false)
        }}
        handleOk={handleOk}
      />
    </div>
  )
}

export default ClusterNodePage
