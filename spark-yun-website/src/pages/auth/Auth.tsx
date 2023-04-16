import React from 'react'
import { Button, Form, Input, message, theme, Upload, type UploadProps } from 'antd'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'
import Dragger from 'antd/es/upload/Dragger'
import { InboxOutlined } from '@ant-design/icons'
import './Auth.less'

function Auth() {
  const { Dragger } = Upload

  const props: UploadProps = {
    name: 'file',
    multiple: false,
    onChange(info) {
      const { status } = info.file
      if (status !== 'uploading') {
        console.log(info.file, info.fileList)
      }
      if (status === 'done') {
        message.success(`${info.file.name} file uploaded successfully.`)
      } else if (status === 'error') {
        message.error(`${info.file.name} file upload failed.`)
      }
    },
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files)
    }
  }

  return (
    <div className={'sy-dragger'}>
      <Dragger {...props}>
        <p className="ant-upload-drag-icon">
          <InboxOutlined />
        </p>
        <p className="ant-upload-text">点击或拖拽文件到此处进行上传</p>
        <p className="ant-upload-hint">请上传企业许可证！</p>
      </Dragger>
    </div>
  )
}

export default Auth
