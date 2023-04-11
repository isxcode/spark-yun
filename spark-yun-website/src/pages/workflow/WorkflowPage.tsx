import React, { useEffect, useState } from 'react'
import { Button, Space, Table } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { useNavigate } from 'react-router-dom'
import { type WorkflowRow } from '../../types/workflow/info/WorkflowRow'
import './Workflow.less'
import { type BasePagination, defaultPagination } from '../../types/base/BasePagination'
import { type WofQueryWorkflowReq } from '../../types/workflow/req/WofQueryWorkflowReq'
import { delWorkflowApi, queryWorkflowApi } from '../../services/worflow/WorkflowService'

function WorkflowPage() {
  const navigate = useNavigate()

  const [workflows, setWorkflows] = useState<WorkflowRow[]>([])
  const [pagination, setPagination] = useState<BasePagination>(defaultPagination)

  useEffect(() => {
    fetchWorkflows()
  }, [pagination.currentPage])

  const queryWorkflowReq: WofQueryWorkflowReq = {
    page: pagination.currentPage,
    pageSize: pagination.pageSize,
    contentSearch: ''
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

  const delWorkflow = (workflowId: string) => {
    delWorkflowApi(workflowId).then(function () {
      fetchWorkflows()
    })
  }

  const columns: ColumnsType<WorkflowRow> = [
    {
      title: '工作流名称',
      dataIndex: 'name',
      key: 'name',
      render: (text, record) => (
        <a
          onClick={() => {
            navigate('/works/' + record.id)
          }}>
          {text}
        </a>
      )
    },
    {
      title: '调度状态',
      key: 'status',
      dataIndex: 'status'
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
          <a>编辑</a>
          <a
            onClick={() => {
              delWorkflow(record.id)
            }}>
            删除
          </a>
        </Space>
      )
    }
  ]

  return (
    <>
      <div className={'workflow-bar'}>
        <Button type={'primary'}>添加作业流</Button>
      </div>
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
    </>
  )
}

export default WorkflowPage
