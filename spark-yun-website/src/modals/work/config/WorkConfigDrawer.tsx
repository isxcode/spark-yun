import React, { useEffect, useState } from 'react'
import { Button, Col, Drawer, Form, Row, Select } from 'antd'
import './WorkConfigDrawer.less'
import { ConfigWorkReq } from '../../../types/woks/req/ConfigWorkReq'
import { configWorkApi } from '../../../services/works/WorksService'
import { WorkInfo } from '../../../types/woks/info/WorkInfo'
import { DatasourceRow } from '../../../types/datasource/info/DatasourceRow'
import { QueryDatasourceReq } from '../../../types/datasource/req/QueryDatasourceReq'
import { queryDatasourceApi } from '../../../services/datasource/DatasourceService'
import { CalculateEngineRow } from '../../../types/calculate/engine/info/CalculateEngineRow'
import { queryEnginesApi } from '../../../services/calculate/engine/CalculateEngineService'
import { QueryEngineReq } from '../../../types/calculate/engine/req/QueryEngineReq'

const { Option } = Select

export const WorkConfigDrawer = (props: { isModalVisible: boolean, handleCancel: () => void, work?: WorkInfo }) => {
  const { isModalVisible, handleCancel, work } = props
  const [form] = Form.useForm()
  const [datasources, setDatasources] = useState<DatasourceRow[]>([])
  const [calculates, setCalculates] = useState<CalculateEngineRow[]>([])

  const queryDatasourceReq: QueryDatasourceReq = {
    page: 1,
    pageSize: 999,
    searchContent: ''
  }
  const fetchDatasources = () => {
    queryDatasourceApi(queryDatasourceReq).then(function (response) {
      setDatasources(response.content)
    })
  }

  const queryEnginesReq: QueryEngineReq = {
    page: 1,
    pageSize: 999,
    searchContent: ''
  }

  const fetchEngines = () => {
    queryEnginesApi(queryEnginesReq).then(function (response) {
      setCalculates(response.content)
    })
  }

  useEffect(() => {
    if (work?.workType === 'SPARK_SQL') {
      fetchEngines()
    } else {
      fetchDatasources()
    }
  }, [work])

  const configWorkReq: ConfigWorkReq = {
    workId: work?.workId,
    calculateEngineId: work?.calculateId as string,
    datasourceId: work?.datasourceId as string,
    sql: work?.sql
  }

  const configWork = () => {
    configWorkApi(configWorkReq).then(() => {})
  }

  const onFinish = (values: any) => {
    configWorkReq.calculateEngineId = values.engineId
    configWorkReq.datasourceId = values.datasourceId
    configWork()
    handleCancel()
  }

  return (
    <>
      <Drawer
        title="作业属性配置"
        placement="right"
        onClose={handleCancel}
        open={isModalVisible}
        getContainer={false}
        maskStyle={{ background: 'none' }}
        footer={
          <Row justify="end">
            <Col>
              <Button onClick={handleCancel}>取消</Button>
            </Col>
            <Col>
              <span style={{ marginLeft: 8 }} />
              <Button
                onClick={() => {
                  form.submit()
                  handleCancel()
                }}
                type="primary">
                保存
              </Button>
            </Col>
          </Row>
        }>
        <Form
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 20 }}
          initialValues={{ remember: true }}
          onFinish={onFinish}
          autoComplete="off"
          form={form}>
          {work?.workType === 'SPARK_SQL'
? (
            <Form.Item
              name="engineId"
              initialValue={work.calculateId}
              label="计算引擎"
              rules={[{ required: true, message: '计算引擎不能为空' }]}>
              <Select placeholder="选择计算引擎" allowClear>
                {calculates.map((option) => (
                  <Option key={option.id} value={option.id}>
                    {option.name}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          )
: (
            <Form.Item
              name="datasourceId"
              label="数据源"
              rules={[{ required: true }]}
              initialValue={work?.datasourceId}>
              <Select placeholder="选择数据源" allowClear>
                {datasources.map((option) => (
                  <Option key={option.id} value={option.id}>
                    {option.name}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          )}
        </Form>
      </Drawer>
    </>
  )
}
