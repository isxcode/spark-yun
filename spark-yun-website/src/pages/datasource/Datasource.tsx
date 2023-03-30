import React, {useEffect, useState} from 'react'
import './Datasource.scss'
import {Button, Form, Input, message, Space, Table, Tag, theme} from 'antd'
import { type ColumnsType } from 'antd/es/table'
import { useNavigate } from 'react-router-dom'
import {AddNodeModal} from "../engine/node/modal/AddNodeModal";
import {AddDatasourceModal} from "./modal/AddDatasourceModal";
import axios from "axios";

interface DataType {
  id: string
  name: string
  age: number
  address: string
}

function Datasource () {

  const [isModalVisible, setIsModalVisible] = useState(false)
  const handleOk = () => { setIsModalVisible(true) }
  const handleCancel = () => { setIsModalVisible(false) }

  const [datasources, setDatasources] = useState([])

  const queryDatasources = () => {
    axios({
      method: 'get',
      url: 'http://localhost:8080/datasource/queryDatasource',
    })
      .then(function (response) {
        setDatasources(response.data)
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  };

  const delDatasource = (value) => {
    axios({
      method: 'get',
      url: 'http://localhost:8080/datasource/delDatasource?datasourceId=' + value
    })
      .then(function (response) {
        queryDatasources()
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  const testConnect = (value) => {
    axios({
      method: 'post',
      url: 'http://localhost:8080/datasource/testConnect',
      data: {
        datasourceId: value
      }
    })
      .then(function (response) {
        if (!response.data.canConnect) {
          message.warning(response.data.message);
        }
        queryDatasources()
      })
      .catch(function (error) {
        message.error(error.response.data.message)
      })
  }

  useEffect(() => { queryDatasources() }, [])

  const columns: ColumnsType<DataType> = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name'
    },
    {
      title: '连接信息',
      dataIndex: 'jdbcUrl',
      key: 'jdbcUrl'
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username'
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type'
    },
    {
      title: '健康状态',
      dataIndex: 'status',
      key: 'status'
    },
    {
      title: '检测时间',
      key: 'checkTime',
      dataIndex: 'checkTime'
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
          <Button>编辑</Button>
          <Button onClick={() => testConnect(record.id)}>检测</Button>
          <Button onClick={() => delDatasource(record.id)}>删除</Button>
        </Space>
      )
    }
  ]

  return (
    <>
      <div className={"datasource-bar"}>
        <Button onClick={() => { handleOk() }} >添加数据源</Button>
        <Input></Input>
        <Button>搜索</Button>
      </div>

      <Table columns={columns} dataSource={datasources} />

      <AddDatasourceModal
        handleCancel={handleCancel}
        handleOk={handleOk}
        isModalVisible={isModalVisible}
        queryDatasources={queryDatasources}
      />
    </>
  )
}

export default Datasource
