import React from 'react'
import {Form, Input, Modal} from 'antd'
import './AddWorkflowModal.less'
import {addWorkflowApi} from "../../services/worflow/workflowService";
import {AddWorkflowReq} from "../../services/worflow/req/AddWorkflowReq";

export const AddWorkflowModal = (props: {
  isModalVisible: boolean
  handleCancel: () => void
  queryWorkflow: any
}) => {

  const {
    isModalVisible,
    handleCancel,
    queryWorkflow
  } = props;

  const [form] = Form.useForm();

  const onFinish = (values: any) => {
    addWorkflow(values)
    form.resetFields()
  }

  const addWorkflow = (data: AddWorkflowReq) => {
    addWorkflowApi(data).then(function () {
      handleCancel()
      queryWorkflow(0,10)
    });
  };

  return (
    <>
      <Modal
        title="添加作业流"
        open={isModalVisible}
        onOk={() => { form.submit() }}
        onCancel={handleCancel}
        okText={'保存'}
        cancelText={'取消'}>
        <Form
          form={form}
          className={'sy-add-workflow-form'}
          initialValues={{remember: true}}
          onFinish={onFinish}
        >
          <Form.Item label="名称" name="name" rules={[{required: true, message: '名称不能为空'}]}>
            <Input/>
          </Form.Item>

          <Form.Item label="备注" name="comment">
            <Input.TextArea/>
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};
