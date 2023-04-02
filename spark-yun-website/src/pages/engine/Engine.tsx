import React, { useEffect, useState } from 'react'
import './Engine.scss'
import { Button, Form, Input, message, Space, Table, Tag, theme } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { useNavigate } from 'react-router-dom'
import { AddEngineModal } from './modal/AddEngineModal'
import axios from 'axios'

interface DataType {
  id: string
  name: string
  node: number
  memory: string
  storage: string
  status: string
  check: string
  comment: string
}

function Engine () {
  const [engines, setEngines] = useState([])

  useEffect(() => {
    queryEngines()
  }, [])

  const queryEngines = () => {
    axios({
      method: 'get',
      url: process.env.API_PREFIX_URL + '/engine/queryEngine'
    })
      .then(function (response) {
        console.log(response)
        response.data.map((server) => {
          server.value = server.id
          server.title = server.name
          return server
        })
        setEngines(response.data)
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const delEngine = (value) => {
    axios({
      method: 'get',
      url: process.env.API_PREFIX_URL + '/engine/delEngine?engineId=' + value
    })
      .then(function (response) {
        console.log(response)
        queryEngines()
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const navigate = useNavigate()

  const [isModalVisible, setIsModalVisible] = useState(false)
  const handleOk = () => {
    setIsModalVisible(true)
  }
  const handleCancel = () => {
    setIsModalVisible(false)
  }

  const columns: ColumnsType<DataType> = [
    {
      title: '集群名称',
      dataIndex: 'name',
      key: 'name',
      width: 160,
      render: (text, record) => (
        <a
          onClick={() => {
            navigate('/node/' + record.id)
          }}>
          {text}
        </a>
      )
    },
    {
      title: '可用/总节点数',
      dataIndex: 'node',
      key: 'node',
      width: 130
    },
    {
      title: '可用/总内存',
      dataIndex: 'memory',
      key: 'memory',
      width: 120
    },
    {
      title: '可用/总存储',
      dataIndex: 'storage',
      key: 'storage',
      width: 120
    },
    {
      title: '检测状态',
      dataIndex: 'status',
      key: 'status',
      width: 100
    },
    {
      title: '检测时间',
      dataIndex: 'checkTime',
      key: 'checkTime',
      width: 200
    },
    {
      title: '备注',
      key: 'comment',
      dataIndex: 'comment',
      width: 250
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <a>编辑</a>
          <a>检测</a>
          <a
            onClick={() => {
              delEngine(record.id)
            }}>
            删除
          </a>
        </Space>
      )
    }
  ]

  return (
    <>
      <div className={'engine-bar'}>
        <Button
          onClick={() => {
            handleOk()
          }}>
          添加集群
        </Button>
        <Input></Input>
        <Button>搜索</Button>
      </div>
      <Table columns={columns} dataSource={engines} />
      <AddEngineModal
        handleCancel={handleCancel}
        handleOk={handleOk}
        isModalVisible={isModalVisible}
        queryEngines={queryEngines}
      />
    </>
  )
}

export default Engine
