import React, { useEffect, useState } from 'react'
import './Workflow.scss'
import { Button, Form, Input, message, Space, Table, Tag, theme } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { useNavigate } from 'react-router-dom'
import { AddEngineModal } from '../engine/modal/AddEngineModal'
import { AddWorkflowModal } from './modal/AddWorkflowModal'
import axios from 'axios'

interface DataType {
  id: string
  name: string
  age: number
  address: string
}

function Workflow () {
  const [workflows, setWorkflows] = useState<DataType[]>([])

  const [isModalVisible, setIsModalVisible] = useState(false)
  const handleOk = () => {
    setIsModalVisible(true)
  }
  const handleCancel = () => {
    setIsModalVisible(false)
  }

  const {
    token: { colorBgContainer, colorPrimary }
  } = theme.useToken()

  const navigate = useNavigate()

  useEffect(() => {
    queryWorkflow()
  }, [])

  const queryWorkflow = () => {
    axios({
      method: 'post',
      url: process.env.API_PREFIX_URL + '/wof/queryWorkflow',
      data: {
        page: 0,
        pageSize: 10
      }
    })
      .then(function (response) {
        console.log(response)
        setWorkflows(response.data.data.content)
      })
      .catch(function (error) {
        message.error(error.response.data.data.msg)
      })
  }

  const delWorkflow = (value) => {
    axios({
      method: 'get',
      url: process.env.API_PREFIX_URL + '/workflow/delWorkflow?workflowId=' + value
    })
      .then(function (response) {
        console.log(response)
        queryWorkflow()
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const columns: ColumnsType<DataType> = [
    {
      title: '工作流名称',
      dataIndex: 'name',
      key: 'name',
      render: (text, record) => (
        <a style={{color: colorPrimary}}
          onClick={() => {
            navigate('/worklist/' + record.id)
          }}>
          {text}
        </a>
      )
    },
    {
      title: '备注',
      key: 'comment',
      dataIndex: 'comment'
    },
    {
      title: '调度状态',
      dataIndex: 'status',
      key: 'status'
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <a style={{color:colorPrimary}}>编辑</a>
          <a style={{color:colorPrimary}}
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
        <Button
          onClick={() => {
            handleOk()
          }}>
          添加作业流
        </Button>
        <Input></Input>
        <Button>搜索</Button>
      </div>

      <Table columns={columns} dataSource={workflows} />

      <AddWorkflowModal
        handleCancel={handleCancel}
        handleOk={handleOk}
        isModalVisible={isModalVisible}
        queryWorkflow={queryWorkflow}
      />
    </>
  )
}

export default Workflow
