import React, { useEffect, useState } from 'react'
import { Button, Col, Input, message, Row, Space, Table, Tag } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { useNavigate } from 'react-router-dom'
import { type WorkflowRow } from '../../types/workflow/info/WorkflowRow'
import './WorkflowPage.less'
import { type BasePagination, defaultPagination } from '../../types/base/BasePagination'
import { type WofQueryWorkflowReq } from '../../types/workflow/req/WofQueryWorkflowReq'
import { delWorkflowApi, queryWorkflowApi } from '../../services/worflow/WorkflowService'
import { WorkflowModal } from '../../modals/workflow/WorkflowModal'

function WorkflowPage() {
  const navigate = useNavigate()

  const [workflows, setWorkflows] = useState<WorkflowRow[]>([])
  const [workflow, setWorkflow] = useState<WorkflowRow>()
  const [pagination, setPagination] = useState<BasePagination>(defaultPagination)
  const [isModalVisible, setIsModalVisible] = useState(false)

  useEffect(() => {
    fetchWorkflows()
  }, [pagination.currentPage])

  const queryWorkflowReq: WofQueryWorkflowReq = {
    page: pagination.currentPage,
    pageSize: pagination.pageSize,
    searchKeyWord: pagination.searchKeyWord
  }

  const fetchWorkflows = () => {
    queryWorkflowApi(queryWorkflowReq).then(function (response) {
      setWorkflows(response.content)
      setPagination((prevPagination) => ({
        ...prevPagination,
        totalItems: response.totalElements
      }))
    })
  }

  const handleOk = () => {
    fetchWorkflows()
    setIsModalVisible(false)
  }

  const handleSearch = () => {
    setPagination((prevPagination) => ({
      ...prevPagination,
      currentPage: 1
    }))
    fetchWorkflows()
  }

  const delWorkflow = (workflowId: string | undefined) => {
    delWorkflowApi(workflowId).then(function () {
      fetchWorkflows()
    })
  }

  const columns: ColumnsType<WorkflowRow> = [
    {
      title: '工作流名称',
      dataIndex: 'name',
      key: 'name',
      width: 200,
      render: (text, record) => (
        <a
          className={'sy-table-a'}
          onClick={() => {
            navigate('/works/' + record.id)
          }}>
          {text}
        </a>
      )
    },
    {
      title: '状态',
      key: 'status',
      dataIndex: 'status',
      width: 120,
      render: (_, record) => (
        <Space size="middle">
          {record.status === 'UN_AUTO' ? <Tag color="blue">未运行</Tag> : <Tag color="green">调度中</Tag>}
        </Space>
      )
    },
    {
      title: '创建人',
      key: 'creator',
      dataIndex: 'creator'
    },
    {
      title: '创建时间',
      key: 'creator',
      dataIndex: 'creator'
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
              setWorkflow(record)
              setIsModalVisible(true)
            }}>
            编辑
          </a>
          <a
            className={'sy-table-a'}
            onClick={() => {
              setWorkflow({})
              delWorkflow(record.id)
            }}>
            删除
          </a>
          {/*<a*/}
          {/*  className={'sy-table-a'}*/}
          {/*  onClick={() => {*/}
          {/*    message.warning('需上传企业许可证').then((r) => {})*/}
          {/*  }}>*/}
          {/*  发布*/}
          {/*</a>*/}
        </Space>
      )
    }
  ]

  return (
    <div style={{ padding: 24 }}>
      <Row className={'workflow-bar'}>
        <Col span={8}>
          <Button
            type={'primary'}
            onClick={() => {
              setWorkflow({})
              setIsModalVisible(true)
            }}>
            添加作业流
          </Button>
        </Col>
        <Col span={7} offset={9} style={{ textAlign: 'right', display: 'flex' }}>
          <Input
            style={{ marginRight: '10px' }}
            onPressEnter={handleSearch}
            defaultValue={queryWorkflowReq.searchKeyWord}
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
        dataSource={workflows}
        pagination={{
          current: pagination.currentPage,
          pageSize: pagination.pageSize,
          total: pagination.totalItems,
          onChange: (currentPage: number) => {
            setPagination({ ...pagination, currentPage })
          }
        }}
      />
      <WorkflowModal
        workflow={workflow}
        isModalVisible={isModalVisible}
        handleCancel={() => {
          setIsModalVisible(false)
        }}
        handleOk={handleOk}
      />
    </div>
  )
}

export default WorkflowPage
