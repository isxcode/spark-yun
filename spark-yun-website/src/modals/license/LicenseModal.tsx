import React from 'react'
import { message, Modal, Upload, UploadProps } from 'antd'
import './LicenseModal.less'
import { InboxOutlined } from '@ant-design/icons'

export const LicenseModal = (props: { isModalVisible: boolean, handleCancel: () => void, handleOk: () => void }) => {
  const { isModalVisible, handleCancel, handleOk } = props

  const { Dragger } = Upload

  const uploadProps: UploadProps = {
    name: 'license',
    multiple: true,
    headers: {
      Authorization: localStorage.getItem('Token') as string
    },
    action: process.env.API_PREFIX_URL + '/lic/uploadLicense',
    onChange(info) {
      const { status } = info.file
      let code
      if (status !== 'uploading') {
        code = JSON.parse(JSON.stringify(info.file)).response.code
      }
      if (code === '200') {
        message.success(`${info.file.name} 验证成功`)
      } else if (code === '500') {
        message.error(`${info.file.name} 验证失败`)
      }
    },
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files)
    }
  }

  return (
    <>
      <Modal
        title={'上传许可证'}
        open={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
        okText={'保存'}
        cancelText={'取消'}>
        <Dragger {...uploadProps}>
          <p className="ant-upload-drag-icon">
            <InboxOutlined />
          </p>
          <p className="ant-upload-text">单击或拖动文件到此区域进行上传</p>
          <p className="ant-upload-hint">上传企业许可证</p>
        </Dragger>
      </Modal>
    </>
  )
}
