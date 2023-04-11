import React from 'react'
import { Drawer, Form, Select } from 'antd'
import engine from '../../../pages/engine/Engine'
import { WorkRow } from '../../../types/woks/info/WorkRow'

export const DrawerWrapper = (props: {
  isModalVisible: boolean
  handleCancel: () => void
  handleOk: () => void
  work?: WorkRow
  workId: string
}) => {
  const { isModalVisible, handleCancel, handleOk, work, workId } = props

  return (
    <>
      <Drawer title="作业配置" placement="right" width={550} onClose={onClose} open={open}>
        <Form
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 18 }}
          style={{ width: 400 }}
          initialValues={{ remember: true }}
          onFinish={onFinish}
          autoComplete="off"
          form={form}>
          {work.workType === 'sparkSql' ? (
            <Form.Item name="engineId" label="计算引擎" rules={[{ required: true }]}>
              <Select defaultValue={work.engineId} placeholder="" allowClear>
                {engine.map((option) => (
                  <Option key={option.id} value={option.id}>
                    {option.name}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          ) : (
            <Form.Item name="datasourceId" label="数据源" rules={[{ required: true }]}>
              <Select defaultValue={work.datasourceId} placeholder="" allowClear>
                {datasource.map((option) => (
                  <Option key={option.id} value={option.id}>
                    {option.name}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          )}

          <Form.Item wrapperCol={{ offset: 8, span: 16 }}>
            <Button type="primary" htmlType="submit">
              保存
            </Button>
          </Form.Item>
        </Form>
      </Drawer>
    </>
  )
}
