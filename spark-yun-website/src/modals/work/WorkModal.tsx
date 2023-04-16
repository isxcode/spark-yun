import React, { useEffect } from 'react'
import { Form, Input, Modal, Select } from 'antd'
import './WorkModal.less'
import { type WorkRow } from '../../types/woks/info/WorkRow'
import { type AddWorkReq } from '../../types/woks/req/AddWorkReq'
import { addWorkApi, updateWorkApi } from '../../services/works/WorksService'
import { type UpdateWorkReq } from '../../types/woks/req/UpdateWorkReq'

const { Option } = Select
export const WorkModal = (props: {
  isModalVisible: boolean
  handleCancel: () => void
  handleOk: () => void
  work?: WorkRow
  workflowId: string
}) => {
  const { isModalVisible, handleCancel, handleOk, work, workflowId } = props
  const [form] = Form.useForm()

  useEffect(() => {
    if (work?.id == null) {
      form.resetFields()
    } else {
      form.setFieldsValue(work)
    }
  }, [work])

  const addWork = (data: AddWorkReq) => {
    data.workflowId = workflowId
    addWorkApi(data).then(function () {
      handleOk()
    })
  }

  const updateWork = (data: UpdateWorkReq) => {
    updateWorkApi(data).then(function () {
      handleOk()
    })
  }

  const onFinish = (values: any) => {
    if (work?.id === undefined) {
      addWork(values)
    } else {
      values.id = work?.id
      updateWork(values)
    }
    form.resetFields()
  }

  return (
    <>
      <Modal
        title={work?.id == null ? '添加作业' : '编辑作业'}
        open={isModalVisible}
        onOk={() => {
          form.submit()
        }}
        onCancel={handleCancel}
        okText={'保存'}
        cancelText={'取消'}>
        <Form
          className={'sy-add-work-form'}
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 20 }}
          initialValues={{ remember: true }}
          onFinish={onFinish}
          form={form}>
          <Form.Item label="名称" name="name" rules={[{ required: true, message: '名称不能为空' }]}>
            <Input />
          </Form.Item>

          <Form.Item name="workType" label="类型" rules={[{ required: true, message: '类型不能为空' }]}>
            <Select placeholder="选择作业类型" allowClear>
              <Option value="EXE_JDBC">Jdbc执行作业</Option>
              <Option value="QUERY_JDBC">Jdbc查询作业</Option>
              <Option value="SPARK_SQL">SparkSql查询作业</Option>
            </Select>
          </Form.Item>

          <Form.Item label="备注" name="comment">
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
