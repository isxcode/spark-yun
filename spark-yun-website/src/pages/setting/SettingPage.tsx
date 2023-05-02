import React, {useState} from 'react'
import {
  Button,
  Cascader,
  Checkbox,
  DatePicker,
  Form,
  Input,
  InputNumber,
  Radio,
  Select,
  Switch,
  TreeSelect,
  Upload
} from 'antd'
import './SettingPage.less'
import {PlusOutlined} from "@ant-design/icons";

function SettingPage() {

  const { RangePicker } = DatePicker;
  const { TextArea } = Input;

  const normFile = (e: any) => {
    if (Array.isArray(e)) {
      return e;
    }
    return e?.fileList;
  };

  return (
    <>
      <Form
        labelCol={{ span: 4 }}
        wrapperCol={{ span: 14 }}
        layout="horizontal"
        style={{ maxWidth: 600 , marginTop: '30px', marginLeft: '30px'}}
      >
        <Form.Item label="公司名称">
          <Input />
        </Form.Item>
        <Form.Item label="管理员密码">
          <Input />
        </Form.Item>
        <Form.Item label="系统启用" valuePropName="checked">
          <Switch />
        </Form.Item>
        <Form.Item label="企业logo" valuePropName="fileList" getValueFromEvent={normFile}>
          <Upload action="/upload.do" listType="picture-card">
            <div>
              <PlusOutlined />
              <div style={{ marginTop: 8 }}>Upload</div>
            </div>
          </Upload>
        </Form.Item>
        <Form.Item label="简介">
          <TextArea rows={4} />
        </Form.Item>
      </Form>
    </>
  );
}

export default SettingPage
