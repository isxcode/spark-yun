import React, {useState} from 'react'
import './Worklist.scss'
import { Button, Form, Input, Space, Table, Tag, theme } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { useNavigate } from 'react-router-dom'
import {AddWorkflowModal} from "../modal/AddWorkflowModal";
import {AddWorkModal} from "./modal/AddWorkModal";

interface DataType {
  id: string
  name: string
  age: number
  address: string
  tags: string[]
}

function Worklist () {
  const navigate = useNavigate()

  const [isModalVisible, setIsModalVisible] = useState(false)
  const handleOk = () => { setIsModalVisible(true) }
  const handleCancel = () => { setIsModalVisible(false) }

  const columns: ColumnsType<DataType> = [
    {
      title: '作业名称',
      dataIndex: 'name',
      key: 'name',
      render: (text,record) => <a onClick={() => { navigate('/work/'+record.id) }}>{text}</a>
    },
    {
      title: '类型',
      dataIndex: 'node',
      key: 'node'
    },
    {
      title: '创建时间',
      dataIndex: 'age',
      key: 'age'
    },
    {
      title: '备注',
      key: 'comment',
      dataIndex: 'comment'
    },
    {
      title: '标签',
      dataIndex: 'node',
      key: 'node'
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <a>编辑</a>
          <a>删除</a>
          <a>操作历史</a>
        </Space>
      )
    }
  ]

  return (
    <>
      <div className={'worklist-bar'}>
        <Button onClick={() => { handleOk()}}>添加作业</Button>
        <Input></Input>
        <Button>搜索</Button>
      </div>

      <Table columns={columns} dataSource={[]} />

      <AddWorkModal
        handleCancel={handleCancel}
        handleOk={handleOk}
        isModalVisible={isModalVisible}
        queryWorkflow={()=>{}}
      />
    </>
  )
}

export default Worklist
