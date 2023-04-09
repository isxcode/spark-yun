import React, { useEffect, useState } from 'react'
import { Button, Form, Input, message, Space, Table, Tag, theme } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { useNavigate, useParams } from 'react-router-dom'
import { AddWorkflowModal } from '../../../modals/workflow/AddWorkflowModal'
import { AddWorkModal } from './modal/AddWorkModal'
import axios from 'axios'

interface DataType {
  id: string
  name: string
  age: number
  address: string
  tags: string[]
}

function Worklist () {
  const navigate = useNavigate()

  const { workflowId } = useParams()

  const [works, setWorks] = useState<DataType[]>([])

  useEffect(() => {
    queryWork()
  }, [])

  const [isModalVisible, setIsModalVisible] = useState(false)
  const handleOk = () => {
    setIsModalVisible(true)
  }
  const handleCancel = () => {
    setIsModalVisible(false)
  }

  const queryWork = () => {
    axios({
      method: 'post',
      url: process.env.API_PREFIX_URL + '/wok/queryWork',
      data:{
        workflowId:workflowId,
        page:0,
        pageSize: 10,
      }
    })
      .then(function (response) {
        setWorks(response.data.data.content)
      })
      .catch(function (error) {
        message.error(error.response.data.data.msg)
      })
  }

  const delWork = (value) => {
    axios({
      method: 'get',
      url: process.env.API_PREFIX_URL + '/workflow/delWork?workId=' + value
    })
      .then(function (response) {
        console.log(response)
        queryWork()
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const columns: ColumnsType<DataType> = [
    {
      title: '作业名称',
      dataIndex: 'name',
      key: 'name',
      render: (text, record) => (
        <a
          onClick={() => {
            navigate('/work/' + record.id)
          }}>
          {text}
        </a>
      )
    },
    {
      title: '类型',
      dataIndex: 'workType',
      key: 'workType'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status'
    },
    {
      title: '创建时间',
      dataIndex: 'createDateTime',
      key: 'createDateTime'
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
              delWork(record.id)
            }}>
            删除
          </a>
          <a>操作历史</a>
        </Space>
      )
    }
  ]

  return (
    <>
      <div className={'worklist-bar'}>
        <Button
          onClick={() => {
            handleOk()
          }}>
          添加作业
        </Button>
        <Input></Input>
        <Button>搜索</Button>
      </div>

      <Table columns={columns} dataSource={works} />

      <AddWorkModal
        handleCancel={handleCancel}
        handleOk={handleOk}
        isModalVisible={isModalVisible}
        queryWork={queryWork}
        workflowId={workflowId}
      />
    </>
  )
}

export default Worklist
