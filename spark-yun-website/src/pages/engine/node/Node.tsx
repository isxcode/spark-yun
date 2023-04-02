import React, { useEffect, useState } from 'react'
import './Node.scss'
import { Button, Form, Input, message, Space, Table, Tag, theme } from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { useParams } from 'react-router-dom'
import axios from 'axios'
import { AddEngineModal } from '../modal/AddEngineModal'
import { AddNodeModal } from './modal/AddNodeModal'
import engine from '../Engine'

interface DataType {
  id: string
  name: string
  host: string
  username: string
  cpu: string
  memory: string
  storage: string
  status: string
  checkTime: string
}

function Node () {
  const [isModalVisible, setIsModalVisible] = useState(false)
  const handleOk = () => {
    setIsModalVisible(true)
  }
  const handleCancel = () => {
    setIsModalVisible(false)
  }

  const [nodes, setNodes] = useState([])
  const queryNodes = (value) => {
    axios({
      method: 'get',
      url: process.env.API_PREFIX_URL + '/node/queryNode?engineId=' + value
    })
      .then(function (response) {
        setNodes(response.data)
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const delNode = (value) => {
    axios({
      method: 'get',
      url: process.env.API_PREFIX_URL + '/node/delNode?nodeId=' + value
    })
      .then(function (response) {
        queryNodes(engineId)
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const checkNode = (value) => {
    axios({
      method: 'get',
      url: process.env.API_PREFIX_URL + '/node/checkAgent?nodeId=' + value
    })
      .then(function (response) {
        queryNodes(engineId)
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const installAgent = (value) => {
    axios({
      method: 'get',
      url: process.env.API_PREFIX_URL + '/node/installAgent?nodeId=' + value
    })
      .then(function (response) {
        queryNodes(engineId)
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const removeAgent = (value) => {
    axios({
      method: 'get',
      url: process.env.API_PREFIX_URL + '/node/removeAgent?nodeId=' + value
    })
      .then(function (response) {
        queryNodes(engineId)
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const { engineId } = useParams()

  useEffect(() => {
    queryNodes(engineId)
  }, [])

  const columns: ColumnsType<DataType> = [
    {
      title: '节点名称',
      dataIndex: 'name',
      key: 'name'
    },
    {
      title: '地址',
      dataIndex: 'host',
      key: 'host'
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username'
    },
    {
      title: 'CPU占用率',
      dataIndex: 'cpu',
      key: 'cpu'
    },
    {
      title: '可用/总内存',
      dataIndex: 'memory',
      key: 'memory'
    },
    {
      title: '可用/总存储',
      dataIndex: 'storage',
      key: 'storage'
    },
    {
      title: '健康状态',
      dataIndex: 'status',
      key: 'status'
    },
    {
      title: '检测时间',
      dataIndex: 'checkTime',
      key: 'checkTime'
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
          <a
            onClick={() => {
              checkNode(record.id)
            }}>
            检测
          </a>
          <a
            onClick={() => {
              installAgent(record.id)
            }}>
            安装
          </a>
          <a
            onClick={() => {
              removeAgent(record.id)
            }}>
            卸载
          </a>
          <a
            onClick={() => {
              delNode(record.id)
            }}>
            删除
          </a>
          <a>更多</a>
        </Space>
      )
    }
  ]

  return (
    <>
      <div className={'node-bar'}>
        <Button
          onClick={() => {
            handleOk()
          }}>
          添加节点
        </Button>
        <Input></Input>
        <Button>搜索</Button>
      </div>
      <Table columns={columns} dataSource={nodes} />
      <AddNodeModal
        handleCancel={handleCancel}
        handleOk={handleOk}
        isModalVisible={isModalVisible}
        queryNodes={queryNodes}
        engineId={engineId}
      />
    </>
  )
}

export default Node
