import React, { useEffect, useState } from 'react'
import { Button, Col, Input, Row, Select, Space, Table, Tag, Tooltip } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { DatasourceModal } from '../../modals/datasource/DatasourceModal'
import './InstancePage.less'
import { type DatasourceRow } from '../../types/datasource/info/DatasourceRow'
import { type BasePagination, defaultPagination } from '../../types/base/BasePagination'
import { type QueryDatasourceReq } from '../../types/datasource/req/QueryDatasourceReq'
import { delDatasourceApi, queryDatasourceApi, testDatasourceApi } from '../../services/datasource/DatasourceService'
import { WorkInstanceRow } from '../../types/woks/info/WorkInstanceRow'
import { deleteWorkInstanceApi, queryWorkInstanceApi } from '../../services/works/WorksService'

const { Option } = Select

function InstancePage() {
  const [instances, setInstances] = useState<WorkInstanceRow[]>([])
  const [pagination, setPagination] = useState<BasePagination>(defaultPagination)
  const [isModalVisible, setIsModalVisible] = useState(false)

  useEffect(() => {
    fetchInstances()
  }, [pagination.currentPage])

  const queryDatasourceReq: QueryDatasourceReq = {
    page: pagination.currentPage,
    pageSize: pagination.pageSize,
    searchKeyWord: pagination.searchKeyWord
  }

  const deleteInstance = (instanceId: string) => {
    deleteWorkInstanceApi(instanceId).then(function () {
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
    {
      title: '作业流',
      key: 'workflowName',
      dataIndex: 'workflowName',
      ellipsis: true,
      width: 120
    },
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
          {record.instanceType === 'WORK' && <Tag>作业</Tag>}
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
          {record.status === 'RUNNING' && <Tag color="blue">运行中</Tag>}
          {record.status == null && <Tag color="red">未运行</Tag>}
        </Space>
      )
    },
    {
      title: '执行时间',
      key: 'execStartDateTime',
      dataIndex: 'execStartDateTime',
      width: 180
    },
    {
      title: '结束时间',
      key: 'execEndDateTime',
      dataIndex: 'execEndDateTime',
      width: 180
    },
    {
      title: '下次计划时间',
      key: 'nextPlanDateTime',
      dataIndex: 'nextPlanDateTime',
      width: 180
    },
    {
      title: '操作',
      key: 'action',
      width: 220,
      render: (_, record) => (
        <Space size="middle">
          <a className={'sy-table-a'} onClick={() => {}}>
            重跑
          </a>
          <a className={'sy-table-a'} onClick={() => {}}>
            日志
          </a>
          <a className={'sy-table-a'} onClick={() => {}}>
            数据
          </a>
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

      {/* <DatasourceModal */}
      {/*  datasource={datasource} */}
      {/*  handleCancel={() => { */}
      {/*    setIsModalVisible(false) */}
      {/*  }} */}
      {/*  handleOk={handleOk} */}
      {/*  isModalVisible={isModalVisible} */}
      {/* /> */}
    </div>
  )
}

export default InstancePage
